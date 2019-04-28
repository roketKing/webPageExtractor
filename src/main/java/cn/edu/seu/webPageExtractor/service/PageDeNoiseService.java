package cn.edu.seu.webPageExtractor.service;

import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.ListPage;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import cn.edu.seu.webPageExtractor.core.page.feature.Context;
import org.openqa.selenium.WebElement;

import java.util.List;

public interface PageDeNoiseService {
    /**
     * 列表页降噪
     * @param listPage
     * @return
     */
    public List<Block> listPageDeNoise(ListPage listPage, List<Context> negativeContext);

    /**
     * 详情页降噪
     * @param detailPage
     * @return
     */
    public Block detailPageDeNoise(DetailPage detailPage);
}
