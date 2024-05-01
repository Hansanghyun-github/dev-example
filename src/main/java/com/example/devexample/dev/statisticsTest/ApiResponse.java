package com.example.devexample.dev.statisticsTest;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ApiResponse<T> {
    private boolean isSuccessed;
    private String message;
    private T data;
}
