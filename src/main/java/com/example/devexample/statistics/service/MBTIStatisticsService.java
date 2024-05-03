package com.example.devexample.statistics.service;

import com.example.devexample.required.domain.RegisterType;
import com.example.devexample.statistics.controller.dto.MBTIEmotionAmountAverageResponseDto;
import com.example.devexample.statistics.repository.StatisticsMapper;
import com.example.devexample.statistics.repository.dto.MBTIEmotionAmountAverageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MBTIStatisticsService {
    private final StatisticsMapper statisticsMapper;

    public List<MBTIEmotionAmountAverageResponseDto> getMBTIEmotionAmountAverageList(int month, RegisterType registerType){
        List<MBTIEmotionAmountAverageDto> dtos = statisticsMapper.getAverageStatistics(month, registerType);

        return dtos.stream()
                .collect(
                        groupingBy(MBTIEmotionAmountAverageDto::getMbti))
                .entrySet().stream()
                .map((e) ->
                        MBTIEmotionAmountAverageResponseDto.of(e.getKey(), e.getValue()))
                .toList();
    }
}
