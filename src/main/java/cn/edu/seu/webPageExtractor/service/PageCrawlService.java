package cn.edu.seu.webPageExtractor.service;



import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.ListPage;
import cn.edu.seu.webPageExtractor.core.page.Node;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * 该接口需要承担所有网页的获取，以及网页爬去的初始化
 */
public interface PageCrawlService {
    /**
     * 获取一个驱动器
     * @return
     */
    public WebDriver getDriver();

    /**
     * 获取搜索结果页的集合，需要翻页
     * @return
     */
    public List<ListPage> getListPage(String searchLink, Integer taskId, WebDriver driver,Integer pageNum);


    /**
     * 获取列表页关键词相关的A标签的链接
     * @param searchWord
     * @param block
     * @return
     */
    public void getListPageALink(String searchWord, Block block);

    /**
     * 获取详情页的信息
     * @param link
     * @param taskId
     * @param listPageId
     * @return
     */
    public DetailPage getDetailPage(String link, Integer taskId,Integer listPageId,WebDriver driver);


}
