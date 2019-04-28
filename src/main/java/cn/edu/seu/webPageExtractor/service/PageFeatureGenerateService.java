package cn.edu.seu.webPageExtractor.service;

import cn.edu.seu.webPageExtractor.controller.dto.TaskInfoDto;
import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.ListPage;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import cn.edu.seu.webPageExtractor.core.page.feature.Context;
import org.openqa.selenium.WebDriver;

import java.util.List;

public interface PageFeatureGenerateService {
    /**
     * 获取列表页的特征
     * @param listPage
     * @param taskInfoDto
     */
    public void listPageFeatureGenerate(ListPage listPage, TaskInfoDto taskInfoDto);


    /**
     * 详情页特征获取
     * @param detailPage
     */
    public void detailPageFeatureGenerate(DetailPage detailPage, String domainName);

    List<Context> getSpecialTagContextFeature(DetailPage detailPage);


}
