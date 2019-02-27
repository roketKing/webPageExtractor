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
     * 查询一个目录中所有属性和属性值
     *
     * @param categoryName
     * @return
     */
    public List<Triple> queryAllPPVofCategory(String categoryName);

    /**
     * 查询一个属性的所有实例和目录
     *
     * @param proName
     * @return
     */
    public List<Triple> queryAllInstanceAndCategoryOfProperty(String proName);

    /**
     * 查询一个属性值的所有实例和目录
     * @param pro
     * @return
     */
    public List<Triple> queryAllInstanceAndCategoryOfPV(String proVName);

    /**
     * 查询两个领域拥有的相同实例的个数
     * @param categoryName
     * @param anotherCategoryName
     * @return
     */
    public Integer querySameInstanceBetweenCategory(String categoryName,String anotherCategoryName);


}
