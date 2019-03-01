package cn.edu.seu.webPageExtractor.graph.service;

import cn.edu.seu.webPageExtractor.graph.core.Resource;

import java.util.List;

public interface GraphSearchService {
    /**
     * 通过es来搜索与词汇相关的document
     * @param categoryName
     * @param word
     * @return
     */
    public String searchByWord(String categoryName,String word);

}
