package com.example.javafxdailyrecords.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.javafxdailyrecords.config.MyBatisPlusConfig;
import com.example.javafxdailyrecords.entity.DailyReport;
import com.example.javafxdailyrecords.mapper.DailyReportMapper;
import com.example.javafxdailyrecords.util.CustomPage;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class DailyReportService {

    private DailyReportMapper dailyReportMapper;

    private void initMapper() {
        if (Objects.isNull(dailyReportMapper)) {
            dailyReportMapper = MyBatisPlusConfig.getMapper(DailyReportMapper.class);
            if (Objects.isNull(dailyReportMapper)) {
                throw new RuntimeException("DailyReportMapper 初始化失败");
            }
        }

    }

    public CustomPage<DailyReport> getDailyReportByPage(int pageNum, int pageSize) {
        initMapper();
        pageNum = Math.max(pageNum, 1);
        pageSize = pageSize < 1 ? 10 : pageSize;
        int offset = (pageNum - 1) * pageSize;
        List<DailyReport> dataList = dailyReportMapper.selectByPage(pageSize, offset);
        long totalCount = dailyReportMapper.selectTotalCount();
        return new CustomPage(dataList, pageNum, pageSize, totalCount);
    }

    public boolean saveOrUpdateDailyReport(DailyReport report) {
        initMapper();
        DailyReport existReport = dailyReportMapper.selectByDate(report.getReportDate());
        if (existReport != null) {
            LambdaUpdateWrapper<DailyReport> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(DailyReport::getId, existReport.getId())
                    .set(DailyReport::getContent, report.getContent())
                    .set(DailyReport::getRemark, report.getRemark());
            return dailyReportMapper.update(updateWrapper) > 0;
        } else {
            return dailyReportMapper.insert(report) > 0;
        }
    }

    public DailyReport getByDate(LocalDate date) {
        initMapper();
        return dailyReportMapper.selectByDate(date);
    }

    public List<DailyReport> listByDateRange(LocalDate start, LocalDate end) {
        initMapper();
        return dailyReportMapper.selectByDateRange(start, end);
    }

}
