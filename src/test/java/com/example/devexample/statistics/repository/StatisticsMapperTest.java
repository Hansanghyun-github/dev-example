package com.example.devexample.statistics.repository;

import com.example.devexample.article.entity.Article;
import com.example.devexample.article.entity.BaseEntity;
import com.example.devexample.article.entity.RegisterType;
import com.example.devexample.user.entity.User;
import com.example.devexample.article.repository.ArticleRepository;
import com.example.devexample.user.repository.UserRepository;
import com.example.devexample.statistics.repository.dto.AllMemoDto;
import com.example.devexample.statistics.repository.dto.MBTIDailyAmountSumDto;
import com.example.devexample.statistics.repository.dto.MBTIEmotionAmountAverageDto;
import com.example.devexample.statistics.repository.dto.MBTISatisfactionAverageDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StatisticsMapperTest {
    @Autowired
    StatisticsMapper statisticsMapper;

    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em; // flush 하기 위해 필요 (JPA 쓰기 지연 방지)

    @BeforeAll
    public static void createView(@Autowired DataSource dataSource) {
        // JPA에서 기존 테이블 생성 해줬지만, 뷰는 생성 안 됨
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("mybatis-schema.sql"));
        populator.execute(dataSource);
    }

    @Nested
    class MBTI별_감정별_금액_평균을_반환하는_메서드 {
        @Test
        void 입력받은_startDate와_endDate를_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .nickname("Han")
                            .mbti("ISTJ")
                            .gender("Male")
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            String registerType = "SPEND";
            String removedEmotion = "bad";
            String survivedEmotion = "good";
            List<Article> removedArticles = List.of(
                    makeArticle(user, registerType, removedEmotion, period.startDate.atStartOfDay().minusSeconds(1L), 1000),
                    makeArticle(user, registerType, removedEmotion, period.endDate.atStartOfDay().plusSeconds(1L), 1000)
                    );
            List<Article> articles = List.of(
                    makeArticle(user, registerType, survivedEmotion, period.startDate.atStartOfDay(), 1000),
                    makeArticle(user, registerType, survivedEmotion, period.endDate.atStartOfDay(), 1000)
            );
            em.flush();

            // when
            List<MBTIEmotionAmountAverageDto> dtos = statisticsMapper.getAmountAveragesEachMBTIAndEmotionBetweenStartDateAndEndDate(
                    RegisterType.valueOf(registerType),
                    period.startDate,
                    period.endDate
            );

            Query nativeQuery = em.createNativeQuery("select u.ei as mbtiFactor, a.emotion, round(avg(a.amount), -3) as amountAverage\n" +
                    "        from articles a join users_mbti_view u on(a.user_id = u.user_id)\n" +
                    "        where a.register_type = 'SPEND' and a.created_date between '2024-05-01' and '2024-05-06' \n" +
                    "        group by mbtiFactor, a.emotion");
            List<Object[]> resultList = nativeQuery.getResultList();
            for(Object[] r: resultList){
                String c = r[0].toString();
                String s = (String) r[1];
                Long v = (Long)((Double)r[2]).longValue();
                System.out.println(c+ " "+s+" "+v);
            }

            // then
            assertThat(dtos)
                    .extracting(MBTIEmotionAmountAverageDto::getEmotion)
                    .containsOnly(survivedEmotion)
                    .isNotIn(removedEmotion);
        }

        @Test
        void 입력받은_registerType을_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .nickname("Han")
                            .mbti("ISTJ")
                            .gender("Male")
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            String removedRegisterType = "SAVE";
            String registerType = "SPEND";
            String removedEmotion = "bad";
            String survivedEmotion = "good";
            List<Article> removedArticles = List.of(
                    makeArticle(user, removedRegisterType, removedEmotion, period.startDate.atStartOfDay(), 1000),
                    makeArticle(user, removedRegisterType, removedEmotion, period.endDate.atStartOfDay(), 1000)
            );
            List<Article> articles = List.of(
                    makeArticle(user, registerType, survivedEmotion, period.startDate.atStartOfDay(), 1000),
                    makeArticle(user, registerType, survivedEmotion, period.endDate.atStartOfDay(), 1000)
            );
            em.flush();

            // when
            List<MBTIEmotionAmountAverageDto> dtos = statisticsMapper.getAmountAveragesEachMBTIAndEmotionBetweenStartDateAndEndDate(
                    RegisterType.valueOf(registerType),
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(dtos)
                    .extracting(MBTIEmotionAmountAverageDto::getEmotion)
                    .containsOnly(survivedEmotion)
                    .isNotIn(removedEmotion);
        }

        @Test
        @Order(1)
        void MBTI별_감정별_금액의_평균을_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .nickname("Han")
                            .mbti("ISTJ")
                            .gender("Male")
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            String registerType = "SPEND";
            String emotion = "good";
            List<Article> articles = List.of(
                    makeArticle(user, registerType, emotion, period.startDate.atStartOfDay(), 1000),
                    makeArticle(user, registerType, emotion, period.endDate.atStartOfDay(), 2000),
                    makeArticle(user, registerType, emotion, period.endDate.atStartOfDay(), 3000)
            );
            Long amountAverage = (long) articles.stream()
                    .map(Article::getAmount)
                    .reduce(Integer::sum)
                    .get() / articles.size();

            em.flush();

            // when
            List<MBTIEmotionAmountAverageDto> dtos = statisticsMapper.getAmountAveragesEachMBTIAndEmotionBetweenStartDateAndEndDate(
                    RegisterType.valueOf(registerType),
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(dtos)
                    .extracting(MBTIEmotionAmountAverageDto::getEmotion)
                    .containsOnly(emotion);
            assertThat(dtos)
                    .extracting(MBTIEmotionAmountAverageDto::getAmountAverage)
                    .containsOnly(amountAverage);
        }

        @Test
        @Order(2)
        void 금액의_평균을_반활할_때_100의_자리수에서_반올림된다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .nickname("Han")
                            .mbti("ISTJ")
                            .gender("Male")
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            String registerType = "SPEND";
            String emotion = "good";
            List<Article> articles = List.of(
                    makeArticle(user, registerType, emotion, period.startDate.atStartOfDay(), 1100),
                    makeArticle(user, registerType, emotion, period.endDate.atStartOfDay(), 2200),
                    makeArticle(user, registerType, emotion, period.endDate.atStartOfDay(), 3300)
            );
            Long amountAverage = (long) articles.stream()
                    .map(Article::getAmount)
                    .reduce(Integer::sum)
                    .get() / articles.size();
            amountAverage = roundingAverage(amountAverage);

            em.flush();

            // when
            List<MBTIEmotionAmountAverageDto> dtos = statisticsMapper.getAmountAveragesEachMBTIAndEmotionBetweenStartDateAndEndDate(
                    RegisterType.valueOf(registerType),
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(dtos)
                    .extracting(MBTIEmotionAmountAverageDto::getEmotion)
                    .containsOnly(emotion);
            assertThat(dtos)
                    .extracting(MBTIEmotionAmountAverageDto::getAmountAverage)
                    .containsOnly(amountAverage);
        }
    }

    @Nested
    class MBTI별_일별_금액_합을_반환하는_메서드 {
        @Test
        void 입력받은_startDate와_endDate를_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .nickname("Han")
                            .mbti("ISTJ")
                            .gender("Male")
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            String registerType = "SPEND";
            List<Article> removedArticles = List.of(
                    makeArticle(user, registerType, null, period.startDate.atStartOfDay().minusSeconds(1L), 1000),
                    makeArticle(user, registerType, null, period.endDate.atStartOfDay().plusSeconds(1L), 1000)
            );
            List<Article> articles = List.of(
                    makeArticle(user, registerType, null, period.startDate.atStartOfDay(), 1000),
                    makeArticle(user, registerType, null, period.endDate.atStartOfDay(), 1000)
            );
            em.flush();

            // when
            List<MBTIDailyAmountSumDto> dtos = statisticsMapper.getAmountSumsEachMBTIAndDayBetweenStartDateAndEndDate(
                    RegisterType.valueOf(registerType),
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(dtos)
                    .extracting(MBTIDailyAmountSumDto::getLocalDate)
                    .containsAll(
                            articles.stream()
                                    .map(a -> a.getCreatedDate().toLocalDate())
                                    .toList()
                    )
                    .doesNotContain(
                            period.startDate.minusDays(1),
                            period.endDate.plusDays(1));
        }

        @Test
        void 입력받은_registerType을_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .nickname("Han")
                            .mbti("ISTJ")
                            .gender("Male")
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            String removedRegisterType = "SAVE";
            String registerType = "SPEND";
            List<Article> removedArticles = List.of(
                    makeArticle(user, removedRegisterType, null, period.startDate.atStartOfDay().minusSeconds(1L), 1000),
                    makeArticle(user, removedRegisterType, null, period.endDate.atStartOfDay().plusSeconds(1L), 1000)
            );
            List<Article> articles = List.of(
                    makeArticle(user, registerType, null, period.startDate.atStartOfDay(), 1000),
                    makeArticle(user, registerType, null, period.endDate.atStartOfDay(), 1000)
            );
            em.flush();

            // when
            List<MBTIDailyAmountSumDto> dtos = statisticsMapper.getAmountSumsEachMBTIAndDayBetweenStartDateAndEndDate(
                    RegisterType.valueOf(registerType),
                    period.startDate,
                    period.endDate
            );

            // then
            // TODO 날짜를 기준으로 객체를 찾고 싶다
            assertThat(dtos)
                    .extracting(MBTIDailyAmountSumDto::getLocalDate)
                    .containsAll(
                            articles.stream()
                                    .map(a -> a.getCreatedDate().toLocalDate())
                                    .toList()
                    )
                    .doesNotContain(
                            period.startDate.minusDays(1),
                            period.endDate.plusDays(1));
        }

        @Test
        void MBTI별_일별_금액의_합을_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .nickname("Han")
                            .mbti("ISTJ")
                            .gender("Male")
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            String registerType = "SPEND";
            String emotion = "good";
            List<Article> articles = List.of(
                    makeArticle(user, registerType, emotion, period.startDate.atStartOfDay(), 1000),
                    makeArticle(user, registerType, emotion, period.startDate.atStartOfDay(), 2000),
                    makeArticle(user, registerType, emotion, period.startDate.atStartOfDay(), 3000)
            );
            Integer sum = articles.stream()
                    .map(Article::getAmount)
                    .reduce(Integer::sum).orElseGet(() -> -1);
            em.flush();

            // when
            List<MBTIDailyAmountSumDto> dtos = statisticsMapper.getAmountSumsEachMBTIAndDayBetweenStartDateAndEndDate(
                    RegisterType.valueOf(registerType),
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(dtos)
                    .extracting(MBTIDailyAmountSumDto::getAmountSum)
                    .containsOnly((long)sum);
        }
    }
    @Nested
    class 단어_빈도수를_반환하는_메서드 {
        @Test
        void 입력받은_startDate와_endDate를_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .nickname("Han")
                            .mbti("ISTJ")
                            .gender("Male")
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));

            String things = "things";
            String removedThings = "removed";
            List<Article> removedArticles = List.of(
                    makeArticle(user, "SPEND", null, period.startDate.atStartOfDay().minusSeconds(1L), removedThings),
                    makeArticle(user, "SPEND", null, period.endDate.atStartOfDay().plusSeconds(1L), removedThings)
            );
            List<Article> articles = List.of(
                    makeArticle(user, "SPEND", null, period.startDate.atStartOfDay(), things),
                    makeArticle(user, "SPEND", null, period.endDate.atStartOfDay(), things)
            );
            em.flush();

            // when
            List<AllMemoDto> dtos = statisticsMapper.getAllMemosByMBTIBetweenStartDateAndEndDate(
                    "none",
                    period.startDate,
                    period.endDate
            );
            System.out.println(articleRepository.findAll());
            System.out.println(dtos);

            // then
            assertThat(dtos)
                    .extracting(AllMemoDto::getThings)
                    .containsOnly(things)
                    .doesNotContain(removedThings);
        }

        @Test
        void MBTI를_입력하면_해당_MBTI_유저들의_메모만_반환된다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .nickname("Han")
                            .mbti("ISTJ")
                            .gender("Male")
                            .build()
            );
            User otherUser = userRepository.save(
                    User.builder()
                            .nickname("Han")
                            .mbti("ENFP")
                            .gender("Male")
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));

            String things = "things";
            String removedThings = "removed";
            List<Article> removedArticles = List.of(
                    makeArticle(otherUser, "SPEND", null, period.startDate.atStartOfDay(), removedThings),
                    makeArticle(otherUser, "SPEND", null, period.endDate.atStartOfDay(), removedThings)
            );
            List<Article> articles = List.of(
                    makeArticle(user, "SPEND", null, period.startDate.atStartOfDay(), things),
                    makeArticle(user, "SPEND", null, period.endDate.atStartOfDay(), things)
            );
            em.flush();

            // when
            List<AllMemoDto> dtos = statisticsMapper.getAllMemosByMBTIBetweenStartDateAndEndDate(
                    user.getMbti(),
                    period.startDate,
                    period.endDate
            );
            System.out.println(articleRepository.findAll());
            System.out.println(dtos);

            // then
            assertThat(dtos)
                    .extracting(AllMemoDto::getThings)
                    .containsOnly(things)
                    .doesNotContain(removedThings);
        }

        @Test
        void MBTI를_입력하지_않으면_모든_유저들의_메모가_반환된다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .nickname("Han")
                            .mbti("ISTJ")
                            .gender("Male")
                            .build()
            );
            User otherUser = userRepository.save(
                    User.builder()
                            .nickname("Han")
                            .mbti("ENFP")
                            .gender("Male")
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));

            String things = "things";
            String otherThings = "removed";
            List<Article> removedArticles = List.of(
                    makeArticle(otherUser, "SPEND", null, period.startDate.atStartOfDay(), otherThings),
                    makeArticle(otherUser, "SPEND", null, period.endDate.atStartOfDay(), otherThings)
            );
            List<Article> articles = List.of(
                    makeArticle(user, "SPEND", null, period.startDate.atStartOfDay(), things),
                    makeArticle(user, "SPEND", null, period.endDate.atStartOfDay(), things)
            );
            em.flush();

            // when
            List<AllMemoDto> dtos = statisticsMapper.getAllMemosByMBTIBetweenStartDateAndEndDate(
                    "none",
                    period.startDate,
                    period.endDate
            );
            System.out.println(articleRepository.findAll());
            System.out.println(dtos);

            // then
            assertThat(dtos)
                    .extracting(AllMemoDto::getThings)
                    .contains(things)
                    .contains(otherThings);
        }

    }
    @Nested
    class MBTI별_만족도_평균을_반환하는_메서드 {
        @Test
        void 입력받은_startDate와_endDate를_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .nickname("Han")
                            .mbti("ISTJ")
                            .gender("Male")
                            .build()
            );
            User otherUser = userRepository.save(
                    User.builder()
                            .nickname("other")
                            .mbti("ENFP")
                            .gender("Female")
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            String registerType = "SPEND";
            List<Article> removedArticles = List.of(
                    makeArticle(otherUser, registerType, null, period.startDate.atStartOfDay().minusSeconds(1L), 1.0f),
                    makeArticle(otherUser, registerType, null, period.endDate.atStartOfDay().plusSeconds(1L), 1.0f)
            );
            List<Article> articles = List.of(
                    makeArticle(user, registerType, null, period.startDate.atStartOfDay(), 1000),
                    makeArticle(user, registerType, null, period.endDate.atStartOfDay(), 1000)
            );
            em.flush();

            // when
            List<MBTISatisfactionAverageDto> dtos = statisticsMapper.getSatisfactionAveragesEachMBTIBetweenStartDateAndEndDate(
                    RegisterType.valueOf(registerType),
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(dtos)
                    .extracting(MBTISatisfactionAverageDto::getMbtiFactor)
                    .contains(user.getMbti().split(""))
                    .doesNotContain(otherUser.getMbti().split(""));
        }

        @Test
        void 입력받은_registerType을_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .nickname("Han")
                            .mbti("ISTJ")
                            .gender("Male")
                            .build()
            );
            User otherUser = userRepository.save(
                    User.builder()
                            .nickname("other")
                            .mbti("ENFP")
                            .gender("Female")
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            String removedRegisterType = "SAVE";
            String registerType = "SPEND";
            List<Article> removedArticles = List.of(
                    makeArticle(otherUser, removedRegisterType, null, period.startDate.atStartOfDay(), 1.0f),
                    makeArticle(otherUser, removedRegisterType, null, period.endDate.atStartOfDay(), 1.0f)
            );
            List<Article> articles = List.of(
                    makeArticle(user, registerType, null, period.startDate.atStartOfDay(), 1000),
                    makeArticle(user, registerType, null, period.endDate.atStartOfDay(), 1000)
            );
            em.flush();

            // when
            List<MBTISatisfactionAverageDto> dtos = statisticsMapper.getSatisfactionAveragesEachMBTIBetweenStartDateAndEndDate(
                    RegisterType.valueOf(registerType),
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(dtos)
                    .extracting(MBTISatisfactionAverageDto::getMbtiFactor)
                    .contains(user.getMbti().split(""))
                    .doesNotContain(otherUser.getMbti().split(""));
        }

        @Test
        @Order(1)
        void MBTI별_만족도의_평균을_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .nickname("Han")
                            .mbti("ISTJ")
                            .gender("Male")
                            .build()
            );
            User otherUser = userRepository.save(
                    User.builder()
                            .nickname("other")
                            .mbti("ENFP")
                            .gender("Female")
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            String registerType = "SPEND";
            List<Article> removedArticles = List.of(
                    makeArticle(otherUser, registerType, null, period.startDate.atStartOfDay(), 1.0f),
                    makeArticle(otherUser, registerType, null, period.endDate.atStartOfDay(), 2.0f)
            );
            List<Article> articles = List.of(
                    makeArticle(user, registerType, null, period.startDate.atStartOfDay(), 3.0f),
                    makeArticle(user, registerType, null, period.endDate.atStartOfDay(), 4.0f)
            );
            em.flush();

            // when
            List<MBTISatisfactionAverageDto> dtos = statisticsMapper.getSatisfactionAveragesEachMBTIBetweenStartDateAndEndDate(
                    RegisterType.valueOf(registerType),
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(
                    dtos.stream()
                            .filter(d -> {
                                String factor = d.getMbtiFactor();
                                if(Arrays.stream(user.getMbti().split("")).anyMatch(m -> m.equals(factor)))
                                    return true;
                                return false;
                            })
                            .map(MBTISatisfactionAverageDto::getSatisfactionAverage)
                            .toList()
            ).containsOnly(3.5f);
            assertThat(
                    dtos.stream()
                            .filter(d -> {
                                String factor = d.getMbtiFactor();
                                if(Arrays.stream(otherUser.getMbti().split("")).anyMatch(m -> m.equals(factor)))
                                    return true;
                                return false;
                            })
                            .map(MBTISatisfactionAverageDto::getSatisfactionAverage)
                            .toList()
            ).containsOnly(1.5f);
        }

        @Test
        @Order(2)
        void 만족도의_평균을_소수_둘째_자리에서_반올림해서_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .nickname("Han")
                            .mbti("ISTJ")
                            .gender("Male")
                            .build()
            );
            User otherUser = userRepository.save(
                    User.builder()
                            .nickname("other")
                            .mbti("ENFP")
                            .gender("Female")
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            String registerType = "SPEND";
            List<Article> removedArticles = List.of(
                    makeArticle(otherUser, registerType, null, period.startDate.atStartOfDay(), 1.04f),
                    makeArticle(otherUser, registerType, null, period.endDate.atStartOfDay(), 2.0f)
            );
            List<Article> articles = List.of(
                    makeArticle(user, registerType, null, period.startDate.atStartOfDay(), 3.04f),
                    makeArticle(user, registerType, null, period.endDate.atStartOfDay(), 4.0f)
            );
            em.flush();

            // when
            List<MBTISatisfactionAverageDto> dtos = statisticsMapper.getSatisfactionAveragesEachMBTIBetweenStartDateAndEndDate(
                    RegisterType.valueOf(registerType),
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(
                    dtos.stream()
                            .filter(d -> {
                                String factor = d.getMbtiFactor();
                                if(Arrays.stream(user.getMbti().split("")).anyMatch(m -> m.equals(factor)))
                                    return true;
                                return false;
                            })
                            .map(MBTISatisfactionAverageDto::getSatisfactionAverage)
                            .toList()
            ).containsOnly(3.5f);
            assertThat(
                    dtos.stream()
                            .filter(d -> {
                                String factor = d.getMbtiFactor();
                                if(Arrays.stream(otherUser.getMbti().split("")).anyMatch(m -> m.equals(factor)))
                                    return true;
                                return false;
                            })
                            .map(MBTISatisfactionAverageDto::getSatisfactionAverage)
                            .toList()
            ).containsOnly(1.5f);
        }
    }
    Article makeArticle(User user, String registerType, String emotion, LocalDateTime dateTime, int amount) {
        Article article = Article.builder()
                .register_type(registerType)
                .emotion(emotion)
                .amount(amount)
                .user(user)
                .build();
        articleRepository.save(article);

        try {
            Field createdDate = BaseEntity.class.getDeclaredField("createdDate");
            createdDate.setAccessible(true);
            createdDate.set(article, dateTime);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return article;
    }
    Article makeArticle(User user, String registerType, String emotion, LocalDateTime dateTime, float satisfaction) {
        Article article = Article.builder()
                .register_type(registerType)
                .emotion(emotion)
                .satisfaction((int)satisfaction)
                .user(user)
                .build();
        articleRepository.save(article);

        try {
            Field createdDate = BaseEntity.class.getDeclaredField("createdDate");
            createdDate.setAccessible(true);
            createdDate.set(article, dateTime);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return article;
    }

    Article makeArticle(User user, String registerType, String emotion, LocalDateTime dateTime, String things) {
        Article article = Article.builder()
                .register_type(registerType)
                .emotion(emotion)
                .things(things)
                .user(user)
                .build();
        articleRepository.save(article);

        try {
            Field createdDate = BaseEntity.class.getDeclaredField("createdDate");
            createdDate.setAccessible(true);
            createdDate.set(article, dateTime);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return article;
    }

    private static Long roundingAverage(Long amountAverage) {
        amountAverage += 500L;
        amountAverage = amountAverage - (amountAverage % 1000);
        return amountAverage;
    }
    @Getter
    @AllArgsConstructor
    static class DatePeriod {

        private LocalDate startDate;
        private LocalDate endDate;
    }
}