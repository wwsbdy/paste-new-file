package com.zj.parsenewfile.handler;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;

import java.util.Arrays;
import java.util.List;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public interface ILanguageHandler {

    List<ILanguageHandler> HANDLERS = Arrays.asList(
            new JavaHandler(),
            new JsonHandler(),
            new HtmlHandler(),
            new XmlHandler(),
            new TxtHandler()
    );

    boolean support(String input);

    boolean handle(Project project, String input, PsiDirectory directory);

    default void addFile(Project project, PsiDirectory directory, PsiFile psiFile) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            directory.add(psiFile);
        });
    }

}
