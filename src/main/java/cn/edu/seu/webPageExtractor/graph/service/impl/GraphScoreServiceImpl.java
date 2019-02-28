package cn.edu.seu.webPageExtractor.graph.service.impl;

import cn.edu.seu.webPageExtractor.graph.service.GraphQueryService;
import cn.edu.seu.webPageExtractor.graph.service.GraphScoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GraphScoreServiceImpl implements GraphScoreService {
    private Logger logger = LoggerFactory.getLogger(GraphScoreServiceImpl.class);

    @Autowired
    private GraphQueryService graphQueryService;
    @Autowired
    private GraphScoreCalculateService graphScoreCalculateService;

    @Override
    public void scoreCalculate(String categoryName) {
        Integer domainInstanceNum = graphQueryService.countAllInstanceNum(categoryName);

        List<String> properties = graphQueryService.queryAllPropertyOfCategory(categoryName);

        for (int i=0;i<properties.size();i++) {
            String propertyName = properties.get(i);
            String tempPropertyName = propertyName.replace(" ","");
            if (tempPropertyName.length() != 1) {
                try {
                    graphScoreCalculateService.ppvScoreCalculate(categoryName, propertyName, domainInstanceNum, true);
                    if (i%100==0){
                        logger.info("已经进行到了"+i);
                    }
                } catch (TaskRejectedException e) {
                    try {
                        Thread.sleep(1000);
                        i=i-1;
                        logger.info("属性:"+propertyName);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public Float contextDomainScoreCalculate(List<String> context) {
        return 200.f;
    }
}
