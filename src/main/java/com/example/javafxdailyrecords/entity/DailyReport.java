package com.example.javafxdailyrecords.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("daily_report")
public class DailyReport {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String userId;
    private LocalDate reportDate;
    private String week;
    private String content;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public DailyReport() {
    }

    public DailyReport(LocalDate reportDate, String content, String remark) {
        this.reportDate = reportDate;
        this.content = content;
        this.remark = remark;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDate getReportDate() {
        return this.reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public String getWeek() {
        return this.week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "DailyReport{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", reportDate=" + reportDate +
                ", week='" + week + '\'' +
                ", content='" + content + '\'' +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

}
