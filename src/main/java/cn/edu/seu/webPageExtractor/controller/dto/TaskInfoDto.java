package cn.edu.seu.webPageExtractor.controller.dto;

import cn.edu.seu.webPageExtractor.core.TaskInfo;

import java.util.List;

public class TaskInfoDto extends TaskInfo {
    private String stateName;
    private String dateString;
    private String typeString;
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

    public String getTypeString() {
        return typeString;
    }

    public void setTypeString(String typeString) {
        this.typeString = typeString;
    }

    public List<String> getTaskContext() {
        return taskContext;
    }

    public void setTaskContext(List<String> taskContext) {
        this.taskContext = taskContext;
    }
}
