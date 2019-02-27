package cn.edu.seu.webPageExtractor.constants;

public enum TaskStateEnum {
    NEWTASK(10,"新任务"),
    PROCESTASK(20,"进行中任务"),
    FINISHTASK(40,"任务完成");

    private Integer state;
    private String name;

    TaskStateEnum(Integer state,String name){
        this.state = state;
        this.name = name;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
