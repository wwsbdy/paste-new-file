package com.zj.parsenewfile.utils;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.lang.xml.XMLLanguage;
import com.zj.parsenewfile.vo.LanguageVo;

import java.util.List;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public class LanguageUtils {

    private static final List<LanguageVo> languageList;

    static {
        languageList = List.of(
                new LanguageVo(JavaLanguage.INSTANCE),
                new LanguageVo(XMLLanguage.INSTANCE)
        );
    }

    public static List<LanguageVo> getAllLanguage() {
        return languageList;
    }

}
