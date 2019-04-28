package cn.edu.seu.webPageExtractor.core.page.feature;

import cn.edu.seu.webPageExtractor.constants.ContextType;

import java.util.List;

/**
 * 文本内容
 */
public class Context {
    private String str;
    private String htmlTag;
    private ContextType type;


    public Context(String str, String htmlTag) {
        this.str = str;
        this.htmlTag = htmlTag;
    }
    public Context(){

    }


    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public String getHtmlTag() {
        return htmlTag;
    }

    public void setHtmlTag(String htmlTag) {
        this.htmlTag = htmlTag;
    }

    public ContextType getType() {
        return type;
    }

    public void setType(ContextType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Context context = (Context) o;

        return str.equals(context.str);
    }

    @Override
    public int hashCode() {
        return str.hashCode();
    }
}
