package com.example.devexample.statistics.repository;

import com.example.devexample.required.domain.RegisterType;
import com.example.devexample.statistics.repository.dto.MBTIDailyAmountSumDto;
import com.example.devexample.statistics.repository.dto.MBTIEmotionAmountAverageDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StatisticsMapper {
    // mbti별, 감정별 지출 평균 그래프
    List<MBTIEmotionAmountAverageDto> getAmountAveragesEachMBTIAndEmotionBetweenStartDateAndEndDate(@Param("registerType") RegisterType registerType, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // mbti별, 날짜별 지출 합 그래프
    List<MBTIDailyAmountSumDto> getAmountSumsEachMBTIAndDayBetweenStartDateAndEndDate(@Param("registerType") RegisterType registerType, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
