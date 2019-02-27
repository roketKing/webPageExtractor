package cn.edu.seu.webPageExtractor.controller.dto;

import cn.edu.seu.webPageExtractor.core.TaskInfo;

import java.util.List;

public class TaskInfoDto extends TaskInfo {
    private String stateName;
    private String dateString;
    /**
     * 任务级别的关键词
     */
    private List<String> taskContext;

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }
}
