package com.example.devexample.statistics.service;

import com.example.devexample.required.domain.RegisterType;
import com.example.devexample.statistics.controller.dto.MBTIDailyAmountSumResponse;
import com.example.devexample.statistics.controller.dto.MBTIEmotionAmountAverageResponse;
import com.example.devexample.statistics.repository.StatisticsMapper;
import com.example.devexample.statistics.repository.dto.MBTIDailyAmountSumDto;
import com.example.devexample.statistics.repository.dto.MBTIEmotionAmountAverageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MBTIStatisticsService {
    private final StatisticsMapper statisticsMapper;
    private final WordExtractionService wordExtractionService;
    private final int PERIOD_CRITERIA = 90;

    public MBTIStatisticsService(StatisticsMapper statisticsMapper, WordExtractionService wordExtractionService) {
        this.statisticsMapper = statisticsMapper;
        this.wordExtractionService = wordExtractionService;
    }

    public List<MBTIEmotionAmountAverageResponse> getAmountAveragesEachMBTIAndEmotionLast90Days(LocalDate today, RegisterType registerType){
        LocalDate startDate = today.minusDays(PERIOD_CRITERIA);
        List<MBTIEmotionAmountAverageDto> dtos = statisticsMapper.getAmountAveragesEachMBTIAndEmotionBetweenStartDateAndEndDate(registerType, startDate, today);
        
        // TODO 없는 값에 대한 0 padding 작업

        return dtos.stream()
                .collect(
                        groupingBy(MBTIEmotionAmountAverageDto::getMbtiFactor))
                .entrySet().stream()
                .map((e) ->
                        MBTIEmotionAmountAverageResponse.of(e.getKey(), e.getValue()))
                .toList();
    }

    public List<MBTIDailyAmountSumResponse> getAmountSumsEachMBTIAndDayLast90Days(LocalDate today, RegisterType registerType) {
        LocalDate startDate = today.minusDays(PERIOD_CRITERIA);
        List<MBTIDailyAmountSumDto> dtos = statisticsMapper.getAmountSumsEachMBTIAndDayBetweenStartDateAndEndDate(registerType, startDate, today);

        // TODO 없는 값에 대한 0 padding 작업

        return dtos.stream()
                .collect(
                        groupingBy(MBTIDailyAmountSumDto::getMbtiFactor))
                .entrySet().stream()
                .map((e) ->
                        MBTIDailyAmountSumResponse.of(e.getKey(), e.getValue()))
                .toList();
    }
}
