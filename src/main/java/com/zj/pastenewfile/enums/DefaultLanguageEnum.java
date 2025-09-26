package com.zj.pastenewfile.enums;

import com.zj.pastenewfile.handler.*;
import lombok.Getter;

/**
 * @author : jie.zhou
 * @date : 2025/9/25
 */
@Getter
public enum DefaultLanguageEnum {
    JSON("json", new JsonHandler()),
    HTML("html", new HtmlHandler()),
    XML("xml", new XmlHandler()),
    TXT("txt", new TxtHandler()),

    ;

    private final String name;
    private final ILanguageHandler languageHandler;

    DefaultLanguageEnum(String name, ILanguageHandler languageHandler) {
        this.name = name;
        this.languageHandler = languageHandler;
    }
}
