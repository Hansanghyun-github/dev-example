package com.example.devexample.statistics.repository.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class MBTIEmotionAmountAverageDto {
    private String mbtiFactor;
    private String emotion;
    private Long amountAverage;
}
