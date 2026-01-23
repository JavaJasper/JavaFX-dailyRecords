package com.example.javafxdailyrecords.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.javafxdailyrecords.entity.DailyReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DailyReportMapper extends BaseMapper<DailyReport> {

    default List<DailyReport> selectAll() {
        return this.selectList(null);
    }

    List<DailyReport> selectByPage(@Param("pageSize") int var1, @Param("offset") int var2);

    long selectTotalCount();

    default DailyReport selectByDate(LocalDate date) {
        LambdaQueryWrapper<DailyReport> query = new LambdaQueryWrapper<>();
        query.eq(DailyReport::getReportDate, date);
        return (DailyReport)this.selectOne(query);
    }

    default List<DailyReport> selectByDateRange(LocalDate start, LocalDate end) {
        LambdaQueryWrapper<DailyReport> query = new LambdaQueryWrapper<>();
        query.between(DailyReport::getReportDate, start, end).orderByDesc(DailyReport::getReportDate);
        return this.selectList(query);
    }
}
