package cn.edu.seu.webPageExtractor.constants;

public enum FileLocationEnum {
    LISTPAGELOCATION("/Users/jinweihao/学习/毕业论文/data/listpage/","列表网页地址"),
    DETAILPAGELOCATION("/Users/jinweihao/学习/毕业论文/data/detailPage/","详情网页地址");

    private String location;
    private String name;

    FileLocationEnum(String location, String name) {
        this.location = location;
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
