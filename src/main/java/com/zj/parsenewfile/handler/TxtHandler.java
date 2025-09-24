package com.zj.parsenewfile.handler;

import com.intellij.openapi.fileTypes.PlainTextFileType;
import org.jetbrains.annotations.Nullable;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public class TxtHandler extends DefaultHandler {
    public TxtHandler() {
        super(PlainTextFileType.INSTANCE, null);
    }

    @Override
    public boolean notSupport(@Nullable String input) {
        return false;
    }
}
