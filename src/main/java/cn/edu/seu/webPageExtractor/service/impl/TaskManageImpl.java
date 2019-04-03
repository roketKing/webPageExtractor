package cn.edu.seu.webPageExtractor.service.impl;

import cn.edu.seu.webPageExtractor.constants.TaskStateEnum;
import cn.edu.seu.webPageExtractor.controller.dto.TaskInfoDto;
import cn.edu.seu.webPageExtractor.core.ExtraResultInfo;
import cn.edu.seu.webPageExtractor.core.TaskInfo;
import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.ListPage;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import cn.edu.seu.webPageExtractor.graph.service.GraphScoreService;
import cn.edu.seu.webPageExtractor.service.DetailPageFeatureService;
import cn.edu.seu.webPageExtractor.service.PageCrawlService;
import cn.edu.seu.webPageExtractor.service.PageDivideService;
import cn.edu.seu.webPageExtractor.service.TaskManageService;
import cn.edu.seu.webPageExtractor.service.manage.DetailPageInfoManager;
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
    private DetailPageInfoManager detailPageInfoManager;

    @Autowired
    private DetailPageFeatureService detailPageFeatureService;

    @Autowired
    private ExtraResultManager extraResultManager;

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
        String keyword = taskInfoDto.getKeyword();
        List<ListPage> listPages = pageCrawlService.getListPage(taskInfoDto.getLink(), taskInfoDto.getId(), driver, 1);

        List<DetailPage> correctDetailPage = new ArrayList<>();
        List<DetailPage> notCorrectDetailPages = new ArrayList<>();

        for (ListPage listPage : listPages) {
            //分割列表页
            pageDivideService.listPageDivide(listPage);
            List<Block> childBlocks = listPage.getChildBlocks();
            for (Block block : childBlocks) {
                //获取详情页链接
                pageCrawlService.getListPageALink(keyword, block);
                String link = block.getNode().getLink();
                //获取详情页特征
                DetailPage detailPage = pageCrawlService.getDetailPage(link, taskInfoDto.getId(), listPage.getId(), driver);
                List<String> contexts = detailPageFeatureService.getSpecialTagContextFeature(detailPage);
                block.setContext(contexts);
                Float contextDomainScore = graphScoreService.contextDomainScoreCalculate(contexts, taskInfoDto.getDomain());
                block.setDomainScore(contextDomainScore);
            }

            //排序，获得无噪声的block，然后获取关键词
            Map<String, Integer> keyWordMap = new HashMap<>();
            childBlocks.sort(new Comparator<Block>() {
                @Override
                public int compare(Block o1, Block o2) {
                    Float res = o1.getDomainScore() - o2.getDomainScore();
                    if (res > 0) {
                        return 1;
                    } else if (res < 0) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
            Integer endNum = childBlocks.size() - 1;
            if (childBlocks.size() > 10) {
                endNum = 10;
            }
            List<Block> corrertBlock = new ArrayList<>();
            for (Block block : childBlocks.subList(0, endNum)) {
                List<String> contexts = block.getContext();
                for (String con : contexts) {
                    if (keyWordMap.containsKey(con)) {
                        Integer count = keyWordMap.get(con);
                        keyWordMap.put(con, count + 1);
                    } else {
                        keyWordMap.put(con, 1);
                    }
                }
                corrertBlock.add(block);
            }

            //获取前10个关键字
            List<Map.Entry<String, Integer>> keyWordMapList = new ArrayList<>(keyWordMap.entrySet());
            keyWordMapList.sort(new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue() - o1.getValue();
                }
            });

            List<String> keyWordList = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : keyWordMapList.subList(0, 9)) {
                keyWordList.add(entry.getKey());
            }

            for (Block block : childBlocks.subList(endNum, childBlocks.size() - 1)) {
                List<String> contexts = block.getContext();
                Integer initContextSize = contexts.size();
                contexts.removeAll(keyWordList);
                if (initContextSize - contexts.size() > 10) {
                    corrertBlock.add(block);
                }
            }
            List<ExtraResultInfo> infos = new ArrayList<>();

            for (Block block : corrertBlock) {
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
    }

    @Override
    @Async
    public void detailPageExtraTask(TaskInfoDto taskInfoDto) {
        WebDriver driver = pageCrawlService.getDriver();
        DetailPage detailPage = pageCrawlService.getDetailPage(taskInfoDto.getLink(), 11, 11, driver);
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        pageDivideService.detailPageDivide(detailPage);
        List<Block> blocks = pageDivideService.getNotDividedBlock();

        for (int i = 0; i < blocks.size(); i++) {
            long start = System.currentTimeMillis();
            detailPageFeatureService.getFeatureFromBlock(blocks.get(i), "手机");
            System.out.println("已经进行到" + i + "花费了" + (System.currentTimeMillis() - start));
        }

        //block特征得分计算
        List<Block> notZeroBlocks = new ArrayList<>();
        List<Float> wordDensityList = new ArrayList<>();
        for (Block block : blocks) {
            if (block.getDomainScore() != 0) {
                wordDensityList.add(block.getContextDensity());
                notZeroBlocks.add(block);
            }
        }

        Float maxWordDensity = Collections.max(wordDensityList);
        Float minWordDensity = Collections.min(wordDensityList);
        Float gradient = 1 / maxWordDensity - minWordDensity;

        for (Block block : notZeroBlocks) {
            if (!block.getContextDensity().isInfinite()) {
                Float density = gradient * (block.getContextDensity() - minWordDensity);
                Float res = block.getDomainScore() * (1 / density + 1 / block.getLinkNumber());
                block.setScoreResult(res);
            }
        }

        //block排序
        notZeroBlocks.sort(new Comparator<Block>() {
            @Override
            public int compare(Block o1, Block o2) {
                float res = o1.getScoreResult() - o2.getScoreResult();
                if (res > 0) {
                    return 1;
                } else if (res < 0) {
                    return -1;
                } else {
                    return 0;
                }

            }
        });

        List<ExtraResultInfo> infos = new ArrayList<>();
        List<String> context = blocks.get(0).getContext();
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
