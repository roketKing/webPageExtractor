package cn.edu.seu.webPageExtractor.graph.service;

import cn.edu.seu.webPageExtractor.core.page.feature.Context;

import java.util.List;

public interface GraphScoreService {
    public void scoreCalculate(String categoryName);

    public Float contextDomainScoreCalculate(List<Context> context, String domainName);

//    public Float contextDomainScoreCalculateWithSegWord(List<String> contexts, String domainName);

}
