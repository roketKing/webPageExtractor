package cn.edu.seu.webPageExtractor.controller.dto;

import cn.edu.seu.webPageExtractor.core.ExtraResultInfo;

import java.util.List;

public class ResultTableDto {
    private List<ExtraResultInfo> data;
    private Integer draw;
    private Integer recordsTotal;
    private Integer recordsFiltered;

    public List<ExtraResultInfo> getData() {
        return data;
    }

    public void setData(List<ExtraResultInfo> data) {
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
