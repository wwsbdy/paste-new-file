package com.zj.pastenewfile.handler.extension;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.zj.pastenewfile.enums.HandlerEnum;
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
    public int order() {
        return HandlerEnum.JAVA.getSort();
    }

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
                .createFileFromText("Temp121" + "." + getExtensionName(), JavaFileType.INSTANCE, input);
        if (tempFile instanceof PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) tempFile;
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
                .createFileFromText("Temp" + "." + getExtensionName(), JavaFileType.INSTANCE, input);
        String fileName = Objects.isNull(fileInfo) ? null : fileInfo.getFileName() + "." + getExtensionName();
        PsiFile file;

        if (tempFile instanceof PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) tempFile;
            // 1. 获取类名
            if (javaFile.getClasses().length > 0 && StringUtils.isNotEmpty(fileName)) {
                String newClass = fileName.replaceAll("." + getExtensionName(), "");
                String oldClass = javaFile.getClasses()[0].getName();
                if (!newClass.equals(oldClass)) {
                    PsiClass psiClass = javaFile.getClasses()[0];
                    renameClass(newClass, psiClass, project);
                }
            } else {
                logger.info("找不到类名");
                fileName = "Java" + "." + getExtensionName();
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
            fileName = fileName == null ? "Java" + "." + getExtensionName() : fileName + "." + getExtensionName();
            file = PsiFileFactory.getInstance(project)
                    .createFileFromText(fileName, JavaFileType.INSTANCE, input);
        }
        addFile(project, directory, file);
        return true;
    }

    @Override
    public void rename(@NotNull PsiFile psiFile, String renameFileName) {
        Project project = psiFile.getProject();
        if (psiFile instanceof PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) psiFile;
            if (javaFile.getClasses().length > 0) {
                PsiClass psiClass = javaFile.getClasses()[0];
                String newName = renameFileName.replaceAll("." + getExtensionName(), "");
                renameClass(newName, psiClass, project);
            }
        }
        IExtensionHandler.super.rename(psiFile, renameFileName);
    }

    public void renameClass(String newName, PsiClass psiClass, Project project) {
        String oldName = psiClass.getName();
        if (oldName == null) {
            return;
        }

        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        GlobalSearchScope scope = psiClass.getResolveScope();

        WriteCommandAction.runWriteCommandAction(project, () -> {
            // 改类名
            psiClass.setName(newName);

            // 更新方法签名
            for (PsiMethod method : psiClass.getMethods()) {
                // 返回类型
                PsiType returnType = method.getReturnType();
                if (returnType != null && returnType.equalsToText(oldName)) {
                    method.getReturnTypeElement().replace(
                            factory.createTypeElement(factory.createTypeByFQClassName(newName, scope))
                    );
                }

                // 参数类型
                for (PsiParameter param : method.getParameterList().getParameters()) {
                    PsiType paramType = param.getType();
                    if (Objects.nonNull(param.getTypeElement()) && paramType.equalsToText(oldName)) {
                        param.getTypeElement().replace(
                                factory.createTypeElement(factory.createTypeByFQClassName(newName, scope))
                        );
                    }
                }
            }

            // 更新方法体内部的引用，包括泛型
            psiClass.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitTypeElement(PsiTypeElement type) {
                    super.visitTypeElement(type);

                    PsiType psiType = type.getType();
                    if (psiType instanceof PsiClassType) {
                        PsiClass resolved = ((PsiClassType) psiType).resolve();
                        if (resolved != null && oldName.equals(resolved.getName())) {
                            PsiClassType newClassType = factory.createTypeByFQClassName(newName, scope);
                            type.replace(factory.createTypeElement(newClassType));
                        }
                    }
                }

                @Override
                public void visitReferenceElement(PsiJavaCodeReferenceElement reference) {
                    super.visitReferenceElement(reference);

                    PsiElement resolved = reference.resolve();
                    if (resolved instanceof PsiClass && oldName.equals(((PsiClass) resolved).getName())) {
                        try {
                            reference.bindToElement(psiClass);
                        } catch (Exception ignored) {
                        }
                    }
                }

                @Override
                public void visitClassObjectAccessExpression(PsiClassObjectAccessExpression expression) {
                    super.visitClassObjectAccessExpression(expression);
                    PsiType type = expression.getOperand().getType();
                    if (type instanceof PsiClassType) {
                        PsiClass resolved = ((PsiClassType) type).resolve();
                        if (resolved != null && oldName.equals(resolved.getName())) {
                            PsiClassType newClassType = factory.createTypeByFQClassName(newName, scope);
                            expression.getOperand().replace(factory.createTypeElement(newClassType));
                        }
                    }
                }
            });
        });
    }
}
