package cn.edu.seu.webPageExtractor.core;

import org.springframework.scheduling.annotation.EnableAsync;

import javax.persistence.*;
import java.util.Date;
@Table(name = "graph_score")
@Entity
public class GraphScoreInfo {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    private String domain;
    private String property;
    private String propertyValue;
    private Float score;
    private Date time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
