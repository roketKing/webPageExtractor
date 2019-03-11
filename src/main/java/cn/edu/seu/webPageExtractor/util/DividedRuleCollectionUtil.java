package cn.edu.seu.webPageExtractor.util;

import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DividedRuleCollectionUtil {

    static Logger logger = LoggerFactory.getLogger(DividedRuleCollectionUtil.class);

    /**
     * VIPS Rule One
     * <p>
     * If the DOM node is not a text node and it has no valid children
     * valid children num == 0 , then
     * this node cannot be divided and will be cut.
     *
     * @param block Input node
     * @return True, if rule is applied, otherwise false.
     */
    public static boolean ruleOne(Block block) {
        //System.err.println("Applying rule One on " + node.getNode().getNodeName() + " node");
        Boolean isApplying = false;
        if (block.getBlocks().isEmpty()) {
            return isApplying;
        }

        Integer noValidChildNum = 0;
        if (!block.getTextNode().equals(true)) {
            for (Block childBlock : block.getBlocks()) {
                if (!childBlock.getValidnode().equals(true)) {
                    noValidChildNum++;
                }
            }
        }

        if (noValidChildNum == block.getBlocks().size()) {
            block.setDivide(false);
            isApplying = true;
            logger.info("node class:" + block.getNode().getElement().getTagName() + "rule one:" + isApplying);

        }

        return isApplying;
    }


    /**
     * If the DOM node has only one valid child and the child is not a text
     * node, then divide this node
     *
     * @param block
     * @return
     */
    public static boolean ruleTwo(Block block) {
        //System.err.println("Applying rule Two on " + node.getNode().getNodeName() + " node");
        Boolean isApplying = false;
        if (block.getBlocks().isEmpty()) {
            return isApplying;
        }

        List<Block> tempBlockList = new ArrayList<>();
        for (Block childBlock : block.getBlocks()) {
            if (childBlock.getValidnode().equals(true)) {
                tempBlockList.add(block);
            }
        }

        if (tempBlockList.size() == 1) {
            Block firstBlock = tempBlockList.get(0);
            if (!firstBlock.getTextNode().equals(true)) {
                isApplying = true;
                block.setDivide(true);
                logger.info("node class:" + block.getNode().getElement().getTagName() + "rule two:" + isApplying);

            }
        }


        return isApplying;
    }

    /**
     * VIPS Rule Three
     * <p>
     * If the DOM node is the root node of the sub-DOM tree (corresponding to
     * the block), and there is only one sub DOM tree corresponding to this
     * block, divide this node.
     *
     * @param block Input node
     * @return True, if rule is applied, otherwise false.
     */
    public static boolean ruleThree(Block block) {
        //System.err.println("Applying rule Three on " + node.getNode().getNodeName() + " node");

        boolean isApplying = false;

        if (block.getBlocks().size() == 1) {
            isApplying = true;
            block.setDivide(true);
            logger.info("node class:" + block.getNode().getElement().getTagName() + "rule three:" + isApplying);

        }

        return isApplying;
    }


    /**
     * VIPS Rule Four
     * <p>
     * If all of the child nodes of the DOM node are text nodes or virtual text
     * nodes, do not divide the node. <br>
     * If the font size and font weight of all these child nodes are same, set
     * the DoC of the extracted block to 10.
     * Otherwise, set the DoC of this extracted block to 9.
     *
     * @param block Input node
     * @return True, if rule is applied, otherwise false.
     */
    public static boolean ruleFour(Block block) {
        boolean isApplying = false;
        if (block.getBlocks().isEmpty()) {
            return isApplying;
        }

        Integer textNodeSize = 0;
        Integer virtualNodeSize = 0;
        for (Block childBlock : block.getBlocks()) {
            if (childBlock.getTextNode().equals(true)) {
                textNodeSize++;
            }
            if (childBlock.getVirtualTextNode().equals(true)) {
                virtualNodeSize++;
            }
        }

        if (textNodeSize + virtualNodeSize == block.getBlocks().size()) {
            isApplying = true;
            block.setDivide(false);
            block.setVisualBlock(true);
            logger.info("node class:" + block.getNode().getElement().getTagName() + "rule four:" + isApplying);

        }


        return isApplying;
    }


    /**
     * VIPS Rule Five
     * <p>
     * If one of the child nodes of the DOM node is line-break node, then
     * divide this DOM node.
     *
     * @param block
     * @return True, if rule is applied, otherwise false.
     */
    public static boolean ruleFive(Block block) {
        boolean isApplying = false;
        if (block.getBlocks().isEmpty()) {
            return false;
        }

        for (Block childBlock : block.getBlocks()) {
            if (childBlock.getLineBreakNode().equals(true)) {
                block.setDivide(true);
                logger.info("node class:" + block.getNode().getElement().getTagName() + "rule five isApplying");
                return true;
            }
        }
        return false;
    }

    /**
     * VIPS Rule Six
     * <p>
     * If one of the child nodes of the DOM node has HTML tag &lt;hr&gt;, then
     * divide this DOM node
     *
     * @param block Input node
     * @return True, if rule is applied, otherwise false.
     */
    public static boolean ruleSix(Block block) {
        if (block.getBlocks().isEmpty()) {
            return false;
        }


        for (Block childBlock : block.getBlocks()) {
            if (childBlock.getNode().getElement().getTagName().equals("hr")) {
                block.setDivide(true);
                logger.info("node class:" + block.getNode().getElement().getTagName() + "rule six isApplying");
                return true;
            }
        }

        return false;
    }

    /**
     * VIPS rule Seven
     * If the sum of all the child nodes’ size is greater than this DOM node’s size, then divide
     * this node.
     * @param block
     * @return
     */
    public static boolean ruleSeven(Block block) {
        if (block.getBlocks().isEmpty())
            return false;
        WebElement currentElement = block.getNode().getElement();
        Integer currentBlcokSize = currentElement.getSize().getHeight() * currentElement.getSize().getWidth();
        Integer sumOfChildElement = 0;
        for (Block childBlock : block.getBlocks()) {
            WebElement childEle = childBlock.getNode().getElement();
            sumOfChildElement = sumOfChildElement + childEle.getSize().getHeight() * childEle.getSize().getWidth();
        }
        if (sumOfChildElement > currentBlcokSize){
            block.setDivide(true);
            return true;
        }
        return false;
    }

    /**
     * VIPS Rule Eight
     * <p>
     * If the background color of this node is different from one of its
     * children’s, divide this node and at the same time, the child node with
     * different background color will not be divided in this round.
     * Set the DoC value (6-8) for the child node based on the &lt;html&gt;
     * tag of the child node and the size of the child node.
     *
     * @param block Input node
     * @return True, if rule is applied, otherwise false.
     */
    public static boolean ruleEight(Block block) {
        if (block.getBlocks().isEmpty())
            return false;

        if (block.getTextNode().equals(true))
            return false;

        String nodeBgColor = block.getNode().getElement().getCssValue("background-color");

        for (Block childBlock : block.getBlocks()) {
            WebElement childEle = childBlock.getNode().getElement();
            if (!(childEle.getCssValue("background-color").equals(nodeBgColor))) {
                block.setDivide(true);
                // TODO DoC values
                childBlock.setDoc(7);
                logger.info("node class:" + block.getNode().getElement().getTagName() + "rule Eight isApplying");
                return true;
            }
        }

        return false;
    }


    /**
     * VIPS Rule Nine
     * <p>
     * If the node has at least one text node child or at least one virtual
     * text node child, and the node's relative size is smaller than
     * a threshold, then the node cannot be divided.
     * Set the DoC value (from 5-8) based on the html tag of the node.
     *
     * @param block Input node
     * @return True, if rule is applied, otherwise false.
     */
    public static boolean ruleNine(Block block, Integer threshold) {
        if (block.getBlocks().isEmpty())
            return false;

        boolean tempResult = false;

        WebElement currentEle = block.getNode().getElement();
        Integer currentElementSize = currentEle.getSize().getWidth()*currentEle.getSize().getHeight();

        for (Block childBlock : block.getBlocks()) {
           if (childBlock.getTextNode().equals(true) || childBlock.getVirtualTextNode().equals(true)) {
                if (currentElementSize <threshold){
                    block.setVisualBlock(true);
                    block.setDivide(false);
                    if (currentEle.getTagName().equals("Xdiv"))
                        block.setDoc(7);
                    else if (currentEle.getTagName().equals("code"))
                        block.setDoc(7);
                    else if (currentEle.getTagName().equals("div"))
                        block.setDoc(5);
                    else
                        block.setDoc(8);

                    logger.info("node class:" + block.getNode().getElement().getTagName() + "rule Nine isApplying");

                    return true;
                }
           }
        }
        return false;
    }

    /**
     * VIPS Rule Ten
     * <p>
     * If the child of the node with maximum size are small than
     * a threshold (relative size), do not divide this node. <br>
     * Set the DoC based on the html tag and size of this node.
     *
     * @param block Input node
     * @return True, if rule is applied, otherwise false.
     */
    public static boolean ruleTen(Block block, Integer threshold) {
        //System.err.println("Applying rule Nine on " + node.getNode().getNodeName() + " node");
        if (block.getBlocks().isEmpty())
            return false;

        int maxSize = 0;

        for (Block childBlock : block.getBlocks()) {
            WebElement childEle = childBlock.getNode().getElement();
            int childSize = childEle.getSize().getWidth() * childEle.getSize().getHeight();

            if (maxSize < childSize) {
                maxSize = childSize;
            }
        }

        if (maxSize < threshold) {
            block.setDivide(false);
            block.setVisualBlock(true);

            WebElement ele = block.getNode().getElement();
            if (ele.getTagName().equals("div")) {
                block.setDoc(7);
            } else if (ele.getTagName().equals("a")) {
                block.setDoc(11);
            } else {
                block.setDoc(8);
            }

            logger.info("node class:" + block.getNode().getElement().getTagName() + "rule Ten isApplying");

            return true;
        }

        return false;
    }

    /**
     * VIPS Rule Eleven
     * <p>
     * If previous sibling node has not been divided, do not divide this node
     *
     * @param block Input node
     * @return True, if rule is applied, otherwise false.
     */
    public static boolean ruleEleven(Block block) {
        Block parentBlock = block.getParentBlock();
        List<Block> parentChildBlocks = parentBlock.getBlocks();

        for (Block childBlock : parentChildBlocks) {
            if (!childBlock.getDivide().equals(true)) {
                block.setDivide(false);

                logger.info("node class:" + block.getNode().getElement().getTagName() + "rule Eleven isApplying");

                return true;
            }
        }

        return false;
    }

    /**
     * VIPS Rule Twelve
     * <p>
     * Divide this node.
     *
     * @param block Input node
     * @return True, if rule is applied, otherwise false.
     */
    public static boolean ruleTwelve(Block block) {
        //System.err.println("Applying rule Eleven on " + node.getNode().getNodeName() + " node");
        block.setDivide(true);

        logger.info("node class:" + block.getNode().getElement().getTagName() + "rule Twelve isApplying");

        return block.getTextNode().equals(true);
    }

    /**
     * VIPS Rule Thirteen
     * <p>
     * Do not divide this node <br>
     * Set the DoC value based on the html tag and size of this node.
     *
     * @param block Input node
     * @return True, if rule is applied, otherwise false.
     */
    public static boolean ruleThirteen(Block block) {
        //System.err.println("Applying rule Twelve on " + node.getNode().getNodeName() + " node");

        block.setDivide(false);
        block.setVisualBlock(true);

        WebElement ele = block.getNode().getElement();
        if (ele.getTagName().equals("Xdiv"))
            block.setDoc(7);
        else if (ele.getTagName().equals("li"))
            block.setDoc(8);
        else if (ele.getTagName().equals("span"))
            block.setDoc(8);
        else if (ele.getTagName().equals("sup"))
            block.setDoc(8);
        else if (ele.getTagName().equals("img"))
            block.setDoc(8);
        else
            block.setDoc(333);
        //TODO DoC Part

        logger.info("node class:" + block.getNode().getElement().getTagName() + "rule Thirteen isApplying");

        return true;
    }
}
