package cn.edu.seu.webPageExtractor.service.repository;

import cn.edu.seu.webPageExtractor.core.Triple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.concurrent.Future;

public interface TripleRepository extends CrudRepository<Triple, Integer> {

    List<Triple> findTriplesByObject(String object);

    List<Triple> findTriplesBySubject(String subject);

    List<Triple> findTriplesByPredict(String predict);

    Integer countTriplesByObject(String object);

    /**
     * 一个分类的所有属性
     *
     * @param categoryName
     * @return
     */
    @Query(value = "select resource.predict as proName from (select subject,object  from category where object =?1 ) as s\n" +
            "  left join infobox as resource on s.subject=resource.subject\n" +
            "where resource.predict like '%property%' group by proName order by null ;", nativeQuery = true)
    List<Object> findAllPropertyByCategory(String categoryName);

    /**
     * 一个分类下的所有属性值
     *
     * @param categoryName
     * @return
     */
    @Query(value = "select resource.object as proValue from (select subject,object  from category where object =?1 ) as s\n" +
            "  left join infobox as resource on s.subject=resource.subject\n" +
            "where resource.predict like '%property%' group by proValue;", nativeQuery = true)
    List<Object> findAllPropertyValueByCategory(String categoryName);


    /**
     * 一个属性对应的所有分类
     *
     * @param propertyName
     * @return
     */
    @Query(value = "select resource.subject as resName , cate.object as cateName from\n" +
            "  (select subject from infobox where predict = ?1) as resource\n" +
            "left join category as cate on resource.subject = cate.subject\n" +
            "where resource.subject is not null and cate.subject is not null ;", nativeQuery = true)
    List<Object> findAllCategoryByProperty(String propertyName);

    /**
     * 一个属性值对应的所有分类
     *
     * @param propertyValue
     * @return
     */
    @Query(value = "select resource.subject as resName , cate.object as cateName from\n" +
            "  (select subject from infobox where object = ?1) as resource\n" +
            "left join category as cate on resource.subject = cate.subject\n" +
            "where resource.subject is not null and cate.subject is not null ;", nativeQuery = true)
    List<Object> findAllCategoryByPropertyValue(String propertyValue);


    /**
     * 两个目录是不是父母目录
     *
     * @param c1
     * @param c2
     * @return
     */
    @Query(value = "select count(*) from (" +
            "select subject as subNum from category " +
            "where object=?1" +
            "      or object =?2" +
            "group by subject having count(*)>=2 order by null )as result;", nativeQuery = true)
    Object isParentCategoryNum(String c1, String c2);

    @Query(value = "select distinct subject from category_spo order by subject desc limit ?1,?2 ", nativeQuery = true)
    List<String> findAllResourceUri(Integer pageNum, Integer pageSize);

    @Query(value = "select predict,object from " +
            "category_spo_4 where predict like '%property%' and subject=?1 ;", nativeQuery = true)
    List<Object> findResourceProperty(String resourceId);


    @Query(value = "SELECT object from graph.category_spo where subject =?1 ;", nativeQuery = true)
    List<String> findAllCategoryResource(String resourceId);

}
