package cn.edu.seu.webPageExtractor.service.impl;

import cn.edu.seu.webPageExtractor.controller.dto.TaskInfoDto;
import cn.edu.seu.webPageExtractor.core.ExtraResultInfo;
import cn.edu.seu.webPageExtractor.core.TaskInfo;
import cn.edu.seu.webPageExtractor.core.page.ListPage;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import cn.edu.seu.webPageExtractor.service.PageExtraService;
import cn.edu.seu.webPageExtractor.service.manage.ExtraResultManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PageExtraServiceImpl implements PageExtraService {
    @Autowired
    private ExtraResultManager extraResultManager;
    @Override
    public void listPageExtra(List<Block> correctBlock, TaskInfoDto taskInfoDto) {
        List<ExtraResultInfo> infos = new ArrayList<>();

        for (Block block : correctBlock) {
            List<String> contexts = block.getContext();
            StringBuilder temp =new StringBuilder();
            contexts.forEach(con->{
                temp.append(con);
                temp.append("\n");
            });
            ExtraResultInfo extraResultInfo = new ExtraResultInfo();
            extraResultInfo.setTaskId(taskInfoDto.getId().toString());
            extraResultInfo.setContext(temp.toString());
            extraResultInfo.setTaskDomain(taskInfoDto.getDomain());
            infos.add(extraResultInfo);
        }

        extraResultManager.saveResult(infos);
    }

    @Override
    public void detailPageExtra(Block block,TaskInfoDto taskInfoDto) {
        List<ExtraResultInfo> infos = new ArrayList<>();
        List<String> context = block.getContext();
        StringBuilder contextStr = new StringBuilder();
        for (String temp : context) {
            contextStr.append(temp);
            contextStr.append("\n");
        }

        ExtraResultInfo extraResultInfo = new ExtraResultInfo();
        extraResultInfo.setTaskId(taskInfoDto.getId().toString());
        extraResultInfo.setContext(contextStr.toString());
        extraResultInfo.setTaskDomain(taskInfoDto.getDomain());
        infos.add(extraResultInfo);

        extraResultManager.saveResult(infos);
    }
}
