package cn.edu.seu.webPageExtractor.core;

import javax.persistence.*;
import java.util.Date;

@Table(name = "list_page_info")
@Entity
public class ListPageInfo {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    private String link;
    private Integer taskId;
    private Date time;
    private String location;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
