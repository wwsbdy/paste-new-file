package com.zj.pastenewfile.handler;

import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.zj.pastenewfile.vo.FileInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author : jie.zhou
 * @date : 2025/9/28
 */
public class PropertiesHandler implements ILanguageHandler {
    @Override
    public @NotNull String getExtensionName() {
        return "properties";
    }

    @Override
    public FileInfo support(@NotNull Project project, @Nullable String input) {
        if (StringUtils.isBlank(input)) {
            return null;
        }

        String text = input.trim();
        String lower = text.toLowerCase();

        // 1. 文件名后缀判断
        if (lower.endsWith(".properties")) {
            return new FileInfo("properties");
        }

        String[] lines = text.split("\\r?\\n");
        int validCount = 0;
        int totalCount = 0;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("!")) {
                continue;
            }
            totalCount++;

            // 典型的 properties key=value 格式，key 不允许有空格
            if (line.matches("^[^\\s=:+]+\\s*[=:]\\s*.+$")) {
                validCount++;
            }
        }

        // 必须有至少一行，并且大部分行都是有效的 key=value
        if (totalCount > 0 && validCount * 2 >= totalCount) {
            return new FileInfo("properties");
        }

        return null;
    }

    @Override
    public boolean handle(@NotNull Project project, @Nullable String input, @NotNull PsiDirectory directory, @Nullable FileInfo fileInfo) {
        if (Objects.isNull(input)) {
            input = "";
        }
        String fileName = Objects.isNull(fileInfo) ? "unknown" : fileInfo.getFileName();
        PsiFile file = PsiFileFactory.getInstance(project)
                .createFileFromText(fileName + "." + getExtensionName(), PlainTextFileType.INSTANCE, input);
        addFile(project, directory, file);
        return true;
    }
}
