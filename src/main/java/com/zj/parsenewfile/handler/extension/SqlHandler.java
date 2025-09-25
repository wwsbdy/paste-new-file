package com.zj.parsenewfile.handler.extension;

import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.zj.parsenewfile.vo.FileInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author : jie.zhou
 * @date : 2025/9/24
 */
public class SqlHandler implements IExtensionHandler {

    private static final Set<String> EXTENSIONS = new HashSet<>(
            Arrays.asList(
                    "select", "insert", "update", "delete", "create", "alter",
                    "drop", "truncate", "rename", "grant", "revoke", "call", "execute", "begin", "commit",
                    "rollback", "set", "show", "desc", "explain", "use")
    );

    @Override
    public @NotNull String getExtensionName() {
        return "sql";
    }

    @Override
    public FileInfo support(@NotNull Project project, @Nullable String input) {
        if (StringUtils.isBlank(input)) {
            return null;
        }
        input = input.trim().toLowerCase().substring(0, Math.min(10, input.length()));
        return input.startsWith("--")
                || EXTENSIONS.stream().anyMatch(input::startsWith)
                ? new FileInfo(getExtensionName()) : null;
    }

    @Override
    public boolean handle(@NotNull Project project, @Nullable String input, @NotNull PsiDirectory directory, @Nullable FileInfo fileInfo) {
        if (Objects.isNull(input)) {
            input = "";
        }
        String fileName = Objects.isNull(fileInfo) ? "unknown" : fileInfo.getFileName();
        PsiFile file = PsiFileFactory.getInstance(project)
                .createFileFromText(fileName + ".sql", PlainTextFileType.INSTANCE, input);
        addFile(project, directory, file);
        return true;
    }
}
