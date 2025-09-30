package com.zj.pastenewfile.enums;

import lombok.Getter;

/**
 * @author : jie.zhou
 * @date : 2025/9/30
 */
@Getter
public enum HandlerEnum {
    JAVA(0),
    KOTLIN(1),
    SQL(2),
    YAML(3),
    YML(4),
    PROPERTIES(5),
    HTML(6),
    XML(7),
    JSON(8),
    TXT(9),
    ;

    private final int sort;

    HandlerEnum(int sort) {
        this.sort = sort;
    }
}
