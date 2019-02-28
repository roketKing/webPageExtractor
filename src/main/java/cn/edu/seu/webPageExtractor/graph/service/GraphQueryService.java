package cn.edu.seu.webPageExtractor.graph.service;

import cn.edu.seu.webPageExtractor.graph.Triple;
import com.franz.agraph.repository.AGRepositoryConnection;

import java.util.List;

public interface GraphQueryService {
    /**
     * 查询所有实例的数量
     *
     * @param categoryName
     * @return
     */
    public Integer countAllInstanceNum(String categoryName);

    /**
     * 查询两个领域拥有的相同实例的个数
     * @param categoryName
     * @param anotherCategoryName
     * @return
     */
    public Integer querySameInstanceBetweenCategory(String categoryName,String anotherCategoryName);

    /**
     * 查询目录下的所有属性
     * @param categoryName
     * @return
     */
    public List<String> queryAllPropertyOfCategory(String categoryName);

    /**
     * 查询一个属性所在的所有目录
     * @param propertyName
     * @return
     */
    public List<String> queryAllCategoryOfProperty(String propertyName);


    public Integer queryPropertyInstanceNumOfCategory(String propertyName,String categoryName);


}
