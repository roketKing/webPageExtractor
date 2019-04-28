package cn.edu.seu.webPageExtractor.service;

import cn.edu.seu.webPageExtractor.controller.dto.TaskInfoDto;
import cn.edu.seu.webPageExtractor.core.page.ListPage;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import org.openqa.selenium.WebElement;

import java.util.List;

public interface PageExtraService {
    public void listPageExtra(List<Block> correctBlock, TaskInfoDto taskInfoDto);

    public void detailPageExtra(Block block,TaskInfoDto taskInfoDto);


}
