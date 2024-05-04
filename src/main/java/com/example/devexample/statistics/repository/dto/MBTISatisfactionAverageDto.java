package com.example.devexample.statistics.repository.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MBTISatisfactionAverageDto {
    private String mbtiFactor;
    private Float satisfactionAverage;
}
