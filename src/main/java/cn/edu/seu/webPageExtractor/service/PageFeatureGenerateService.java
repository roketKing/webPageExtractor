package cn.edu.seu.webPageExtractor.service;

import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.ListPage;

public interface PageFeatureGenerateService {
    /**
     * 列表页特征获取
     * @param listPage
     */
    public void listPageFeatureGenerate(ListPage listPage);

    /**
     * 详情页特征获取
     * @param detailPage
     */
    public void detailPageFeatureGenerate(DetailPage detailPage, String domainName);
}
