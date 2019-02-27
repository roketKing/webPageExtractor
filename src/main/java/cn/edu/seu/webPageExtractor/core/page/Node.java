package cn.edu.seu.webPageExtractor.core.page;

import org.jsoup.nodes.Element;
import org.openqa.selenium.WebElement;


/**
 * 记录解析后的网页DOM
 */
public class Node {
    //超链接获取的网页需要填写
    private String link;
    //获取"body"的element
    private WebElement element;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public WebElement getElement() {
        return element;
    }

    public void setElement(WebElement element) {
        this.element = element;
    }
}
