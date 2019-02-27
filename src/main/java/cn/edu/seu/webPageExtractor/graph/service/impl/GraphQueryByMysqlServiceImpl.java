package cn.edu.seu.webPageExtractor.graph.service.impl;

import cn.edu.seu.webPageExtractor.graph.Triple;
import cn.edu.seu.webPageExtractor.graph.service.GraphQueryService;
import cn.edu.seu.webPageExtractor.graph.util.QueryGenerateUtil;
import cn.edu.seu.webPageExtractor.service.repository.TripleRepository;
import cn.edu.seu.webPageExtractor.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class GraphQueryByMysqlServiceImpl implements GraphQueryService {
    @Autowired
    TripleRepository tripleRepository;

    @Override
    public Integer countAllInstanceNum(String categoryName) {
        //生成uri
        String categoryUri = QueryGenerateUtil.categoryUriGenerator(categoryName,"baidubaike");
        //查询
        return tripleRepository.countTriplesByObject(categoryUri);
    }

    @Override
    public List<Triple> queryAllPPVofCategory(String categoryName) {

        return null;
    }

    @Override
    public List<Triple> queryAllInstanceAndCategoryOfProperty(String proName) {
        return null;
    }

    @Override
    public List<Triple> queryAllInstanceAndCategoryOfPV(String proVName) {
        return null;
    }

    @Override
    public Integer querySameInstanceBetweenCategory(String categoryName, String anotherCategoryName) {
        return null;
    }
}
