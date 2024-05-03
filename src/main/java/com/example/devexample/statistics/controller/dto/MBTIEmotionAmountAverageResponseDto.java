package com.example.devexample.statistics.controller.dto;

import com.example.devexample.statistics.repository.dto.MBTIEmotionAmountAverageDto;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MBTIEmotionAmountAverageResponseDto {
        private MBTIFactor mbti;
        private List<EmotionAmountAverage> emotionAmountAverages;

        @Builder
        public MBTIEmotionAmountAverageResponseDto(MBTIFactor mbtiFactor, List<EmotionAmountAverage> emotionCount) {
            this.mbti = mbtiFactor;
            this.emotionAmountAverages = emotionCount;
        }

        public static MBTIEmotionAmountAverageResponseDto of(String factor, List<MBTIEmotionAmountAverageDto> dtos){
            return MBTIEmotionAmountAverageResponseDto.builder()
                    .mbtiFactor(MBTIFactor.valueOf(factor))
                    .emotionCount(
                            dtos.stream()
                                    .map(EmotionAmountAverage::of)
                                    .collect(Collectors.toList())
                    )
                    .build();
        }

        @Override
        public String toString() {
            return "EmotionAmountAverage\n" +
                    "mbti=" + mbti + "\n" +
                    "emotionCount=\n" +
                    emotionAmountAverages.stream()
                            .map(EmotionAmountAverage::toString)
                            .map(ea -> ea+"\n")
                            .toList()
                    + "\n";
        }

    @Getter
    @ToString
    public static class EmotionAmountAverage {
        private Emotion emotion;
        private Float amountAverage;

        @Builder
        public EmotionAmountAverage(Emotion emotion, Float average) {
            this.emotion = emotion;
            this.amountAverage = average;
        }

        public static EmotionAmountAverage of(MBTIEmotionAmountAverageDto dto){
            return EmotionAmountAverage.builder()
                    .emotion(Emotion.valueOf(dto.getEmotion()))
                    .average(dto.getAmountAverage())
                    .build();
        }
    }
}
