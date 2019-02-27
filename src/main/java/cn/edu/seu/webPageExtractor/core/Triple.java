package cn.edu.seu.webPageExtractor.core;


import javax.persistence.*;

@Entity
@Table(name = "category_spo")
public class Triple {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    private String subject;
    private String predict;
    private String object;


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPredict() {
        return predict;
    }

    public void setPredict(String predict) {
        this.predict = predict;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
