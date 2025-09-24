package com.zj.parsenewfile.handler;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public class JavaHandler implements ILanguageHandler {
    @Override
    public boolean support(String input) {
        String trim = input.trim();
        return trim.startsWith("package ") || trim.contains("class ");
    }

    @Override
    public boolean handle(Project project, String input, PsiDirectory directory) {
        String fileName;
        PsiFile file;
        PsiPackage aPackage = JavaDirectoryService.getInstance().getPackage(directory);
        if (aPackage == null) {
            return false;
        }
        String pkg = aPackage.getQualifiedName();

        // 先用 PsiFileFactory 解析输入文本
        PsiFile tempFile = PsiFileFactory.getInstance(project)
                .createFileFromText("Temp.java", JavaFileType.INSTANCE, input);

        if (tempFile instanceof PsiJavaFile javaFile) {
            // 1. 获取类名
            String className = "Unnamed";
            if (javaFile.getClasses().length > 0) {
                className = javaFile.getClasses()[0].getName();
            }
            fileName = className + ".java";

            // 2. 替换/添加 package
            PsiPackageStatement oldPkgStmt = javaFile.getPackageStatement();
            PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

            if (!pkg.isEmpty()) {
                PsiPackageStatement newStmt = factory.createPackageStatement(pkg);
                if (oldPkgStmt != null) {
                    oldPkgStmt.replace(newStmt);
                } else {
                    javaFile.addBefore(newStmt, javaFile.getFirstChild());
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
