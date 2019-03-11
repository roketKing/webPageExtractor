package cn.edu.seu.webPageExtractor.graph.service;

import cn.edu.seu.webPageExtractor.graph.core.Resource;

import java.util.List;
import java.util.Map;

public interface GraphSearchService {
    /**
     * 通过es来搜索与词汇相关的document
     * @param scriptParams
     * @return
     */
    public String searchByWord(Map<String,Object> scriptParams);

}
