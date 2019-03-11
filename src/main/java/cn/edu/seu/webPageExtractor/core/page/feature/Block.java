package cn.edu.seu.webPageExtractor.core.page.feature;

import cn.edu.seu.webPageExtractor.core.page.Node;

import java.util.List;

public class Block {
    /**
     * 分块中链接的数量
     */
    Integer linkNumber;
    /**
     * 分块中的文本密度
     */
    Float contextDensity;
    /**
     * 分块中的文本
     */
    List<String> context;
    /**
     * 分块中的领域得分
     */
    Float domainScore;
    /**
     * 分块中的节点内容
     */
    Node node;
    /**
     * 子分块
     */
    List<Block> blocks;

    /**
     * 父分块
     */
    Block parentBlock;
    /**
     * 可视
     */
    private boolean visualBlock;

    /**
     * block值
     */
    private Integer doc;

    /**
     * 可以分割
     */
    private Boolean divide;

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

    public Integer getLinkNumber() {
        return linkNumber;
    }

    public void setLinkNumber(Integer linkNumber) {
        this.linkNumber = linkNumber;
    }

    public Float getContextDensity() {
        return contextDensity;
    }

    public void setContextDensity(Float contextDensity) {
        this.contextDensity = contextDensity;
    }

    public List<String> getContext() {
        return context;
    }

    public void setContext(List<String> context) {
        this.context = context;
    }

    public Float getDomainScore() {
        return domainScore;
    }

    public void setDomainScore(Float domainScore) {
        this.domainScore = domainScore;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public Block getParentBlock() {
        return parentBlock;
    }

    public void setParentBlock(Block parentBlock) {
        this.parentBlock = parentBlock;
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

    public Boolean getDivide() {
        return divide;
    }

    public void setDivide(Boolean divide) {
        this.divide = divide;
    }

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
}
