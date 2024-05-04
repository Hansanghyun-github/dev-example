package com.example.devexample.statistics.service;

import com.example.devexample.required.domain.RegisterType;
import com.example.devexample.statistics.controller.dto.MBTIDailyAmountSumResponse;
import com.example.devexample.statistics.controller.dto.MBTIEmotionAmountAverageResponse;
import com.example.devexample.statistics.controller.dto.WordFrequencyResponse;
import com.example.devexample.statistics.repository.StatisticsMapper;
import com.example.devexample.statistics.repository.dto.AllMemoDto;
import com.example.devexample.statistics.repository.dto.MBTIDailyAmountSumDto;
import com.example.devexample.statistics.repository.dto.MBTIEmotionAmountAverageDto;
import com.example.devexample.statistics.repository.dto.MBTISatisfactionAverageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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

    public WordFrequencyResponse getWordFrequenciesLast90Days(LocalDate today){
        LocalDate startDate = today.minusDays(PERIOD_CRITERIA);
        // 최근 90일동안 모든 유저가 적은 메모의 빈도수 측정
        List<AllMemoDto> allMemo = statisticsMapper.getAllMemosByMBTIBetweenStartDateAndEndDate("none", startDate, today);

        // 최근 90일 동안 나와 MBTI가 같은 유저가 적은 메모의 빈도수 측정
        String mbti = getUserMBTI();

        if(isNone(mbti)){
            return WordFrequencyResponse.builder()
                    .allWordFrequencies(
                            wordExtractionService.analyzeWords(
                                    allMemo.stream()
                                            .flatMap((m) -> Stream.of(m.getThings(), m.getAny()))
                                            .toList())
                    )
                    .myWordFrequencies(List.of())
                    .build();
        }

        List<AllMemoDto> memoByMBTI = statisticsMapper.getAllMemosByMBTIBetweenStartDateAndEndDate(mbti, startDate, today);

        return WordFrequencyResponse.builder()
                .allWordFrequencies(
                        wordExtractionService.analyzeWords(
                                allMemo.stream()
                                        .flatMap((m) -> Stream.of(m.getThings(), m.getAny()))
                                        .toList())
                )
                .myWordFrequencies(
                        wordExtractionService.analyzeWords(
                                memoByMBTI.stream()
                                        .flatMap((m) -> Stream.of(m.getThings(), m.getAny()))
                                        .toList())
                )
                .build();
    }

    private static boolean isNone(String mbti) {
        return Objects.isNull(mbti) || mbti.equals("none");
    }

    public List<MBTISatisfactionAverageDto> getSatisfactionAveragesEachMBTILast90Days(LocalDate today, RegisterType registerType){
        LocalDate startDate = today.minusDays(PERIOD_CRITERIA);
        return statisticsMapper.getSatisfactionAveragesEachMBTIBetweenStartDateAndEndDate(registerType, startDate, today);
    }

    private String getUserMBTI() {
        // TODO 현재 세션 이용해 인증 한 유저의 mbti 반환해야 함
        return "ISTJ";
    }
}
