package com.zj.parsenewfile.handler;

import com.intellij.ide.highlighter.XmlFileType;

import java.util.regex.Pattern;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public class XmlHandler extends DefaultHandler {
    private static final Pattern PATTERN_ANGLE_BRACKET_START = Pattern.compile("^\\s*<[A-Za-z_]");

    public XmlHandler() {
        super(XmlFileType.INSTANCE, PATTERN_ANGLE_BRACKET_START);
    }
}
