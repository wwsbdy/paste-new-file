package com.zj.parsenewfile.handler;

import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.zj.parsenewfile.vo.FileInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public class TxtHandler extends DefaultHandler {

    public static final FileInfo FILE_INFO = new FileInfo("txt");

    public TxtHandler() {
        super(PlainTextFileType.INSTANCE, null);
    }

    @Override
    public FileInfo support(@NotNull Project project, @Nullable String input) {
        return FILE_INFO;
    }
}
