package cn.edu.seu.webPageExtractor.service.repository;

import cn.edu.seu.webPageExtractor.core.GraphScoreInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GraphScoreRepository extends CrudRepository<GraphScoreInfo,Integer> {
    /**
     * 通过领域来查询所有的属性及领域分数
     * @param domain
     * @return
     */
    List<GraphScoreInfo> findGraphScoreInfosByDomain(String domain);

    /**
     * 通过属性查询分值
     * @param property
     * @return
     */
    GraphScoreInfo findGraphScoreInfoByDomainAndProperty(String domain,String property);

    /**
     * 通过属性值查询分值
     * @param pv
     * @return
     */
    GraphScoreInfo findGraphScoreInfoByDomainAndPropertyValue(String pv,String domain);

}
