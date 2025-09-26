package com.zj.pastenewfile.handler;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.zj.pastenewfile.vo.FileInfo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
@AllArgsConstructor
public class DefaultHandler implements ILanguageHandler {

    @NotNull
    private final LanguageFileType fileType;
    @Nullable
    private final Pattern pattern;

    @Override
    public @NotNull String getExtensionName() {
        return fileType.getDefaultExtension();
    }

    @Override
    public FileInfo support(@NotNull Project project, @Nullable String input) {
        if (Objects.isNull(pattern)) {
            return new FileInfo(getExtensionName());
        }
        if (StringUtils.isBlank(input)) {
            return null;
        }
        return pattern.matcher(input.trim()).find() ? new FileInfo(getExtensionName()) : null;
    }

    @Override
    public boolean handle(@NotNull Project project, @Nullable String input, @NotNull PsiDirectory directory, @Nullable FileInfo fileInfo) {
        if (Objects.isNull(input)) {
            input = "";
        }
        String fileName = Objects.isNull(fileInfo) ? "unknown" : fileInfo.getFileName();
        PsiFile file = PsiFileFactory.getInstance(project)
                .createFileFromText(fileName + "." + fileType.getDefaultExtension(), fileType, input);
        addFile(project, directory, file);
        return true;
    }
}
