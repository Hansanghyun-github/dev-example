package com.example.devexample.dev.statisticsTest;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class EmotionCountDto {
    private final Long maxCount;
    private final List<EmotionCount> emotionList;

    @Builder
    public EmotionCountDto(Long maxCount,
                           EmotionCount tCount,
                           EmotionCount jCount,
                           EmotionCount fCount,
                           EmotionCount pCount) {
        this.maxCount = maxCount;
        this.emotionList = List.of(tCount, jCount, fCount, pCount);
    }

    @Getter
    static class EmotionCount {
        private final Criteria criteria;
        private final List<Long> emotionCount;

        @Builder
        public EmotionCount(Criteria c, Long e1, Long e2, Long e3) {
            this.criteria = c;
            this.emotionCount = List.of(e1, e2, e3);
        }

        enum Criteria {
            T,J,F,P
        }

        enum Emotion {
            emotion1, emotion2, emotion3
        }
    }
}
