package com.zj.parsenewfile.handler.extension;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.zj.parsenewfile.utils.log.Logger;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public class JavaHandler implements IExtensionHandler {

    private static final Logger logger = Logger.getInstance(JavaHandler.class);

    @Override
    public @NotNull String getName() {
        return "java";
    }

    @Override
    public boolean notSupport(String input) {
        if (StringUtils.isBlank(input)) {
            return true;
        }
        String trim = input.trim();
        return !trim.startsWith("package ") && !trim.contains("class ");
    }

    @Override
    public boolean handle(@NotNull Project project, @Nullable String input, @NotNull PsiDirectory directory) {
        if (StringUtils.isBlank(input)) {
            return false;
        }
        // 先用 PsiFileFactory 解析输入文本
        PsiFile tempFile = PsiFileFactory.getInstance(project)
                .createFileFromText("Temp.java", JavaFileType.INSTANCE, input);
        String fileName;
        PsiFile file;

        if (tempFile instanceof PsiJavaFile javaFile) {
            // 1. 获取类名
            String className;
            if (javaFile.getClasses().length > 0) {
                className = javaFile.getClasses()[0].getName();
            } else {
                logger.info("找不到类名");
                return false;
            }
            fileName = className + ".java";

            // 2. 替换/添加 package
            PsiPackageStatement oldPkgStmt = javaFile.getPackageStatement();
            PsiPackage aPackage = JavaDirectoryService.getInstance().getPackage(directory);
            if (aPackage == null) {
                logger.info("找不到Package");
                return false;
            }
            String pkg = aPackage.getQualifiedName();
            if (!pkg.isEmpty()) {
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
            fileName = "Unnamed.java";
            file = PsiFileFactory.getInstance(project)
                    .createFileFromText(fileName, JavaFileType.INSTANCE, input);
        }
        addFile(project, directory, file);
        return true;
    }
}
