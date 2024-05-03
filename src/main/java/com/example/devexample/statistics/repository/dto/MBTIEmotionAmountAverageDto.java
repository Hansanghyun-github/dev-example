package com.example.devexample.statistics.repository.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class MBTIEmotionAmountAverageDto {
    private String mbti;
    private String emotion;
    private Float amountAverage;
}
