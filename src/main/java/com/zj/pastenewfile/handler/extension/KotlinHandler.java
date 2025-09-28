package com.zj.pastenewfile.handler.extension;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.zj.pastenewfile.utils.log.Logger;
import com.zj.pastenewfile.vo.FileInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.idea.KotlinFileType;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.psi.KtFile;

import java.util.Objects;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public class KotlinHandler extends JavaHandler {

    private static final Logger logger = Logger.getInstance(KotlinHandler.class);

    @Override
    public @NotNull String getExtensionName() {
        return "kt";
    }

    @Override
    public FileInfo support(@NotNull Project project, String input) {
        if (StringUtils.isBlank(input)) {
            return null;
        }
        PsiFile tempFile = PsiFileFactory.getInstance(project)
                .createFileFromText("Temp121" + "." + getExtensionName(), KotlinFileType.INSTANCE, input);
        if (tempFile instanceof KtFile) {
            KtFile ktFile = (KtFile) tempFile;
            // 1. 获取类名
            return ktFile.getClasses().length > 0
                    && StringUtils.isNotBlank(ktFile.getClasses()[0].getName())
                    ? new FileInfo(ktFile.getClasses()[0].getName(), getExtensionName()) : null;
        }
        return null;
    }

    @Override
    public boolean handle(@NotNull Project project, @Nullable String input, @NotNull PsiDirectory directory, @Nullable FileInfo fileInfo) {
        if (StringUtils.isBlank(input)) {
            return false;
        }
        // 先用 PsiFileFactory 解析输入文本
        PsiFile tempFile = PsiFileFactory.getInstance(project)
                .createFileFromText("Temp" + "." + getExtensionName(), KotlinFileType.INSTANCE, input);
        String fileName = Objects.isNull(fileInfo) ? null : fileInfo.getFileName() + "." + getExtensionName();
        PsiFile file;

        if (tempFile instanceof KtFile) {
            KtFile ktFile = (KtFile) tempFile;
            // 1. 获取类名
            if (ktFile.getClasses().length > 0 && StringUtils.isNotEmpty(fileName)) {
                String newClass = fileName.replaceAll("." + getExtensionName(), "");
                String oldClass = ktFile.getClasses()[0].getName();
                if (!newClass.equals(oldClass)) {
                    PsiClass psiClass = ktFile.getClasses()[0];
                    // TODO 没有改名成功
                    renameClass(newClass, psiClass, project);
                }
            } else {
                logger.info("找不到类名");
                fileName = "Kotlin" + "." + getExtensionName();
            }
            // 2. 替换/添加 package
            PsiPackage aPackage = JavaDirectoryService.getInstance().getPackage(directory);
            if (Objects.nonNull(aPackage) && StringUtils.isNotBlank(aPackage.getQualifiedName())) {
                String pkg = aPackage.getQualifiedName();
                ktFile.setPackageFqName(new FqName(pkg));
            }
            ktFile.setName(fileName);
            file = ktFile;
        } else {
            // fallback: 无法解析成 Java 文件
            fileName = fileName == null ? "Kotlin" + "." + getExtensionName() : fileName + "." + getExtensionName();
            file = PsiFileFactory.getInstance(project)
                    .createFileFromText(fileName, KotlinFileType.INSTANCE, input);
        }
        addFile(project, directory, file);
        return true;
    }

    @Override
    public void rename(@NotNull PsiFile psiFile, String renameFileName) {
        Project project = psiFile.getProject();
        if (psiFile instanceof KtFile) {
            KtFile ktFile = (KtFile) psiFile;
            if (ktFile.getClasses().length > 0) {
                PsiClass psiClass = ktFile.getClasses()[0];
                String newName = renameFileName.replaceAll("." + getExtensionName(), "");
                renameClass(newName, psiClass, project);
            }
        }
        super.rename(psiFile, renameFileName);
    }
}
