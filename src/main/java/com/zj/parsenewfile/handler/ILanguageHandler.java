package com.zj.parsenewfile.handler;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.zj.parsenewfile.utils.language.PluginBundle;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public interface ILanguageHandler {

    @NotNull
    String getName();

    boolean notSupport(@Nullable String input);

    boolean handle(@NotNull Project project, @Nullable String input, @NotNull PsiDirectory directory);

    default void addFile(@NotNull Project project, @NotNull PsiDirectory directory, @NotNull PsiFile psiFile) {
        if (StringUtils.isEmpty(psiFile.getName())) {
            return;
        }
        if (checkFileExist(directory, psiFile.getName())) {
            // 弹窗输入新文件名
            String renameFileName = Messages.showInputDialog(
                    project,
                    null,
                    PluginBundle.get("message.title.file-exists"),
                    null,
                    psiFile.getName(),
                    null
            );
            if (StringUtils.isEmpty(renameFileName)) {
                return;
            }
            if (checkFileExist(directory, renameFileName)) {
                return;
            }
            psiFile.setName(renameFileName);
        }
        WriteCommandAction.runWriteCommandAction(project, () -> {
            directory.add(psiFile);
        });
    }

    default boolean checkFileExist(PsiDirectory directory, String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return false;
        }
        return Objects.nonNull(directory.findFile(fileName));
    }

}
