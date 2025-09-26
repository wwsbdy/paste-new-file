package com.zj.pastenewfile.handler;

import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.zj.pastenewfile.vo.FileInfo;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author : jie.zhou
 * @date : 2025/9/25
 */
@AllArgsConstructor
public class UnknownHandler implements ILanguageHandler {

    private final String fileType;

    @Override
    public @NotNull String getExtensionName() {
        return fileType;
    }

    @Override
    public FileInfo support(@NotNull Project project, @Nullable String input) {
        return new FileInfo(getExtensionName());
    }

    @Override
    public boolean handle(@NotNull Project project, @Nullable String input, @NotNull PsiDirectory directory, @Nullable FileInfo fileInfo) {
        if (Objects.isNull(input)) {
            input = "";
        }
        String fileName = Objects.isNull(fileInfo) ? "unknown" : fileInfo.getFileName();
        PsiFile file = PsiFileFactory.getInstance(project)
                .createFileFromText(fileName + "." + fileType, PlainTextFileType.INSTANCE, input);
        addFile(project, directory, file);
        return false;
    }
}
