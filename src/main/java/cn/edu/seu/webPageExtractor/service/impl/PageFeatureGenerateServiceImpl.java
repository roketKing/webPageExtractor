package cn.edu.seu.webPageExtractor.service.impl;

import cn.edu.seu.webPageExtractor.controller.dto.TaskInfoDto;
import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.ListPage;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import cn.edu.seu.webPageExtractor.service.DetailPageFeatureService;
import cn.edu.seu.webPageExtractor.service.PageFeatureGenerateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PageFeatureGenerateServiceImpl implements PageFeatureGenerateService {
    @Autowired
    private DetailPageFeatureService detailPageFeatureService;
    @Override
    public void listPageFeatureGenerate(ListPage listPage) {

    }

    @Override
    public void detailPageFeatureGenerate(DetailPage detailPage, String domainName) {
        List<Block> blocks = detailPage.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            long start = System.currentTimeMillis();
            detailPageFeatureService.getFeatureFromBlock(blocks.get(i), domainName);
            System.out.println("已经进行到" + i + "花费了" + (System.currentTimeMillis() - start));
        }
    }
}
