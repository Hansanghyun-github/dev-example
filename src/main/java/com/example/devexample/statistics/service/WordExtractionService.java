package com.example.devexample.statistics.service;

import com.example.devexample.statistics.controller.dto.WordFrequencyResponse;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WordExtractionService {
    private final Komoran komoran;

    public WordExtractionService() {
        this.komoran = new Komoran(DEFAULT_MODEL.FULL);
    }

    public List<WordFrequencyResponse.WordFrequency> analyzeWords(List<String> memos){
        /**
         * 3. koroman.analyze() // 형태소 분석
         * 4. getTokenList() // 토큰 리스트로 반환
         * 5. 명사 형용사 감탄사 명사만 가져오고, 명사 & 형용사에 '다' 붙인다.
         * 6. 각 단어 빈도수 정리
         * */

        log.info("before erase:\n" + memos.toString());

        List<String> keyWords = getKeyWords(getTokens(memos));

        log.info("after erase:\n" + keyWords.toString());

        return keyWords.stream()
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(e -> WordFrequencyResponse.WordFrequency.builder()
                        .word(e.getKey())
                        .frequency(e.getValue())
                        .build())
                .toList();
    }

    private List<Token> getTokens(List<String> memos) {
        final int NUM_THREADS = 1;
        return komoran.analyze(memos, NUM_THREADS)
                .stream()
                .map(KomoranResult::getTokenList)
                .flatMap(Collection::stream)
                .toList();
    }

    private List<String> getKeyWords(List<Token> tokens) {
        return tokens.stream()
                .filter(this::isKeyWord)
                .map(t -> {
                    if (isVerbOrAdjective(t))
                        return t.getMorph().concat("다");
                    return t.getMorph();
                })
                .toList();
    }

    private boolean isKeyWord(Token token){
        // KOMORAN 라이브러리에서 제공하는 명사, 대명사, 동사, 형용사, 외국어에 해당하는 pos
        // https://docs.komoran.kr/firststep/postypes.html
        return token.getPos().equals("NNG") ||
                token.getPos().equals("NNP") ||
                token.getPos().equals("NP") ||
                token.getPos().equals("VV") ||
                token.getPos().equals("VA") ||
                token.getPos().equals("SL");
    }

    private boolean isVerbOrAdjective(Token token) {
        return token.getPos().startsWith("V");
    }
}
