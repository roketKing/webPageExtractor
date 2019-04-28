package cn.edu.seu.webPageExtractor.service.impl;

import cn.edu.seu.webPageExtractor.controller.dto.TaskInfoDto;
import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.ListPage;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import cn.edu.seu.webPageExtractor.core.page.feature.Context;
import cn.edu.seu.webPageExtractor.graph.service.GraphScoreService;
import cn.edu.seu.webPageExtractor.service.PageCrawlService;
import cn.edu.seu.webPageExtractor.service.PageExtraService;
import cn.edu.seu.webPageExtractor.service.PageFeatureGenerateService;
import cn.edu.seu.webPageExtractor.util.PageDivideRuleUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PageFeatureGenerateServiceImpl implements PageFeatureGenerateService {
    @Autowired
    private GraphScoreService graphScoreService;
    @Autowired
    private PageCrawlService pageCrawlService;
    @Autowired
    private PageExtraService pageExtraService;
    private Integer linkNum = 0;

    @Override
    public void listPageFeatureGenerate(ListPage listPage, TaskInfoDto taskInfoDto) {
        WebDriver driver = pageCrawlService.getDriver();
        List<Block> childBlocks = listPage.getChildBlocks();
        for (Block block : childBlocks) {
            //获取详情页链接
            pageCrawlService.getListPageALink(taskInfoDto.getKeyword(), block);
            //获取块内文本
            List<String> blockInfos = new ArrayList<>();
            inLineNodeExtra(block.getNode().getElement(), blockInfos);
            block.getBlockDeNoiseFeature().setBlockInfos(blockInfos);
        }
        for (Block block : childBlocks) {
            String link = block.getNode().getLink();
            if (link != null) {
                //获取详情页特征,每隔1s获取
                DetailPage detailPage = pageCrawlService.getDetailPage(link, taskInfoDto.getId(), listPage.getId(), driver);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<Context> contexts = getSpecialTagContextFeature(detailPage);
                block.getBlockDeNoiseFeature().setContext(contexts);
                Float contextDomainScore = graphScoreService.contextDomainScoreCalculate(contexts, taskInfoDto.getDomain());
                block.getBlockDeNoiseFeature().setDomainScore(contextDomainScore);
            }
        }
    }

    @Override
    public void detailPageFeatureGenerate(DetailPage detailPage, String domainName) {
        List<Block> blocks = detailPage.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            long start = System.currentTimeMillis();
            getFeatureFromBlock(blocks.get(i), domainName);
            System.out.println("已经进行到" + i + "花费了" + (System.currentTimeMillis() - start));
        }
    }

    /**
     * 获取特殊标签的文本
     *
     * @param detailPage
     * @return
     */
    public List<Context> getSpecialTagContextFeature(DetailPage detailPage) {
        WebElement parent = detailPage.getBlock().getNode().getElement();
        List<Context> context = new ArrayList<>();
        String innerHTML = parent.getAttribute("innerHTML");
        Document document = Jsoup.parse(innerHTML);
        Element detailBody = document.body();
        Elements specialElements = detailBody.getElementsByTag("table");
        specialElements.addAll(detailBody.getElementsByTag("ul"));
        specialElements.addAll(detailBody.getElementsByTag("ol"));
        specialElements.addAll(detailBody.getElementsByTag("dl"));
        for (Element specialEle : specialElements) {
            loopElementGetSpecialContext(specialEle, context);
        }
        return contextDeal(context);
    }

    /**
     * 获取的文本进行合并
     *
     * @param contexts
     * @return
     */
    private List<Context> contextDeal(List<Context> contexts) {
        Set<Context> result = new HashSet<>();
        for (Context context : contexts) {
            if (context.getStr().trim().length() > 1) {
                result.add(context);
            }
        }
        return new ArrayList<>(result);
    }

    /**
     * 遍历获取特殊标签的文本
     *
     * @param specialEle
     * @param contexts
     */
    private void loopElementGetSpecialContext(Element specialEle, List<Context> contexts) {
        List<Node> nodes = specialEle.childNodes();
        if (nodes.size() == 0) {
            return;
        }
        for (Node node : nodes) {
            if (node.getClass().getName().contains("TextNode")) {
                String tempText = node.toString();
                tempText = tempText.replaceAll(" ", "");
                if (!tempText.isEmpty()) {
                    if (node.nodeName().equals("#text")) {
                        String htmlTag = node.parentNode().nodeName();
                        contexts.add(new Context(tempText, htmlTag));
                    } else {
                        contexts.add(new Context(tempText, node.nodeName()));
                    }
                }
                continue;
            }
            if (node.getClass().getName().contains("Comment") || node.getClass().getName().contains("DataNode")) {
                continue;
            }
            if (node.nodeName().equals("a") && node.hasAttr("href")) {
                String href = node.attr("href");
                if (!href.contains("java"))
                    linkNum++;

            }
            loopElementGetSpecialContext((Element) node, contexts);
        }
    }

    /**
     * 从分割好的Block获取特征，领域得分，链接得分，文本密度得分
     *
     * @param block
     * @param domainInfo
     */
    private void getFeatureFromBlock(Block block, String domainInfo) {
        //初始化
        linkNum = 0;
        String blockText = block.getNode().getElement().getAttribute("innerText");
        if (blockText != null && !blockText.replace(" ", "").equals("")) {
            getBlockContextAndContextDensity(block);
            //计算领域得分
            Float domainScore = graphScoreService.contextDomainScoreCalculate(block.getBlockDeNoiseFeature().getContext(), domainInfo);
            block.getBlockDeNoiseFeature().setDomainScore(domainScore);
        } else {
            block.getBlockDeNoiseFeature().setContextStrLength(0);
            block.getBlockDeNoiseFeature().setContextDensity(0.0f);
            block.getBlockDeNoiseFeature().setDomainScore(0.0f);
            block.getBlockDeNoiseFeature().setLinkNumber(0);
        }
    }


    /**
     * 获取一个网页分块的文本长度，文本内容，链接数量，文本密度
     *
     * @param block
     */
    private void getBlockContextAndContextDensity(Block block) {
        WebElement blockBody = block.getNode().getElement();
        String outerHTML = blockBody.getAttribute("outerHTML");


        Document document = Jsoup.parse(outerHTML);
        Element blockElement = document.body();
        outerHTML = blockElement.outerHtml();
        Integer codeNum = outerHTML.length() - outerHTML.replace("\n", "").length() + 1;
        Integer blockSize = blockBody.getSize().getHeight() * blockBody.getSize().getWidth();

        List<Context> contexts = new ArrayList<>();
        loopElementGetSpecialContext(blockElement, contexts);
        Integer contextNum = 0;
        for (Context context : contexts) {
            contextNum = contextNum + context.getStr().length();
        }
        block.getBlockDeNoiseFeature().setContextStrLength(contextNum);
        block.getBlockDeNoiseFeature().setContext(contexts);
        block.getBlockDeNoiseFeature().setLinkNumber(linkNum);
        block.getBlockDeNoiseFeature().setContextDensity((float) contextNum / blockSize);
    }

    private void inLineNodeExtra(WebElement element, List<String> blockInfos) {
        List<WebElement> childs = element.findElements(By.xpath("./*"));
        boolean flag = false;
        for (WebElement tempEle : childs) {
           flag =PageDivideRuleUtil.isInLineNode(tempEle);
        }
        if (flag){
            String temp = element.getText();
            if (temp.length()>=1){
                blockInfos.add(temp);
            }
            return;
        }
        for (WebElement tempEle : childs) {
            inLineNodeExtra(tempEle, blockInfos);
        }
    }
}
