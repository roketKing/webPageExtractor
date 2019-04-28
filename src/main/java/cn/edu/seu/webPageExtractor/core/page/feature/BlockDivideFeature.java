package cn.edu.seu.webPageExtractor.core.page.feature;

public class BlockDivideFeature {
    /**
     * 是否是inlineNode，修饰文本的节点
     * 例如 B
     */
    private Boolean inlineNode;

    /**
     * 是否是linebreakNode
     */
    private Boolean lineBreakNode;

    /**
     * 是否是合法节点
     */
    private Boolean validnode;

    /**
     * 是否是纯文本节点，即没有html标签修饰的文本
     */
    private Boolean textNode;

    /**
     * inlineNode中是虚拟的文本节点
     */
    private Boolean virtualTextNode;

    /**
     * 可以分割
     */
    private Boolean divide;

    /**
     * 可视
     */
    private boolean visualBlock;

    /**
     * block值
     */
    private Integer doc;


    public Boolean getInlineNode() {
        return inlineNode;
    }

    public void setInlineNode(Boolean inlineNode) {
        this.inlineNode = inlineNode;
    }

    public Boolean getLineBreakNode() {
        return lineBreakNode;
    }

    public void setLineBreakNode(Boolean lineBreakNode) {
        this.lineBreakNode = lineBreakNode;
    }

    public Boolean getValidnode() {
        return validnode;
    }

    public void setValidnode(Boolean validnode) {
        this.validnode = validnode;
    }

    public Boolean getTextNode() {
        return textNode;
    }

    public void setTextNode(Boolean textNode) {
        this.textNode = textNode;
    }

    public Boolean getVirtualTextNode() {
        return virtualTextNode;
    }

    public void setVirtualTextNode(Boolean virtualTextNode) {
        this.virtualTextNode = virtualTextNode;
    }

    public Boolean getDivide() {
        return divide;
    }

    public void setDivide(Boolean divide) {
        this.divide = divide;
    }

    public boolean isVisualBlock() {
        return visualBlock;
    }

    public void setVisualBlock(boolean visualBlock) {
        this.visualBlock = visualBlock;
    }

    public Integer getDoc() {
        return doc;
    }

    public void setDoc(Integer doc) {
        this.doc = doc;
    }
}
