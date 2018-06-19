package com.saru.nicotrans.utils;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

public class TranslateUtil {
    private static final String JAPAN = "ja";
    private static final String KOREA = "ko";
    private static final int SPLIT_SIZE = 100;

    private static final Translate translate = TranslateOptions.newBuilder().build().getService();

    private TranslateUtil() {
    }

    public static List<Translation> translateList(List<String> sourceTexts) {
        Translate.TranslateOption srcLang = Translate.TranslateOption.sourceLanguage(JAPAN);
        Translate.TranslateOption tgtLang = Translate.TranslateOption.targetLanguage(KOREA);

        // GUAVA 활용해서 100개 단위로 나눈다
        List<List<String>> listOfList = Lists.partition(sourceTexts, SPLIT_SIZE);

        // 나눠진 리스트를 스트림으로 각각 번역 후 합쳐서 리스트로 리턴
        return listOfList.stream().map(list -> translate.translate(list, srcLang, tgtLang))
                .flatMap(List::stream).collect(Collectors.toList());
    }
}
