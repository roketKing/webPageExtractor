package cn.edu.seu.webPageExtractor.service;

import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.ListPage;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;

import java.util.List;

public interface PageDivideService {

    /**
     * 获取搜索结果页的分块结果
     * @param listPage
     */
    public void listPageDivide(ListPage listPage);

    /**
     * 获取详情页的分块结果
     * @param detailPage
     */
    public void detailPageDivide(DetailPage detailPage);

}
