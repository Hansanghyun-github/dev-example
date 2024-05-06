package com.example.devexample.statistics.service;

import com.example.devexample.required.domain.RegisterType;
import com.example.devexample.statistics.controller.dto.MBTIDailyAmountSumResponse;
import com.example.devexample.statistics.controller.dto.MBTIEmotionAmountAverageResponse;
import com.example.devexample.statistics.controller.dto.WordFrequencyResponse;
import com.example.devexample.statistics.repository.StatisticsMapper;
import com.example.devexample.statistics.repository.dto.AllMemoDto;
import com.example.devexample.statistics.repository.dto.MBTIDailyAmountSumDto;
import com.example.devexample.statistics.repository.dto.MBTIEmotionAmountAverageDto;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MBTIStatisticsServiceTest {
    @Mock
    UserService userService;
    @Mock
    WordExtractionService wordExtractionService;
    @Mock
    StatisticsMapper statisticsMapper;

    @InjectMocks
    MBTIStatisticsService statisticsService;

    @Captor
    ArgumentCaptor<RegisterType> captorRegisterType;
    @Captor
    ArgumentCaptor<LocalDate> captorStartDate, captorEndDate;
    @Captor
    ArgumentCaptor<String> captorString;
    @Captor
    ArgumentCaptor<List<String>> captorWords;

    @Nested
    class 최근_90일_동안의_MBTI별_감정별_금액_평균을_얻는_메서드{
        @Test
        void 현재_날짜를_레포에게_전달하면_DB에게_최근_90일의_데이터만_요청한다() throws Exception {
            // given
            LocalDate now = LocalDate.now();

            // when
            statisticsService.getAmountAveragesEachMBTIAndEmotionLast90Days(now, null);

            // then
            verify(statisticsMapper)
                    .getAmountAveragesEachMBTIAndEmotionBetweenStartDateAndEndDate(
                            any(),
                            captorStartDate.capture(),
                            captorEndDate.capture());

            assertThat(captorStartDate.getValue()).isEqualTo(now.minusDays(90));
            assertThat(captorEndDate.getValue()).isEqualTo(now);
        }

        @ParameterizedTest
        @ValueSource(strings = {"SPEND", "SAVE"})
        void 입력한_RegisterType을_레포에게_그대로_입력한다(RegisterType registerType) throws Exception {
            // when
            statisticsService.getAmountAveragesEachMBTIAndEmotionLast90Days(LocalDate.now(), registerType);

            // then
            verify(statisticsMapper)
                    .getAmountAveragesEachMBTIAndEmotionBetweenStartDateAndEndDate(
                            captorRegisterType.capture(),
                            any(),
                            any()
                    );


            assertThat(captorRegisterType.getValue()).isEqualTo(registerType);
        }

        @Test
        void MBTI와_감정에_대한_금액_평균을_MBTI_별로_그루핑해서_반환한다() throws Exception {
            // given
            List<MBTIEmotionAmountAverageDto> inputs = List.of(
                    new MBTIEmotionAmountAverageDto("I", "good", 1L),
                    new MBTIEmotionAmountAverageDto("I", "bad", 2L),
                    new MBTIEmotionAmountAverageDto("E", "good", 3L),
                    new MBTIEmotionAmountAverageDto("E", "bad", 4L)
            );
            List<Boolean> visited = new ArrayList<>();
            for(int i=0;i<inputs.size();i++)
                visited.add(false);

            when(statisticsMapper.getAmountAveragesEachMBTIAndEmotionBetweenStartDateAndEndDate(any(), any(), any()))
                    .thenReturn(inputs);

            // when
            List<MBTIEmotionAmountAverageResponse> responses =
                    statisticsService.getAmountAveragesEachMBTIAndEmotionLast90Days(LocalDate.now(), null);

            // then
            assertThat(responses)
                    .hasSize(2);

            // TODO 그루핑하는 테스트 분리 & 리팩토링
            for(MBTIEmotionAmountAverageResponse r: responses){
                for(int i=0;i<inputs.size();i++){
                    if(inputs.get(i).getMbtiFactor().equals(r.getMbtiFactor().toString())){
                        final int index = i;
                        r.getEmotionAmountAverages()
                                .forEach(ea -> {
                                    if(ea.getEmotion().toString().equals(inputs.get(index).getEmotion())
                                            && ea.getAmountAverage().equals(inputs.get(index).getAmountAverage()))
                                        visited.set(index, true);
                                });
                    }
                }
            }

            assertThat(visited).doesNotContain(false);
        }
    }

    @Nested
    class 최근_90일_동안의_MBTI별_일별_금액_총합을_얻는_메서드 {
        @Test
        void 현재_날짜를_레포에게_전달하면_DB에게_최근_90일의_데이터만_요청한다() throws Exception {
            // given
            LocalDate now = LocalDate.now();

            // when
            statisticsService.getAmountSumsEachMBTIAndDayLast90Days(now, null);

            // then
            verify(statisticsMapper)
                    .getAmountSumsEachMBTIAndDayBetweenStartDateAndEndDate(
                            any(),
                            captorStartDate.capture(),
                            captorEndDate.capture());

            assertThat(captorStartDate.getValue()).isEqualTo(now.minusDays(90));
            assertThat(captorEndDate.getValue()).isEqualTo(now);
        }

        @ParameterizedTest
        @ValueSource(strings = {"SPEND", "SAVE"})
        void 입력한_RegisterType을_레포에게_그대로_입력한다(RegisterType registerType) throws Exception {
            // when
            statisticsService.getAmountSumsEachMBTIAndDayLast90Days(LocalDate.now(), registerType);

            // then
            verify(statisticsMapper)
                    .getAmountSumsEachMBTIAndDayBetweenStartDateAndEndDate(
                            captorRegisterType.capture(),
                            any(),
                            any()
                    );


            assertThat(captorRegisterType.getValue()).isEqualTo(registerType);
        }

        @Test
        void MBTI와_일별_금액_평균을_MBTI_별로_그루핑해서_반환한다() throws Exception {
            // given
            List<MBTIDailyAmountSumDto> inputs = List.of(
                    new MBTIDailyAmountSumDto("I", LocalDate.now(), 1L),
                    new MBTIDailyAmountSumDto("I", LocalDate.now().minusDays(1L), 2L),
                    new MBTIDailyAmountSumDto("E", LocalDate.now(), 3L),
                    new MBTIDailyAmountSumDto("E", LocalDate.now().minusDays(1L), 4L)
            );
            List<Boolean> visited = new ArrayList<>();
            for(int i=0;i<inputs.size();i++)
                visited.add(false);

            when(statisticsMapper.getAmountSumsEachMBTIAndDayBetweenStartDateAndEndDate(any(), any(), any()))
                    .thenReturn(inputs);

            // when
            List<MBTIDailyAmountSumResponse> responses =
                    statisticsService.getAmountSumsEachMBTIAndDayLast90Days(LocalDate.now(), null);

            // then
            assertThat(responses)
                    .hasSize(2);

            for(MBTIDailyAmountSumResponse r: responses){
                for(int i=0;i<inputs.size();i++){
                    if(inputs.get(i).getMbtiFactor().equals(r.getMbtiFactor().toString())){
                        final int index = i;
                        r.getEmotionAmountSums()
                                .forEach(ea -> {
                                    if(ea.getDate().equals(inputs.get(index).getDay())
                                            && ea.getAmountSum().equals(inputs.get(index).getAmountSum()))
                                        visited.set(index, true);
                                });
                    }
                }
            }

            assertThat(visited).doesNotContain(false);
        }
    }

    @Nested
    class 최근_90일_동안의_메모_빈도수를_얻는_메서드 {
        @Test
        void 현재_날짜를_레포에게_전달하면_DB에게_최근_90일의_데이터만_요청한다() throws Exception {
            // given
            LocalDate now = LocalDate.now();

            // when
            statisticsService.getWordFrequenciesLast90Days(now);

            // then
            verify(statisticsMapper)
                    .getAllMemosByMBTIBetweenStartDateAndEndDate(
                            any(),
                            captorStartDate.capture(),
                            captorEndDate.capture());

            assertThat(captorStartDate.getValue()).isEqualTo(now.minusDays(90));
            assertThat(captorEndDate.getValue()).isEqualTo(now);
        }

        // TODO 레포라는 이름 괜찮나?
        @Test
        void 레포에게_MBTI_파라미터로_로그인_한_유저의_MBTI와_NONE을_전달한다() throws Exception {
            // given
            String userMBTI = "ISTJ";
            when(userService.getUserMBTI())
                    .thenReturn(userMBTI);

            // when
            statisticsService.getWordFrequenciesLast90Days(LocalDate.now());
            
            // then
            verify(statisticsMapper, times(2))
                    .getAllMemosByMBTIBetweenStartDateAndEndDate(
                            captorString.capture(),
                            any(),
                            any());

            assertThat(captorString.getAllValues().get(0)).isEqualTo("none");
            assertThat(captorString.getAllValues().get(1)).isEqualTo(userMBTI);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"none"})
        void UserService로부터_받는_MBTI가_유효하지_않은_값이라면_전체_메모만_반환한다(String mbti) throws Exception {
            // given
            when(userService.getUserMBTI())
                    .thenReturn(mbti);

            // when
            WordFrequencyResponse response = statisticsService.getWordFrequenciesLast90Days(LocalDate.now());

            // then
            assertThat(response.getMyWordFrequencies())
                    .hasSize(0);
        }

        @Test
        void 레포로부터_리스트를_받고_이를_평면화하여_WordExtractionService에게_보낸다() throws Exception {
            // given
            List<AllMemoDto> memos = List.of(
                    new AllMemoDto("a1"),
                    new AllMemoDto("a2"),
                    new AllMemoDto("a3"),
                    new AllMemoDto("a4")
            );
            when(statisticsMapper.getAllMemosByMBTIBetweenStartDateAndEndDate(any(), any(), any()))
                    .thenReturn(memos);

            // when
            WordFrequencyResponse response = statisticsService.getWordFrequenciesLast90Days(LocalDate.now());

            // then
            verify(wordExtractionService)
                    .analyzeWords(captorWords.capture());

            List<String> value = captorWords.getValue();
            List<String> list = memos.stream().flatMap(m -> Stream.of(m.getThings(), m.getAny())).toList();

            assertThat(list).containsExactlyInAnyOrderElementsOf(value);
        }
    }

    @Nested
    class 최근_90일_동안의_MBTI별_만족도_평균을_얻는_메서드 {

        @ParameterizedTest
        @ValueSource(strings = {"SPEND", "SAVE"})
        void 입력한_RegisterType을_레포에게_그대로_입력한다(RegisterType registerType) throws Exception {
            // when
            statisticsService.getSatisfactionAveragesEachMBTILast90Days(LocalDate.now(), registerType);

            // then
            verify(statisticsMapper)
                    .getSatisfactionAveragesEachMBTIBetweenStartDateAndEndDate(
                            captorRegisterType.capture(),
                            any(),
                            any()
                    );


            assertThat(captorRegisterType.getValue()).isEqualTo(registerType);
        }

        @Test
        void 현재_날짜를_레포에게_전달하면_DB에게_최근_90일의_데이터만_요청한다() throws Exception {
            // given
            LocalDate now = LocalDate.now();

            // when
            statisticsService.getSatisfactionAveragesEachMBTILast90Days(now, null);

            // then
            verify(statisticsMapper)
                    .getSatisfactionAveragesEachMBTIBetweenStartDateAndEndDate(
                            any(),
                            captorStartDate.capture(),
                            captorEndDate.capture());

            assertThat(captorStartDate.getValue()).isEqualTo(now.minusDays(90));
            assertThat(captorEndDate.getValue()).isEqualTo(now);
        }
    }
}