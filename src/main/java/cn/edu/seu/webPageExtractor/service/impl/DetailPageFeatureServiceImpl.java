package cn.edu.seu.webPageExtractor.service.impl;

import cn.edu.seu.webPageExtractor.controller.dto.TaskInfoDto;
import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.ListPage;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import cn.edu.seu.webPageExtractor.graph.service.GraphScoreService;
import cn.edu.seu.webPageExtractor.service.DetailPageFeatureService;
import cn.edu.seu.webPageExtractor.service.PageCrawlService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DetailPageFeatureServiceImpl implements DetailPageFeatureService {
    @Autowired
    private GraphScoreService graphScoreService;
    @Autowired
    private PageCrawlService pageCrawlService;
    private Integer linkNum = 0;

    @Override
    public List<String> getSpecialTagContextFeature(DetailPage detailPage) {
        WebElement parent = detailPage.getBlock().getNode().getElement();
        List<String> context = new ArrayList<>();
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

    private List<String> contextDeal(List<String> contexts) {
        Set<String> result = new HashSet<>();
        for (String context : contexts) {
            if (context.trim().length() != 1) {
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
    private void loopElementGetSpecialContext(Element specialEle, List<String> contexts) {
        List<Node> nodes = specialEle.childNodes();
        if (nodes.size() == 0) {
            return;
        }
        for (Node node : nodes) {
            if (node.getClass().getName().contains("TextNode")) {
                String tempText = node.toString();
                tempText = tempText.replaceAll(" ", "");
                if (!tempText.isEmpty()) {
                    contexts.add(specialEle.text());
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

    @Override
    public void getFeatureFromBlock(Block block, String domainInfo) {
        //初始化
        linkNum = 0;
        String blockText = block.getNode().getElement().getAttribute("innerText");
        if (blockText != null && !blockText.replace(" ","").equals("")) {
            getBlockContextAndContextDensity(block);
            //计算领域得分
            Float domainScore = graphScoreService.contextDomainScoreCalculate(block.getContext(), domainInfo);
            block.setDomainScore(domainScore);
        } else {
            block.setContextStrLength(0);
            block.setContextDensity(0.0f);
            block.setDomainScore(0.0f);
            block.setLinkNumber(0);
        }
    }

    @Override
    public void getListPageBlockFeature(ListPage listPage, TaskInfoDto taskInfoDto) {
        WebDriver driver = pageCrawlService.getDriver();
        List<Block> childBlocks = listPage.getChildBlocks();
        for (Block block : childBlocks) {
            //获取详情页链接
            pageCrawlService.getListPageALink(taskInfoDto.getKeyword(), block);
            String link = block.getNode().getLink();
            //获取详情页特征
            DetailPage detailPage = pageCrawlService.getDetailPage(link, taskInfoDto.getId(), listPage.getId(), driver);
            List<String> contexts = getSpecialTagContextFeature(detailPage);
            block.setContext(contexts);
            Float contextDomainScore = graphScoreService.contextDomainScoreCalculate(contexts, taskInfoDto.getDomain());
            block.setDomainScore(contextDomainScore);
        }
    }

    private void getBlockContextAndContextDensity(Block block) {
        WebElement blockBody = block.getNode().getElement();
        String outerHTML = blockBody.getAttribute("outerHTML");


        Document document = Jsoup.parse(outerHTML);
        Element blockElement = document.body();
        outerHTML = blockElement.outerHtml();
        Integer codeNum = outerHTML.length() - outerHTML.replace("\n", "").length() + 1;
        Integer blockSize =  blockBody.getSize().getHeight()*blockBody.getSize().getWidth();

        List<String> contexts = new ArrayList<>();
        loopElementGetSpecialContext(blockElement, contexts);
        Integer contextNum = 0;
        for (String context : contexts) {
            contextNum = contextNum + context.length();
        }
        block.setContextStrLength(contextNum);
        block.setContext(contexts);
        block.setLinkNumber(linkNum);
        block.setContextDensity((float) contextNum / blockSize);
    }
}
