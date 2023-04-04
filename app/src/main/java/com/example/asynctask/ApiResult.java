package com.example.asynctask;

import java.util.ArrayList;

public class ApiResult {
    private Integer total;
    private Integer totalHits;
    private ArrayList<ResultImage> hits;

    public ApiResult(Integer total, Integer totalHits, ArrayList<ResultImage> hits) {
        this.total = total;
        this.totalHits = totalHits;
        this.hits = hits;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(Integer totalHits) {
        this.totalHits = totalHits;
    }

    public ArrayList<ResultImage> getHits() {
        return hits;
    }

    public void setHits(ArrayList<ResultImage> hits) {
        this.hits = hits;
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "total=" + total +
                ", totalHits=" + totalHits +
                ", hits=" + hits +
                '}';
    }
}
