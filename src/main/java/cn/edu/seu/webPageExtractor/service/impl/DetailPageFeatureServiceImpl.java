package cn.edu.seu.webPageExtractor.service.impl;

import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import cn.edu.seu.webPageExtractor.service.DetailPageFeatureService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DetailPageFeatureServiceImpl implements DetailPageFeatureService {
    private Integer linkNum;
    private Integer codeNum;

    @Override
    public List<String> getSpecialTagContextFeature(DetailPage detailPage) {
        WebElement parent = detailPage.getNode().getElement();
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
        return context;
    }

    /**
     * 遍历获取特殊标签的文本
     *
     * @param parent
     * @param context
     */
    private void loopElementGetSpecialContext(Element specialEle, List<String> contexts) {
        List<Node> nodes = specialEle.childNodes();
        if (nodes.size() == 0) {
            return;
        }
        for (Node node : nodes) {
            if (node.getClass().getName().contains("TextNode")) {
                String tempText = node.toString();
                tempText = tempText.replaceAll(" ","");
                if (!tempText.isEmpty()) {
                    contexts.add(specialEle.text());
                }
                continue;
            }
            if (node.getClass().getName().contains("Comment")||node.getClass().getName().contains("DataNode")) {
                continue;
            }
            if (node.nodeName().equals("a")) {
                linkNum++;
            }
            loopElementGetSpecialContext((Element) node, contexts);
        }
    }

    @Override
    public List<Block> getBlockFromDetailPage(DetailPage detailPage) {
        return null;
    }

    private void getBlockFeatureFromBlock(Block block) {
        //初始化
        linkNum = 0;
        codeNum = 0;
        if (block != null) {
            getBlockContextAndContextDensity(block);
            //计算领域得分
            // block.setDomainScore();
        }
    }

    private void getBlockContextAndContextDensity(Block block) {
        WebElement blockBody = block.getNode().getElement();
        String innerHTML = blockBody.getAttribute("innerHTML");

        Integer codeNum = innerHTML.length() - innerHTML.replace("\n","").length();


        Document document = Jsoup.parse(innerHTML);
        Element blockElement = document.body();

        List<String> contexts = new ArrayList<>();
        loopElementGetSpecialContext(blockElement,contexts);
        Integer contextNum = 0;
        for (String  context : contexts) {
            contextNum = contextNum + context.length();
        }
        block.setContext(contexts);
        block.setLinkNumber(linkNum);
        block.setContextDensity((float) contextNum / codeNum);
    }

    /**
     * 计算一个Block中所有的节点的数量
     * 所有A标签的节点数量
     *
     * @param body
     */


    private void loopBlock(Node body) {
        List<Node> childs = body.childNodes();
        if (childs.size() == 0) {
            return;
        }
        for (Node node : childs) {
            if (node.nodeName().equals("A")) {
                linkNum++;
            }
            codeNum++;
            loopBlock(node);
        }
    }
}
