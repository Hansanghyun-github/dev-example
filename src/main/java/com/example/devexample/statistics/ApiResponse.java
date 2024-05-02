package com.example.devexample.statistics;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ApiResponse<T> {
    private final boolean isSuccessed;
    private final String message;
    private final T data;
}
