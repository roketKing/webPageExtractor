package cn.edu.seu.webPageExtractor.service;

import cn.edu.seu.webPageExtractor.controller.dto.TaskInfoDto;
import cn.edu.seu.webPageExtractor.core.TaskInfo;
import cn.edu.seu.webPageExtractor.core.page.DetailPage;

import java.util.List;

public interface TaskManageService {
    public TaskInfoDto createATask(TaskInfo taskInfo);

    public boolean updateTaskState(TaskInfo taskInfo);

    public TaskInfo queryTaskById(Integer id);

    public List<TaskInfoDto> queryAllTask();

    public List<String> taskContextCalculate(List<DetailPage> detailPages);

    public void listPageExtraTask(TaskInfoDto taskInfoDto);

    public void detailPageExtraTask(TaskInfoDto taskInfoDto);

    public void domainTrainTask(TaskInfoDto taskInfoDto);

}
