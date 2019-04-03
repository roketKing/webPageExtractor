package cn.edu.seu.webPageExtractor.service.impl;

import cn.edu.seu.webPageExtractor.constants.TaskStateEnum;
import cn.edu.seu.webPageExtractor.controller.dto.TaskInfoDto;
import cn.edu.seu.webPageExtractor.core.ExtraResultInfo;
import cn.edu.seu.webPageExtractor.core.TaskInfo;
import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.ListPage;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import cn.edu.seu.webPageExtractor.graph.service.GraphScoreService;
import cn.edu.seu.webPageExtractor.service.*;
import cn.edu.seu.webPageExtractor.service.manage.ExtraResultManager;
import cn.edu.seu.webPageExtractor.service.repository.TaskRepository;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class TaskManageImpl implements TaskManageService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PageCrawlService pageCrawlService;

    @Autowired
    private PageDivideService pageDivideService;

    @Autowired
    private GraphScoreService graphScoreService;

    @Autowired
    private DetailPageFeatureService detailPageFeatureService;

    @Autowired
    private PageDeNoiseService pageDeNoiseService;

    @Autowired
    private PageExtraService pageExtraService;

    @Autowired
    private PageFeatureGenerateService pageFeatureGenerateService;

    @Override
    public TaskInfoDto createATask(TaskInfo taskInfo) {
        taskInfo = taskRepository.save(taskInfo);
        return transTaskInfo2Dto(taskInfo);
    }

    @Override
    public boolean updateTaskState(TaskInfo taskInfo) {
        try {
            taskRepository.save(taskInfo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public TaskInfo queryTaskById(Integer id) {
        try {
            Optional<TaskInfo> taskInfoOptional = taskRepository.findById(id);
            if (taskInfoOptional.isPresent()) {
                return taskInfoOptional.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<TaskInfoDto> queryAllTask() {
        try {
            List<TaskInfoDto> taskInfoList = new ArrayList<>();
            Iterable<TaskInfo> taskInfos = taskRepository.findAll();
            taskInfos.forEach(taskInfo -> {
                TaskInfoDto taskInfoDto = transTaskInfo2Dto(taskInfo);
                taskInfoList.add(taskInfoDto);
            });
            return taskInfoList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> taskContextCalculate(List<DetailPage> detailPages) {
        Map<String, Integer> wordCountMap = new HashMap<>();
        List<String> result = new ArrayList<>();
        for (DetailPage detailPage : detailPages) {
            List<String> contexts = detailPage.getSpeacilContext();
            contexts.forEach(context -> {
                if (wordCountMap.containsKey(context)) {
                    wordCountMap.put(context, wordCountMap.get(context) + 1);
                } else {
                    wordCountMap.put(context, 1);
                }
            });
        }
        List<Map.Entry<String, Integer>> wordCountMapEntryList = new ArrayList<>(wordCountMap.entrySet());
        wordCountMapEntryList.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue() - o2.getValue();
            }
        });

        //获取前10个作为关键词
        for (int i = 0; i < 10; i++) {
            result.add(wordCountMapEntryList.get(i).getKey());
        }
        return result;
    }

    @Override
    @Async
    public void listPageExtraTask(TaskInfoDto taskInfoDto) {
        WebDriver driver = pageCrawlService.getDriver();
        List<ListPage> listPages = pageCrawlService.getListPage(taskInfoDto.getLink(), taskInfoDto.getId(), driver, 1);

        for (ListPage listPage : listPages) {
            //分割列表页
            pageDivideService.listPageDivide(listPage);
            //获取特征
            detailPageFeatureService.getListPageBlockFeature(listPage, taskInfoDto);
            //页面降噪
            List<Block> correctBlock = pageDeNoiseService.listPageDeNoise(listPage);
            //页面抽取
            pageExtraService.listPageExtra(correctBlock, taskInfoDto);
        }
    }


    @Override
    @Async
    public void detailPageExtraTask(TaskInfoDto taskInfoDto) {
        WebDriver driver = pageCrawlService.getDriver();
        DetailPage detailPage = pageCrawlService.getDetailPage(taskInfoDto.getLink(), 11, 11, driver);
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        //分割网页
        pageDivideService.detailPageDivide(detailPage);
        //获取特征
        pageFeatureGenerateService.detailPageFeatureGenerate(detailPage, taskInfoDto.getDomain());
        //降噪
        Block block = pageDeNoiseService.detailPageDeNoise(detailPage);
        //页面抽取
        pageExtraService.detailPageExtra(block,taskInfoDto);
    }

    @Override
    @Async
    public void domainTrainTask(TaskInfoDto taskInfoDto) {
        graphScoreService.scoreCalculate(taskInfoDto.getDomain());
    }

    private TaskInfoDto transTaskInfo2Dto(TaskInfo taskInfo) {
        TaskInfoDto taskInfoDto = new TaskInfoDto();
        BeanUtils.copyProperties(taskInfo, taskInfoDto);

        DateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        taskInfoDto.setDateString(dft.format(taskInfoDto.getTime()));

        if (taskInfo.getState().equals(TaskStateEnum.NEWTASK.getState())) {
            taskInfoDto.setStateName(TaskStateEnum.NEWTASK.getName());
        } else if (taskInfo.getState().equals(TaskStateEnum.PROCESTASK.getState())) {
            taskInfoDto.setStateName(TaskStateEnum.PROCESTASK.getName());
        } else if (taskInfo.getState().equals(TaskStateEnum.FINISHTASK.getState())) {
            taskInfoDto.setStateName(TaskStateEnum.FINISHTASK.getName());
        }

        if (taskInfo.getType() == 10) {
            taskInfoDto.setTypeString("领域构建训练任务");
        } else if (taskInfo.getType() == 20) {
            taskInfoDto.setTypeString("商品搜索结果页抽取任务");
        } else if (taskInfo.getType() == 30) {
            taskInfoDto.setTypeString("商品详情页抽取任务");
        }

        return taskInfoDto;
    }
}
