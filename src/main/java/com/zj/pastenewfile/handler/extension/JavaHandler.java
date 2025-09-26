package com.zj.pastenewfile.handler.extension;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.zj.pastenewfile.utils.log.Logger;
import com.zj.pastenewfile.vo.FileInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public class JavaHandler implements IExtensionHandler {

    private static final Logger logger = Logger.getInstance(JavaHandler.class);

    @Override
    public @NotNull String getExtensionName() {
        return "java";
    }

    @Override
    public FileInfo support(@NotNull Project project, String input) {
        if (StringUtils.isBlank(input)) {
            return null;
        }
        PsiFile tempFile = PsiFileFactory.getInstance(project)
                .createFileFromText("Temp121.java", JavaFileType.INSTANCE, input);
        if (tempFile instanceof PsiJavaFile javaFile) {
            // 1. 获取类名
            return javaFile.getClasses().length > 0
                    && StringUtils.isNotBlank(javaFile.getClasses()[0].getName())
                    ? new FileInfo(javaFile.getClasses()[0].getName(), getExtensionName()) : null;
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
                .createFileFromText("Temp.java", JavaFileType.INSTANCE, input);
        String fileName = Objects.isNull(fileInfo) ? null : fileInfo.getFileName() + ".java";
        PsiFile file;

        if (tempFile instanceof PsiJavaFile javaFile) {
            // 1. 获取类名
            if (javaFile.getClasses().length > 0 && StringUtils.isNotEmpty(fileName)) {
                String newClass = fileName.replaceAll(".java", "");
                String oldClass = javaFile.getClasses()[0].getName();
                if (!newClass.equals(oldClass)) {
                    javaFile.getClasses()[0].setName(newClass);
                }
            } else {
                logger.info("找不到类名");
                fileName = "Java.java";
            }
            // 2. 替换/添加 package
            PsiPackageStatement oldPkgStmt = javaFile.getPackageStatement();
            PsiPackage aPackage = JavaDirectoryService.getInstance().getPackage(directory);
            if (Objects.nonNull(aPackage) && StringUtils.isNotBlank(aPackage.getQualifiedName())) {
                String pkg = aPackage.getQualifiedName();
                PsiPackageStatement newStmt = JavaPsiFacade.getElementFactory(project)
                        .createPackageStatement(pkg);
                if (oldPkgStmt != null) {
                    oldPkgStmt.replace(newStmt);
                }
            }
            javaFile.setName(fileName);
            file = javaFile;
        } else {
            // fallback: 无法解析成 Java 文件
            fileName = fileName == null ? "Java.java" : fileName + ".java";
            file = PsiFileFactory.getInstance(project)
                    .createFileFromText(fileName, JavaFileType.INSTANCE, input);
        }
        addFile(project, directory, file);
        return true;
    }

    @Override
    public void rename(@NotNull PsiFile psiFile, String renameFileName) {
        if (psiFile instanceof PsiJavaFile javaFile) {
            if (javaFile.getClasses().length > 0) {
                javaFile.getClasses()[0].setName(renameFileName.replaceAll(".java", ""));
            }
        }
        IExtensionHandler.super.rename(psiFile, renameFileName);
    }
}
