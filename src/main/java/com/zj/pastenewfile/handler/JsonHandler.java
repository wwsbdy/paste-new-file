package com.zj.pastenewfile.handler;

import com.intellij.json.JsonFileType;

import java.util.regex.Pattern;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public class JsonHandler extends DefaultHandler {
    private static final Pattern PATTERN_JSON_START = Pattern.compile("^\\s*[{\\[]");

    public JsonHandler() {
        super(JsonFileType.INSTANCE, PATTERN_JSON_START);
    }
}
