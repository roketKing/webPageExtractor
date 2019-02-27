package cn.edu.seu.webPageExtractor.core;

import javax.persistence.*;
import java.util.Date;

@Table(name = "detail_page_info")
@Entity
public class DetailPageInfo {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    private String link;
    private Integer taskId;
    private Date time;
    private String location;
    private Integer listId;
    private Integer noise;

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

    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer listId) {
        this.listId = listId;
    }

    public Integer getNoise() {
        return noise;
    }

    public void setNoise(Integer noise) {
        this.noise = noise;
    }
}
