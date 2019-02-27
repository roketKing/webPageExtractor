package cn.edu.seu.webPageExtractor.service.manage;

import cn.edu.seu.webPageExtractor.core.GraphScoreInfo;
import cn.edu.seu.webPageExtractor.service.repository.GraphScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GraphScoreManager {
    @Autowired
    private GraphScoreRepository graphScoreRepository;

    /**
     * 更新领域值
     * @param graphScoreInfo
     * @return
     */
    public GraphScoreInfo saveAndUpdateGraphScoreInfo(GraphScoreInfo graphScoreInfo) {
        try {
            return graphScoreRepository.save(graphScoreInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过领域和属性查找分值
     * @param graphScoreInfo
     * @return
     */
    public GraphScoreInfo queryScoreByDomainAndProperty(GraphScoreInfo graphScoreInfo) {
        try {
           return   graphScoreRepository.findGraphScoreInfoByDomainAndProperty(graphScoreInfo.getProperty(),
                    graphScoreInfo.getDomain());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过领域和属性值查找分值
     * @param graphScoreInfo
     * @return
     */
    public GraphScoreInfo queryScoreByDomainAndPropertyValue(GraphScoreInfo graphScoreInfo) {
        try {
            return   graphScoreRepository.findGraphScoreInfoByDomainAndPropertyValue(graphScoreInfo.getProperty(),
                    graphScoreInfo.getDomain());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
