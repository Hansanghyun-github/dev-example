package com.example.devexample.dev.statisticsTest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StatisticsController {
    @GetMapping("/statistics")
    public ApiResponse<EmotionCountDto> statisticsTest(){

        return ApiResponse.<EmotionCountDto>builder()
                .isSuccessed(true)
                .message("success")
                .data(EmotionCountDto.builder()
                        .maxCount(10L)
                        .tCount(
                                EmotionCountDto.EmotionCount.builder()
                                        .c(EmotionCountDto.EmotionCount.Criteria.T)
                                        .e1(10L)
                                        .e2(5L)
                                        .e3(0L)
                                .build())
                        .fCount(
                                EmotionCountDto.EmotionCount.builder()
                                        .c(EmotionCountDto.EmotionCount.Criteria.T)
                                        .e1(10L)
                                        .e2(5L)
                                        .e3(0L)
                                        .build())
                        .jCount(
                                EmotionCountDto.EmotionCount.builder()
                                        .c(EmotionCountDto.EmotionCount.Criteria.T)
                                        .e1(10L)
                                        .e2(5L)
                                        .e3(0L)
                                        .build())
                        .pCount(
                                EmotionCountDto.EmotionCount.builder()
                                        .c(EmotionCountDto.EmotionCount.Criteria.T)
                                        .e1(10L)
                                        .e2(5L)
                                        .e3(0L)
                                        .build())
                        .build())
                .build();
    }
}
