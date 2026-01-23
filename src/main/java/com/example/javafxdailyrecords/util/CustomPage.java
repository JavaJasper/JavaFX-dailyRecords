package com.example.javafxdailyrecords.util;

import java.util.List;

public class CustomPage<T> {
    private List<T> dataList;
    private int pageNum;
    private int pageSize;
    private long totalCount;
    private int totalPage;

    public CustomPage(List<T> dataList, int pageNum, int pageSize, long totalCount) {
        this.dataList = dataList;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPage = (int)Math.ceil((double)totalCount / (double)pageSize);
    }

    public List<T> getDataList() {
        return this.dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public int getPageNum() {
        return this.pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
