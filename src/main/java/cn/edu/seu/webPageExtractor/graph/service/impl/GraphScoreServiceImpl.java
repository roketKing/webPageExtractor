package cn.edu.seu.webPageExtractor.graph.service.impl;

import cn.edu.seu.webPageExtractor.graph.service.GraphQueryService;
import cn.edu.seu.webPageExtractor.graph.service.GraphScoreService;
import cn.edu.seu.webPageExtractor.service.repository.GraphScoreRepository;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.IndexAnalysis;
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
    @Autowired
    private GraphScoreRepository graphScoreRepository;

    @Override
    public void scoreCalculate(String categoryName) {
        Integer domainInstanceNum = graphQueryService.countAllInstanceNum(categoryName);

        List<String> properties = graphQueryService.queryAllPropertyOfCategory(categoryName);

        for (int i = 0; i < properties.size(); i++) {
            String propertyName = properties.get(i);
            String tempPropertyName = propertyName.replace(" ", "");
            if (tempPropertyName.length() != 1) {
                try {
                    graphScoreCalculateService.ppvScoreCalculate(categoryName, propertyName, domainInstanceNum, true);
                    if (i % 100 == 0) {
                        logger.info("已经进行到了" + i);
                    }
                } catch (TaskRejectedException e) {
                    try {
                        Thread.sleep(1000);
                        i = i - 1;
                        logger.info("属性:" + propertyName);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public Float contextDomainScoreCalculateWithSegWord(List<String> contexts, String domainName) {
        Float sumResult = 0.0f;
        Integer scoreWordNum = 0;
        for (String context : contexts) {
            //进行分词，对每个词进行搜索
            Result segResult = IndexAnalysis.parse(context);
            Float qResult = 0.0f;
            for (Term term : segResult.getTerms()) {
                String termName = term.getName().replace(domainName, "");
                if (termName.length() > 1 && !term.getNatureStr().equals("m")) {
                    Float tempResult = graphScoreCalculateService.calculateWordDomainScore(domainName, termName);
                    if (tempResult > 0) {
                        scoreWordNum++;
                        qResult = qResult + tempResult;
                    }
                }
            }
            if (qResult > 0.01)
                sumResult = sumResult + qResult;
            logger.info("context is " + context + " score is " + qResult);
        }

        return scoreWordNum == 0 ? 0 : sumResult / scoreWordNum;
    }

    @Override
    public Float contextDomainScoreCalculate(List<String> contexts, String domainName) {
        Float sumResult = 0.0f;
        for (String context : contexts) {
            context = context.replace(domainName, "");
            if (!context.contains("div")) {
                Float qResult = graphScoreCalculateService.calculateWordDomainScore(domainName, context);
                sumResult = sumResult + qResult;
                logger.info("context is " + context + " score is " + qResult);
            }
        }

        return sumResult;
    }
}
