package cn.edu.seu.webPageExtractor.controller;


import cn.edu.seu.webPageExtractor.constants.TaskStateEnum;
import cn.edu.seu.webPageExtractor.controller.dto.ResultTableDto;
import cn.edu.seu.webPageExtractor.controller.dto.TableDataDto;
import cn.edu.seu.webPageExtractor.controller.dto.TaskInfoDto;
import cn.edu.seu.webPageExtractor.core.DetailPageInfo;
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
import cn.edu.seu.webPageExtractor.service.manage.DbTransferManager;
import cn.edu.seu.webPageExtractor.service.manage.DetailPageInfoManager;
import cn.edu.seu.webPageExtractor.service.manage.ExtraResultManager;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
public class TaskController {
    @Autowired
    private TaskManageService taskManageService;

    @Autowired
    private ExtraResultManager extraResultManager;


    @GetMapping("/task")
    public String index() {
        return "task";
    }

    @GetMapping("/taskResult")
    public String taskResult() {
        return "taskResult";
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
        TaskInfo currentTaskInfo = taskManageService.queryTaskById(taskInfoDto.getId());
        if (currentTaskInfo.getState().equals(TaskStateEnum.NEWTASK.getState())){
            if (taskInfoDto.getType() == 10) {
                taskManageService.domainTrainTask(taskInfoDto);
            } else if (taskInfoDto.getType() == 20) {
                taskManageService.listPageExtraTask(taskInfoDto);
            } else if (taskInfoDto.getType() == 30) {
                taskManageService.detailPageExtraTask(taskInfoDto);
            }
        }else {
            return "非法启动的任务";
        }
        return "启动任务成功！";
    }

    @Autowired
    private DbTransferManager dbTransferManager;

    @RequestMapping(value = "/db.do", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public @ResponseBody
    String sydb() {
        for (int i = 0; i < 2292; i++) {
            try {
                Integer pageNum = 0;
                if (i != 0) {
                    pageNum = (i + 1) * 5000;
                }
                dbTransferManager.transferData(pageNum, 5000);
            } catch (Exception e) {
                try {
                    Thread.sleep(1000);
                    i = i - 1;
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return "success";
    }

    //查询任务结果
    @RequestMapping(value = "/queryTaskResult.do", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public @ResponseBody
    String queryTaskResult(HttpServletRequest request) {
        String taskId = request.getParameter("taskId");
        //在mongo中查询任务结果
        List<ExtraResultInfo> infos = extraResultManager.queryResultByTaskId(taskId);
        ResultTableDto resultTableDto = new ResultTableDto();
        resultTableDto.setData(infos);
        resultTableDto.setDraw(1);
        resultTableDto.setRecordsFiltered(infos.size());
        resultTableDto.setRecordsTotal(infos.size());

        Gson gson = new Gson();
        return gson.toJson(resultTableDto);
    }



}
