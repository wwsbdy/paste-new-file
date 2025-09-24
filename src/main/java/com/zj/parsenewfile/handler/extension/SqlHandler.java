package com.zj.parsenewfile.handler.extension;

import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author : jie.zhou
 * @date : 2025/9/24
 */
public class SqlHandler implements IExtensionHandler {
    @Override
    public @NotNull String getName() {
        return "SQL";
    }

    @Override
    public boolean notSupport(@Nullable String input) {
        if (StringUtils.isBlank(input)) {
            return true;
        }
        return !input.startsWith("--") && !input.startsWith("select");
    }

    @Override
    public boolean handle(@NotNull Project project, @Nullable String input, @NotNull PsiDirectory directory) {
        if (StringUtils.isBlank(input) || notSupport(input)) {
            return false;
        }
        String fileName = "unknown";
        PsiFile file = PsiFileFactory.getInstance(project)
                .createFileFromText(fileName + ".sql", PlainTextFileType.INSTANCE, input);
        addFile(project, directory, file);
        return true;
    }
}
