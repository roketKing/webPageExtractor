package cn.edu.seu.webPageExtractor.graph.service.impl;

import cn.edu.seu.webPageExtractor.core.GraphScoreInfo;
import cn.edu.seu.webPageExtractor.graph.Triple;
import cn.edu.seu.webPageExtractor.graph.service.GraphQueryService;
import cn.edu.seu.webPageExtractor.service.manage.GraphScoreManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GraphScoreCalculateService {
    private Logger logger = LoggerFactory.getLogger(GraphScoreServiceImpl.class);

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
        List<String> categories = graphQueryService.queryAllCategoryOfProperty(propertyName);

        //拥有该属性的当前实例的个数
        Integer domainInstanceNumOfProperty = graphQueryService.queryPropertyInstanceNumOfCategory(propertyName,categoryName);
        //拥有相同属性的领域个数
        Integer shareDomainNum = 0;

        logger.info("开始对属性所在的领域进行分类" + propertyName);
        for (String category : categories) {
            //不属于当前指定的领域
            if (!category.equals(categoryName)) {
                if (!isParentCategory(category, categoryName)) {
                    shareDomainNum++;
                }
            }
        }

        logger.info("开始计算属性所在领域的得分" + propertyName);
        Float domainScore = ( float) domainInstanceNumOfProperty /domainInstanceNum;
        Float shareScore = (float) Math.log((GRAPH_DOMAIN_NUM + 1) / (shareDomainNum + 1));

        GraphScoreInfo graphScoreInfo = new GraphScoreInfo();
        graphScoreInfo.setTime(new Date());
        graphScoreInfo.setDomain(categoryName);
        if (isProperty) {
            graphScoreInfo.setProperty(propertyName);
        } else {
            graphScoreInfo.setPropertyValue(propertyName);
        }
        Float calculateResult = domainScore * shareScore ;
        graphScoreInfo.setScore(calculateResult);
        graphScoreManager.saveAndUpdateGraphScoreInfo(graphScoreInfo);
        logger.info("category:" + categoryName + "propertyName:" + propertyName + "score" + calculateResult
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
