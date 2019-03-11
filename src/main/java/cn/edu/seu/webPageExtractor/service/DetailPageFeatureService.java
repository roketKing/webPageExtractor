package cn.edu.seu.webPageExtractor.service;

import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import org.openqa.selenium.WebDriver;

import java.util.List;

public interface DetailPageFeatureService {
    /**
     * 获取特殊标签的文本
     * @param detailPage
     * @return
     */
    public List<String> getSpecialTagContextFeature(DetailPage detailPage);


    /**
     * 详情页分块
     * @param detailPage
     */
    public void getFeatureFromBlock(Block block,String domainInfo);



}
