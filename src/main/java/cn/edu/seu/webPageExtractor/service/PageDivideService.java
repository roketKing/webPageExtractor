package cn.edu.seu.webPageExtractor.service;

import cn.edu.seu.webPageExtractor.core.page.ListPage;

public interface PageDivideService {

    /**
     * 找到列表页面积最大的
     * @param listPage
     */
    public void listPageDivide(ListPage listPage);

}
