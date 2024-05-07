package com.example.devexample.statistics.controller;

import com.example.devexample.article.entity.RegisterType;
import com.example.devexample.statistics.service.MBTIStatisticsService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@WebMvcTest(StatisticsController.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StatisticsControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean MBTIStatisticsService statisticsService;

    @Nested
    class MBTI_감정별_금액_평균_통계_API {
        @Test
        void 아래_path로_요청하면_statisticsService의_getAmountAverage_메서드가_실행된다() throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/mbti/emotion/amounts/average"));

            // then
            verify(statisticsService, times(1))
                    .getAmountAveragesEachMBTIAndEmotionLast90Days(any(), any());
        }
        
        @Test
        void registerType_쿼리_파라미터를_입력하지_않으면_SPEND가_입력된다() throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/mbti/emotion/amounts/average"));

            // then
            verify(statisticsService)
                    .getAmountAveragesEachMBTIAndEmotionLast90Days(any(), eq(RegisterType.SPEND));
        }

        @Test
        void 올바르지_않은_registerType_쿼리_파라미터를_입력하면_실패한다() throws Exception {
            // given
            String invalidRegisterType = "Invalid";

            // when // then
            // TODO ControllerAdvice 적용
            assertThrows(ServletException.class, () ->
                    mockMvc.perform(get("/api/statistics/mbti/emotion/amounts/average?registerType=" + invalidRegisterType)));
        }

        @ParameterizedTest
        @ValueSource(strings = {"SPEND", "SAVE"})
        void registerType_쿼리_파라미터로_SPEND나_SAVE를_입력하면_성공한다(String registerType) throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/mbti/emotion/amounts/average?registerType=" + registerType));

            // then
            verify(statisticsService, times(1))
                    .getAmountAveragesEachMBTIAndEmotionLast90Days(any(), eq(RegisterType.valueOf(registerType)));
        }
    }

    @Nested
    class MBTI_일별_금액_총합_통계_API {
        @Test
        void 아래_path로_요청하면_statisticsService의_getAmountSums_메서드가_실행된다() throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/mbti/daily/amounts/sum"));

            // then
            verify(statisticsService, times(1))
                    .getAmountSumsEachMBTIAndDayLast90Days(any(), any());
        }

        @Test
        void registerType_쿼리_파라미터를_입력하지_않으면_SPEND가_입력된다() throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/mbti/daily/amounts/sum"));

            // then
            verify(statisticsService)
                    .getAmountSumsEachMBTIAndDayLast90Days(any(), eq(RegisterType.SPEND));
        }

        @Test
        void 올바르지_않은_registerType_쿼리_파라미터를_입력하면_실패한다() throws Exception {
            // given
            String invalidRegisterType = "Invalid";

            // when // then
            // TODO ControllerAdvice 적용
            assertThrows(ServletException.class, () ->
                    mockMvc.perform(get("/api/statistics/mbti/daily/amounts/sum?registerType=" + invalidRegisterType)));
        }

        @ParameterizedTest
        @ValueSource(strings = {"SPEND", "SAVE"})
        void registerType_쿼리_파라미터로_SPEND나_SAVE를_입력하면_성공한다(String registerType) throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/mbti/daily/amounts/sum?registerType=" + registerType));

            // then
            verify(statisticsService, times(1))
                    .getAmountSumsEachMBTIAndDayLast90Days(any(), eq(RegisterType.valueOf(registerType)));
        }
    }

    @Nested
    class MBTI_단어_빈도수_통계_API {
        @Test
        void 아래_path로_요청하면_statisticsService의_getWordFrequencies_메서드가_실행된다() throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/mbti/word/frequencies"));

            // then
            verify(statisticsService, times(1))
                    .getWordFrequenciesLast90Days(any());
        }
    }

    @Nested
    class MBTI별_만족도_평균_통계_API {
        @Test
        void 아래_path로_요청하면_statisticsService의_getSatisfactionAverages_메서드가_실행된다() throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/mbti/statisfactions/average"));

            // then
            verify(statisticsService, times(1))
                    .getSatisfactionAveragesEachMBTILast90Days(any(), any());
        }

        @Test
        void registerType_쿼리_파라미터를_입력하지_않으면_SPEND가_입력된다() throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/mbti/statisfactions/average"));

            // then
            verify(statisticsService)
                    .getSatisfactionAveragesEachMBTILast90Days(any(), eq(RegisterType.SPEND));
        }

        @Test
        void 올바르지_않은_registerType_쿼리_파라미터를_입력하면_실패한다() throws Exception {
            // given
            String invalidRegisterType = "Invalid";

            // when // then
            // TODO ControllerAdvice 적용
            assertThrows(ServletException.class, () ->
                    mockMvc.perform(get("/api/statistics/mbti/statisfactions/average?registerType=" + invalidRegisterType)));
        }

        @ParameterizedTest
        @ValueSource(strings = {"SPEND", "SAVE"})
        void registerType_쿼리_파라미터로_SPEND나_SAVE를_입력하면_성공한다(String registerType) throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/mbti/statisfactions/average?registerType=" + registerType));

            // then
            verify(statisticsService, times(1))
                    .getSatisfactionAveragesEachMBTILast90Days(any(), eq(RegisterType.valueOf(registerType)));
        }
    }
}