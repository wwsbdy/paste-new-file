package com.zj.pastenewfile.handler;

import com.intellij.ide.highlighter.XmlFileType;
import com.zj.pastenewfile.enums.HandlerEnum;

import java.util.regex.Pattern;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public class XmlHandler extends DefaultHandler {
    private static final Pattern PATTERN_ANGLE_BRACKET_START = Pattern.compile("^\\s*<");

    public XmlHandler() {
        super(XmlFileType.INSTANCE, PATTERN_ANGLE_BRACKET_START);
    }

    @Override
    public int order() {
        return HandlerEnum.XML.getSort();
    }

}
