package com.example.devexample.statistics.repository.dto;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class MBTIDailyAmountSumDto {
    private String mbtiFactor;
    private LocalDate day;
    private Long amountSum;
}
