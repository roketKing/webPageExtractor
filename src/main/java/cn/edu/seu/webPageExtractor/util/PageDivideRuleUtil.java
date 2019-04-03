package cn.edu.seu.webPageExtractor.util;

import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PageDivideRuleUtil {
    public static boolean applyInlineTextNodeVipsRules(Block block, Integer threshold) {
        // 1 2 3 4 5 6 7 9 10 12
        if (DividedRuleCollectionUtil.ruleOne(block)
                || DividedRuleCollectionUtil.ruleTwo(block)
                || DividedRuleCollectionUtil.ruleThree(block)
                || DividedRuleCollectionUtil.ruleFour(block)
                || DividedRuleCollectionUtil.ruleFive(block)
                || DividedRuleCollectionUtil.ruleSix(block)
                || DividedRuleCollectionUtil.ruleSeven(block)
                || DividedRuleCollectionUtil.ruleNine(block, threshold)
                || DividedRuleCollectionUtil.ruleTen(block, threshold)
                || DividedRuleCollectionUtil.ruleTwelve(block)
                )
            return true;
        return false;
    }

    public static boolean applyTableNodeVipsRules(Block block, Integer threshold) {
        // 1 2 3 8 10 13
        if (DividedRuleCollectionUtil.ruleOne(block)
                || DividedRuleCollectionUtil.ruleTwo(block)
                || DividedRuleCollectionUtil.ruleThree(block)
                || DividedRuleCollectionUtil.ruleEight(block)
                || DividedRuleCollectionUtil.ruleTen(block, threshold)
                || DividedRuleCollectionUtil.ruleThirteen(block)
                )
            return true;
        return false;
    }

    public static boolean applyTrNodeVipsRules(Block block, Integer threshold) {
        // 1 2 3 7 8 10 13
        if (DividedRuleCollectionUtil.ruleOne(block)
                || DividedRuleCollectionUtil.ruleTwo(block)
                || DividedRuleCollectionUtil.ruleThree(block)
                || DividedRuleCollectionUtil.ruleSeven(block)
                || DividedRuleCollectionUtil.ruleEight(block)
                || DividedRuleCollectionUtil.ruleTen(block,threshold)
                || DividedRuleCollectionUtil.ruleThirteen(block)
                )
            return true;
        return false;
    }

    public static boolean applyTdNodeVipsRules(Block block, Integer threshold) {
        // 1 2 3 4 9 10 11 13
        if (DividedRuleCollectionUtil.ruleOne(block)
                || DividedRuleCollectionUtil.ruleTwo(block)
                || DividedRuleCollectionUtil.ruleThree(block)
                || DividedRuleCollectionUtil.ruleFour(block)
                || DividedRuleCollectionUtil.ruleNine(block, threshold)
                || DividedRuleCollectionUtil.ruleTen(block, threshold)
                || DividedRuleCollectionUtil.ruleEleven(block)
                || DividedRuleCollectionUtil.ruleThirteen(block)
                )
            return true;
        return false;
    }

    public static boolean applyPNodeVipsRules(Block block, Integer threshold) {
        // 1 2 3 4 5 6 7 9 10 12

        if (DividedRuleCollectionUtil.ruleOne(block)
                || DividedRuleCollectionUtil.ruleTwo(block)
                || DividedRuleCollectionUtil.ruleThree(block)
                || DividedRuleCollectionUtil.ruleFour(block)
                || DividedRuleCollectionUtil.ruleFive(block)
                || DividedRuleCollectionUtil.ruleSix(block)
                || DividedRuleCollectionUtil.ruleSeven(block)
                || DividedRuleCollectionUtil.ruleNine(block,threshold)
                || DividedRuleCollectionUtil.ruleTen(block,threshold)
                || DividedRuleCollectionUtil.ruleTwelve(block)
                )
            return true;
        return false;
    }

    public static boolean applyOtherNodeVipsRules(Block block, Integer threshold) {
        // 1 2 3 4 6 7 9 10 12
        if (DividedRuleCollectionUtil.ruleOne(block)
                || DividedRuleCollectionUtil.ruleTwo(block)
                || DividedRuleCollectionUtil.ruleThree(block)
                || DividedRuleCollectionUtil.ruleFour(block)
                || DividedRuleCollectionUtil.ruleSix(block)
                || DividedRuleCollectionUtil.ruleSeven(block)
                || DividedRuleCollectionUtil.ruleNine(block,threshold)
                || DividedRuleCollectionUtil.ruleTen(block,threshold)
                || DividedRuleCollectionUtil.ruleTwelve(block)

                )
            return true;
        return false;
    }


    /**
     * 是否是TextNode
     *
     * @param ele
     * @return
     */
    public static boolean isTextNode(WebElement ele) {
        if (isInLineNode(ele)){
            List<WebElement> childElements = ele.findElements(By.xpath("./*"));
            if (childElements.size()==0){
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是合法的Node
     *
     * @param ele
     * @return
     */
    public static boolean isValidNode(WebElement ele) {
        if (ele.getSize().getHeight() > 0 && ele.getSize().getWidth() > 0)
            return true;

        return false;
    }

    /**
     * 判断是否是<B>, <BIG>, <EM>, <FONT>, <I>,
     * <STRONG>, <U>为inlineNode
     * @param ele
     * @return
     */
    public static boolean isInLineNode(WebElement ele) {
        String tagName = ele.getTagName().toLowerCase();
        String[] inLineTags = {"b","big","em","font","i","strong","u",
                "span","img","input","label","small","textarea"};
        List<String> tags = Arrays.asList(inLineTags);
        if (tags.contains(tagName)){
            return true;
        }
        return false;
    }

    public static boolean isVirtualTextNode(WebElement ele) {
        if (isInLineNode(ele)) {
            List<WebElement> childElements = ele.findElements(By.xpath("./*"));
            Integer textNodeSize = 0;
            for (WebElement childEle : childElements) {
                if (isTextNode(childEle)) {
                    textNodeSize++;
                } else if (isVirtualTextNode(childEle)) {
                    textNodeSize++;
                }
            }
            if (textNodeSize == childElements.size()) {
                return true;
            }
        }
        return false;
    }

}
