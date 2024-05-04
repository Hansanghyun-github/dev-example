package com.example.devexample.statistics.controller;

import com.example.devexample.required.domain.RegisterType;
import com.example.devexample.statistics.controller.dto.MBTIDailyAmountSumResponse;
import com.example.devexample.statistics.controller.dto.MBTIEmotionAmountAverageResponse;
import com.example.devexample.statistics.controller.dto.WordFrequencyResponse;
import com.example.devexample.statistics.repository.dto.MBTISatisfactionAverageDto;
import com.example.devexample.statistics.service.MBTIStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatisticsController {
    private final MBTIStatisticsService statisticsService;

    @GetMapping("/api/statistics/mbti/emotion/amounts/average")
    public List<MBTIEmotionAmountAverageResponse> getAmountAveragesEachMBTIAndEmotionLast90Days(@RequestParam(defaultValue = "SPEND") String registerType){
        return statisticsService.getAmountAveragesEachMBTIAndEmotionLast90Days(
                LocalDate.now(),
                RegisterType.valueOf(registerType));
    }

    @GetMapping("/api/statistics/mbti/daily/amounts/sum")
    public List<MBTIDailyAmountSumResponse> getAmountSumsEachMBTIAndDayLast90Days(@RequestParam(defaultValue = "SPEND") String registerType) {
        return statisticsService.getAmountSumsEachMBTIAndDayLast90Days(
                LocalDate.now(),
                RegisterType.valueOf(registerType));
    }

    @GetMapping("/api/statistics/mbti/word/frequencies")
    public WordFrequencyResponse getWordFrequencyLast90Days(){
        return statisticsService.getWordFrequenciesLast90Days(LocalDate.now());
    }

    @GetMapping("/api/statistics/mbti/statisfactions/average")
    public List<MBTISatisfactionAverageDto> getSatisfactionAveragesEachMBTILast90Days(@RequestParam(defaultValue = "SPEND") String registerType){
        return statisticsService.getSatisfactionAveragesEachMBTILast90Days(
                LocalDate.now(),
                RegisterType.valueOf(registerType));
    }
}
