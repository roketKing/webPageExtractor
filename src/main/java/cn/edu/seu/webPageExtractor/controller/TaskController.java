package cn.edu.seu.webPageExtractor.controller;


import cn.edu.seu.webPageExtractor.constants.TaskStateEnum;
import cn.edu.seu.webPageExtractor.controller.dto.TableDataDto;
import cn.edu.seu.webPageExtractor.controller.dto.TaskInfoDto;
import cn.edu.seu.webPageExtractor.core.TaskInfo;
import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.ListPage;
import cn.edu.seu.webPageExtractor.graph.service.GraphScoreService;
import cn.edu.seu.webPageExtractor.service.DetailPageFeatureService;
import cn.edu.seu.webPageExtractor.service.PageCrawlService;
import cn.edu.seu.webPageExtractor.service.TaskManageService;
import cn.edu.seu.webPageExtractor.service.manage.DbTransferManager;
import com.google.gson.Gson;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class TaskController {
    @Autowired
    private TaskManageService taskManageService;
    @Autowired
    private PageCrawlService pageCrawlService;

    @Autowired
    private DetailPageFeatureService detailPageFeatureService;

    @Autowired
    private GraphScoreService graphScoreService;


    @GetMapping("/task")
    public String index() {
        return "task";
    }


    //创建任务
    @RequestMapping(value = "/createTask.do", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public @ResponseBody
    String createTask(@RequestBody TaskInfo taskInfo) {
        taskInfo.setTime(new Date());
        taskInfo.setState(TaskStateEnum.NEWTASK.getState());
        TaskInfoDto taskInfoDto = taskManageService.createATask(taskInfo);
        Gson gson = new Gson();
        return gson.toJson(taskInfoDto);
    }

    //查询任务
    @RequestMapping(value = "/queryTask.do", method = RequestMethod.GET)
    public @ResponseBody
    String queryTask() {
        List<TaskInfoDto> data = taskManageService.queryAllTask();
        TableDataDto tableDataDto = new TableDataDto();
        tableDataDto.setData(data);
        tableDataDto.setDraw(1);
        tableDataDto.setRecordsFiltered(data.size());
        tableDataDto.setRecordsTotal(data.size());

        Gson gson = new Gson();
        return gson.toJson(tableDataDto);
    }

    //查询任务结果


    //开始任务
    @RequestMapping(value = "/startTask.do", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public @ResponseBody
    String startTask(@RequestBody TaskInfoDto taskInfoDto) {
        WebDriver driver = pageCrawlService.getDriver();
        String keyword = taskInfoDto.getKeyword();
        List<ListPage> listPages = pageCrawlService.getListPage(taskInfoDto.getLink(), taskInfoDto.getId(), driver, 1);
        List<DetailPage> correctDetailPage = new ArrayList<>();
        List<DetailPage> notCorrectDetailPages = new ArrayList<>();
        for (ListPage listPage : listPages) {
            List<String> links = pageCrawlService.getListPageALink(keyword, listPage);
            for (String link : links) {
                DetailPage detailPage = pageCrawlService.getDetailPage(link, taskInfoDto.getId(), listPage.getId(), driver);
                List<String> contexts = detailPageFeatureService.getSpecialTagContextFeature(detailPage);
                detailPage.setSpeacilContext(contexts);
                //查询属性领域分值数据库
                Float contextDomainScore = graphScoreService.contextDomainScoreCalculate(contexts);
                if (contextDomainScore > 100) {
                    // 是领域相关的页面  不是噪声
                    correctDetailPage.add(detailPage);
                } else {
                    notCorrectDetailPages.add(detailPage);
                }
            }
        }
        //统计关键词，从notCorrect中重新获取领域相关的页面
        List<String> correctWords = taskManageService.taskContextCalculate(correctDetailPage);
        for (DetailPage notCorrectDetailPage : notCorrectDetailPages) {
            List<String> contexts = notCorrectDetailPage.getSpeacilContext();
            if (contexts.containsAll(correctWords)) {
                correctDetailPage.add(notCorrectDetailPage);
            }
        }
        //将结果记录到数据库中
        return null;
    }

    @Autowired
    private DbTransferManager dbTransferManager;

    @RequestMapping(value = "/db.do", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public @ResponseBody
    String sydb() {
        for (int i = 0; i < 2292; i++) {
            try {
                Integer pageNum=0;
                if (i!=0){
                    pageNum = (i+1)*5000;
                }
                dbTransferManager.transferData(pageNum,5000);
            } catch (Exception e) {
                try {
                    Thread.sleep(1000);
                    i=i-1;
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return "success";
    }

}