package cn.edu.seu.webPageExtractor.graph.service.impl;

import cn.edu.seu.webPageExtractor.core.GraphScoreInfo;
import cn.edu.seu.webPageExtractor.graph.Triple;
import cn.edu.seu.webPageExtractor.graph.service.GraphQueryService;
import cn.edu.seu.webPageExtractor.service.manage.GraphScoreManager;
import cn.edu.seu.webPageExtractor.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GraphScoreCalculateService {
    private Logger logger = LoggerFactory.getLogger(GraphScoreServiceImpl.class);
    @Qualifier("graphQueryServiceImpl")
    @Autowired
    private GraphQueryService graphQueryService;
    @Autowired
    private GraphScoreManager graphScoreManager;
    @Value("${graph.domain.num}")
    private Integer GRAPH_DOMAIN_NUM;
    @Value("${graph.parentDomain.num}")
    private Integer PARENT_DOMAIN_NUM;

    Map<String, Integer> cacheParentCategory = new HashMap<>();

    @Async
    public void ppvScoreCalculate(String categoryName, String propertyName, Integer domainInstanceNum, Boolean isProperty) {
        //获取含有该属性的所有的实例和领域
        List<Triple> result;
        if (isProperty) {
            result = graphQueryService.queryAllInstanceAndCategoryOfProperty(propertyName);
        } else {
            result = graphQueryService.queryAllInstanceAndCategoryOfPV(propertyName);
        }
        logger.info("查询属性所在的所有领域完成"+propertyName);
        //领域内实例的个数
        Integer domainNum = 0;
        //相关领域的实例个数
        Integer parentDomainInstanceNum = 0;
        //拥有相同属性的领域个数
        Integer shareDomainNum = 0;

        Map<String, List<String>> categoryResMap = new HashMap<>();
        for (Triple r : result) {
            //判断统计
            String qCategory = StringUtil.getLastSubString(r.getObject());
            String qResource = r.getPredict();
            if (categoryResMap.containsKey(qCategory)) {
                categoryResMap.get(qCategory).add(qResource);
            } else {
                categoryResMap.put(qCategory, new ArrayList<>());
                categoryResMap.get(qCategory).add(qResource);
            }
        }
        logger.info("开始对属性所在的领域进行分类"+propertyName);
        for (Map.Entry<String, List<String>> categoryResEntry : categoryResMap.entrySet()) {
            String key = categoryResEntry.getKey();
            List<String> value = categoryResEntry.getValue();

            if (key.equals(categoryName)) {
                domainNum = value.size();
            } else {
                if (isParentCategory(key, categoryName)) {
                    //parentDomainInstanceNum = parentDomainInstanceNum + value.size();
                } else {
                    shareDomainNum++;
                }
            }
        }

        logger.info("开始计算属性所在领域的得分"+propertyName);
        Float domainScore = (float) domainNum / domainInstanceNum;
        Float shareScore = (float) Math.log((GRAPH_DOMAIN_NUM + 1) / (shareDomainNum + 1));
        Float parentScore = (float) parentDomainInstanceNum;

       GraphScoreInfo graphScoreInfo = new GraphScoreInfo();
        graphScoreInfo.setTime(new Date());
        graphScoreInfo.setDomain(categoryName);
        if (isProperty) {
            graphScoreInfo.setProperty(propertyName);
        } else {
            graphScoreInfo.setPropertyValue(propertyName);
        }
        Float calculateResult = domainScore * shareScore + parentScore;
        graphScoreInfo.setScore(calculateResult);
        graphScoreManager.saveAndUpdateGraphScoreInfo(graphScoreInfo);
        logger.info("category:" + categoryName + "propertyName:" + propertyName +"score"+calculateResult
                + "ThreadName:" + Thread.currentThread().getName());
    }

    /**
     * 查询是否是一个领域，通过相同实例的个数来判断
     *
     * @param qCategory
     * @param categoryName
     * @return
     */
    private boolean isParentCategory(String qCategory, String categoryName) {
        String key = qCategory + categoryName;
        String rKey = categoryName + qCategory;
        Integer sameInstanceNum;
        if (cacheParentCategory.containsKey(key) || cacheParentCategory.containsKey(rKey)) {
            sameInstanceNum = cacheParentCategory.get(key);
            if (sameInstanceNum == null) {
                sameInstanceNum = cacheParentCategory.get(rKey);
            }
        } else {
            sameInstanceNum = graphQueryService.querySameInstanceBetweenCategory(qCategory, categoryName);
        }
        return sameInstanceNum > PARENT_DOMAIN_NUM;
    }
}
