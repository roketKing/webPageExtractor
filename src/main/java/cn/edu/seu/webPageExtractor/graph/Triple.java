package cn.edu.seu.webPageExtractor.graph;

/**
 * 图谱查询出来的三元组的类型
 */
public class Triple {
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


    @Override
    public String toString() {
        return "Triple{" +
                "subject='" + subject + '\'' +
                ", predict='" + predict + '\'' +
                ", object='" + object + '\'' +
                '}';
    }
}
