package cn.edu.seu.webPageExtractor.service.impl;

import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.ListPage;
import cn.edu.seu.webPageExtractor.core.page.Node;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import cn.edu.seu.webPageExtractor.service.PageDivideService;
import cn.edu.seu.webPageExtractor.util.PageDivideRuleUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PageDivideServiceImpl implements PageDivideService {
    private List<Block> notDividedBlock = new ArrayList<>();

    /**
     * 获取列表页的功能块
     * @param listPage
     */
    @Override
    public void listPageDivide(ListPage listPage) {
        WebElement body = listPage.getBlock().getNode().getElement();
        List<WebElement> childs = body.findElements(By.xpath("./*"));

        Map<Integer, Integer> eleSizeMap = new HashMap<>();
        Set<Integer> sizeSet = new HashSet<>();
        for (int i = 0; i < childs.size(); i++) {
            Dimension size = childs.get(i).getSize();
            Integer sizeResult = size.getHeight() * size.getWidth();

            eleSizeMap.put(i, sizeResult);
            sizeSet.add(sizeResult);
        }
        //只有一个大小则返回，有多个大小的取最大区间继续分割
        if (sizeSet.size() == 1 && childs.size()>1) {
            Block block = new Block();
            Node node = new Node();
            node.setElement(body);
            block.setNode(node);
            listPage.setBlock(block);
            List<Block> childBlocks = new ArrayList<>();
            for (WebElement child : childs) {
                Block tempBlock = new Block();
                Node tempNode = new Node();
                tempNode.setElement(child);
                tempBlock.setNode(tempNode);
                childBlocks.add(tempBlock);
            }
            listPage.setChildBlocks(childBlocks);
            return;
        }

        List<Map.Entry<Integer, Integer>> eleSizeMapList = new ArrayList<>(eleSizeMap.entrySet());
        eleSizeMapList.sort(new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        WebElement bigElement = childs.get(eleSizeMapList.get(0).getKey());
        listPage.getBlock().getNode().setElement(bigElement);

        listPageDivide(listPage);

    }

    @Override
    public void detailPageDivide(DetailPage detailPage) {
        //获取body
        Block block = detailPage.getBlock();
        //对body进行分块
        blockDivide(block);
        //分块结果回传
        detailPage.setBlocks(notDividedBlock);
    }


    private void blockDivide(Block block) {
        //获取当前节点信息
        getBlockFeature(block);
        //获取孩子节点
        WebElement currentEle = block.getNode().getElement();
        List<WebElement> childElements = currentEle.findElements(By.xpath("./*"));
        if (block.getBlocks() == null) {
            List<Block> blockList = new ArrayList<>();
            block.setBlocks(blockList);
        }
        //获取孩子节点的信息
        for (WebElement childElement : childElements) {
            if (childElement.getTagName().toLowerCase().equals("script")
                    || childElement.getTagName().toLowerCase().equals("style")) {
                continue;
            }
            Block tempBlock = new Block();
            tempBlock.setNode(new Node(null, childElement));
            tempBlock.setParentBlock(block);
            getBlockFeature(tempBlock);
            block.getBlocks().add(tempBlock);
        }
        //判断是否需要分割
        applyVipsRules(block, 140000);

        if (block.getBlockDivideFeature().getDivide()) {
            //继续获取其子节点,并分割
            for (Block childBlock : block.getBlocks()) {
                blockDivide(childBlock);
            }
        } else {
            notDividedBlock.add(block);
        }
    }

    private void getBlockFeature(Block block) {
        WebElement currentEle = block.getNode().getElement();

        if (PageDivideRuleUtil.isInLineNode(currentEle)) {
            block.getBlockDivideFeature().setInlineNode(true);
            block.getBlockDivideFeature().setLineBreakNode(false);
        } else {
            block.getBlockDivideFeature().setLineBreakNode(true);
            block.getBlockDivideFeature().setInlineNode(false);
        }
        block.getBlockDivideFeature().setTextNode(PageDivideRuleUtil.isTextNode(currentEle));
        block.getBlockDivideFeature().setValidnode(PageDivideRuleUtil.isValidNode(currentEle));
        block.getBlockDivideFeature().setVirtualTextNode(PageDivideRuleUtil.isVirtualTextNode(currentEle));
    }


    private boolean applyVipsRules(Block block, Integer threshold) {
        boolean retVal = false;

        //System.err.println("Applying VIPS rules on " + node.getNode().getNodeName() + " node");
        WebElement currentElement = block.getNode().getElement();
        String elementTagName = currentElement.getTagName();
        if (block.getBlockDivideFeature().getInlineNode()) {
            retVal = PageDivideRuleUtil.applyInlineTextNodeVipsRules(block, threshold);
        } else if (elementTagName.equals("table") || elementTagName.equals("dl")) {
            retVal = PageDivideRuleUtil.applyTableNodeVipsRules(block, threshold);
        } else if (elementTagName.equals("tr") || elementTagName.equals("dt")) {
            retVal = PageDivideRuleUtil.applyTrNodeVipsRules(block, threshold);
        } else if (elementTagName.equals("td")||elementTagName.equals("dd")) {
            retVal = PageDivideRuleUtil.applyTdNodeVipsRules(block, threshold);
        } else if (elementTagName.equals("p")) {
            retVal = PageDivideRuleUtil.applyPNodeVipsRules(block, threshold);
        } else {
            retVal = PageDivideRuleUtil.applyOtherNodeVipsRules(block, threshold);
        }

        return retVal;
    }


}
