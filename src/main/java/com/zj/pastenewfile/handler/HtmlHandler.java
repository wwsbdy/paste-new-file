package com.zj.pastenewfile.handler;

import com.intellij.ide.highlighter.HtmlFileType;

import java.util.regex.Pattern;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public class HtmlHandler extends DefaultHandler {
    private static final Pattern PATTERN_HTML_ROOT = Pattern.compile("<html(?=\\s|>).*?>", Pattern.CASE_INSENSITIVE);

    public HtmlHandler() {
        super(HtmlFileType.INSTANCE, PATTERN_HTML_ROOT);
    }
}
