package cn.edu.seu.webPageExtractor.graph.service;

import java.util.List;

public interface GraphScoreService {
    public void scoreCalculate(String categoryName);

    public Float contextDomainScoreCalculate(List<String> context,String domainName);

}
