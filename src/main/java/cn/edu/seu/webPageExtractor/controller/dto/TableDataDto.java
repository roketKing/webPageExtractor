package cn.edu.seu.webPageExtractor.controller.dto;

import java.util.List;

public class TableDataDto {
    private List<TaskInfoDto> data;
    private Integer draw;
    private Integer recordsTotal;
    private Integer recordsFiltered;

    public List<TaskInfoDto> getData() {
        return data;
    }

    public void setData(List<TaskInfoDto> data) {
        this.data = data;
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public Integer getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(Integer recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public Integer getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(Integer recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }
}
