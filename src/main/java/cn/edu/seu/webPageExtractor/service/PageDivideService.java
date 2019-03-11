package cn.edu.seu.webPageExtractor.service;

import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.ListPage;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;

import java.util.List;

public interface PageDivideService {

    /**
     * 找到列表页面积最大的
     * @param listPage
     */
    public void listPageDivide(ListPage listPage);

    public void detailPageDivide(DetailPage detailPage);

    public void detailPageParser(Block block);

    public List<Block> getNotDividedBlock();

}
