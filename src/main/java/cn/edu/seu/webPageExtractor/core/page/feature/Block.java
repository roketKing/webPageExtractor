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
}
