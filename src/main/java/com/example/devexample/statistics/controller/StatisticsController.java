package com.example.devexample.statistics.controller;

import com.example.devexample.required.domain.RegisterType;
import com.example.devexample.statistics.controller.dto.MBTIEmotionAmountAverageResponseDto;
import com.example.devexample.statistics.service.MBTIStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatisticsController {
    private final MBTIStatisticsService statisticsService;

    @GetMapping("/api/statistics/mbti/emotion/amounts/average")
    public List<MBTIEmotionAmountAverageResponseDto> getAverageStatistics(@RequestParam(defaultValue = "SPEND") String registerType){
        log.info("average statistics api required");
        return statisticsService.getMBTIEmotionAverageList(LocalDateTime.now().getMonthValue(), RegisterType.valueOf(registerType));
    }
}
