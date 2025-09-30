package com.zj.pastenewfile.handler;

import com.intellij.ide.highlighter.HtmlFileType;
import com.zj.pastenewfile.enums.HandlerEnum;

import java.util.regex.Pattern;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public class HtmlHandler extends DefaultHandler {
    private static final Pattern PATTERN_HTML_ROOT = Pattern.compile("^\\s*<html(?=\\s|>).*?>", Pattern.CASE_INSENSITIVE);

    public HtmlHandler() {
        super(HtmlFileType.INSTANCE, PATTERN_HTML_ROOT);
    }

    @Override
    public int order() {
        return HandlerEnum.HTML.getSort();
    }
}
