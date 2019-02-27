package cn.edu.seu.webPageExtractor.graph.service.impl;

import cn.edu.seu.webPageExtractor.graph.Triple;
import cn.edu.seu.webPageExtractor.graph.service.GraphQueryService;
import cn.edu.seu.webPageExtractor.graph.service.GraphScoreService;
import cn.edu.seu.webPageExtractor.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class GraphScoreServiceImpl implements GraphScoreService {
    private Logger logger = LoggerFactory.getLogger(GraphScoreServiceImpl.class);

    @Qualifier("graphQueryServiceImpl")
    @Autowired
    private GraphQueryService graphQueryService;
    @Autowired
    private GraphScoreCalculateService graphScoreCalculateService;

    @Override
    public void scoreCalculate(String categoryName) {
        Integer domainInstanceNum = graphQueryService.countAllInstanceNum(categoryName);
        List<Triple> triples = graphQueryService.queryAllPPVofCategory(categoryName);
        Set<String> propertyNameSet = new HashSet<>();
        Set<String> propertyValueSet = new HashSet<>();
        for (Triple triple : triples) {
            String[] propertyUri = triple.getPredict().split("/");
            String propertyName = propertyUri[propertyUri.length-1];
            String pvName = triple.getObject();
            propertyNameSet.add(StringUtil.replaceSpeace(propertyName));
            propertyValueSet.add(StringUtil.replaceSpeace(pvName));
        }
        for (String propertyName:propertyNameSet){
            try {
                graphScoreCalculateService.ppvScoreCalculate(categoryName, propertyName, domainInstanceNum, true);
            }catch (TaskRejectedException e){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        for (String propertyValue : propertyValueSet){
            graphScoreCalculateService.ppvScoreCalculate(categoryName,propertyValue,domainInstanceNum,false);
        }
    }

    @Override
    public Float contextDomainScoreCalculate(List<String> context) {
        return 200.f;
    }
}
