package cn.edu.seu.webPageExtractor.constants;

public enum  ContextType {
    PROPERTY_CONTEXT("属性类型文本",10),
    PROPERTYVALUE_CONTEXT("属性类型文本",20),
    NORMAL_CONTEXT("属性类型文本",30);

    private String typeStr;
    private Integer type;

    ContextType(String typeStr, Integer type) {
        this.typeStr = typeStr;
        this.type = type;
    }
}
