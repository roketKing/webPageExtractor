package cn.edu.seu.webPageExtractor.core.page.feature;

import java.util.List;

public class BlockDeNoiseFeature {
    /**
     * 分块中链接的数量
     */
    private Integer linkNumber;
    /**
     * 分块中的文本密度
     */
    private Float contextDensity;
    /**
     * 分块中的文本
     */
    private List<Context> context;

    /**
     * 分块中文本的数量
     */
    private Integer contextStrLength;
    /**
     * 分块中的领域得分
     */
    private Float domainScore;

    /**
     * 降噪得分
     */
    private Float scoreResult;

    /**
     * 列表页块文本
     */
    private List<String> blockInfos;


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

    public List<Context> getContext() {
        return context;
    }

    public void setContext(List<Context> context) {
        this.context = context;
    }

    public Integer getContextStrLength() {
        return contextStrLength;
    }

    public void setContextStrLength(Integer contextStrLength) {
        this.contextStrLength = contextStrLength;
    }

    public Float getDomainScore() {
        return domainScore;
    }

    public void setDomainScore(Float domainScore) {
        this.domainScore = domainScore;
    }

    public Float getScoreResult() {
        return scoreResult;
    }

    public void setScoreResult(Float scoreResult) {
        this.scoreResult = scoreResult;
    }

    public List<String> getBlockInfos() {
        return blockInfos;
    }

    public void setBlockInfos(List<String> blockInfos) {
        this.blockInfos = blockInfos;
    }
}
