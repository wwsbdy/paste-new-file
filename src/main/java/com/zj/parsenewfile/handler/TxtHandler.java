package com.zj.parsenewfile.handler;

import com.intellij.openapi.fileTypes.PlainTextFileType;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public class TxtHandler extends DefaultHandler {
    public TxtHandler() {
        super(PlainTextFileType.INSTANCE, null);
    }

    @Override
    public boolean support(String input) {
        return true;
    }
}
