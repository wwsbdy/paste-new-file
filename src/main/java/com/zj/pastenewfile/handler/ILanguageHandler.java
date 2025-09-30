package com.zj.pastenewfile.handler;

import com.intellij.ide.util.EditorHelper;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.zj.pastenewfile.utils.language.PluginBundle;
import com.zj.pastenewfile.vo.FileInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * {@link  com.zj.pastenewfile.utils.LanguageUtils}
 *
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public interface ILanguageHandler extends Serializable {

    default int order() {
        return Integer.MAX_VALUE;
    }

    @NotNull
    String getExtensionName();

    @Nullable
    FileInfo support(@NotNull Project project, @Nullable String input);

    boolean handle(@NotNull Project project, @Nullable String input, @NotNull PsiDirectory directory, @Nullable FileInfo fileInfo);

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
            rename(directory, psiFile, renameFileName);
        }
        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (!checkFileExist(directory, psiFile.getName())) {
                directory.add(psiFile);
            }
            // 跳转到文件
            PsiFile jumpFile = directory.findFile(psiFile.getName());
            if (jumpFile != null) {
                EditorHelper.openInEditor(jumpFile);
            }
        });
    }

    default void rename(@NotNull PsiDirectory directory, @NotNull PsiFile psiFile, @NotNull String renameFileName) {
        psiFile.setName(renameFileName);
    }

    default boolean checkFileExist(PsiDirectory directory, String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return false;
        }
        return Objects.nonNull(directory.findFile(fileName));
    }

}
