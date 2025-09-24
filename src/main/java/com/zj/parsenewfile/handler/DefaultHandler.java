package com.zj.parsenewfile.handler;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
@AllArgsConstructor
public class DefaultHandler implements ILanguageHandler {

    private final LanguageFileType fileType;
    private final Pattern pattern;

    @Override
    public boolean support(String input) {
        if (Objects.isNull(pattern)) {
            return true;
        }
        if (StringUtils.isNotBlank(input)) {
            return false;
        }
        return pattern.matcher(input.trim()).matches();
    }

    @Override
    public boolean handle(Project project, String input, PsiDirectory directory) {
        String fileName = "unknown";
        PsiFile file = PsiFileFactory.getInstance(project)
                .createFileFromText(fileName + "." + fileType.getDefaultExtension(), fileType, input);
        addFile(project, directory, file);
        return true;
    }
}
