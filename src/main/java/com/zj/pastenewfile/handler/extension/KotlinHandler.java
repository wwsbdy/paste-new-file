package com.zj.pastenewfile.handler.extension;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.refactoring.rename.RenameProcessor;
import com.intellij.testFramework.LightVirtualFile;
import com.zj.pastenewfile.enums.HandlerEnum;
import com.zj.pastenewfile.utils.log.Logger;
import com.zj.pastenewfile.vo.FileInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.idea.KotlinFileType;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.psi.KtClass;
import org.jetbrains.kotlin.psi.KtDeclaration;
import org.jetbrains.kotlin.psi.KtFile;

import java.util.Objects;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public class KotlinHandler implements IExtensionHandler {

    private static final Logger logger = Logger.getInstance(KotlinHandler.class);

    @Override
    public int order() {
        return HandlerEnum.KOTLIN.getSort();
    }

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
        PsiFile tempFile = PsiManager.getInstance(project).findFile(
                new LightVirtualFile("Temp" + "." + getExtensionName(), KotlinFileType.INSTANCE, input
                ));
        String fileName = Objects.isNull(fileInfo) ? null : fileInfo.getFileName() + "." + getExtensionName();
        PsiFile file;

        if (tempFile instanceof KtFile) {
            KtFile ktFile = (KtFile) tempFile;
            String finalFileName = fileName;
            WriteCommandAction.runWriteCommandAction(project, () -> {
                // 2. 替换/添加 package
                PsiPackage aPackage = JavaDirectoryService.getInstance().getPackage(directory);
                if (Objects.nonNull(aPackage) && StringUtils.isNotBlank(aPackage.getQualifiedName())) {
                    String pkg = aPackage.getQualifiedName();
                    ktFile.setPackageFqName(new FqName(pkg));
                }
                ktFile.setName(finalFileName);
            });
//            CommandProcessor.getInstance().executeCommand(project, () -> {
//                // 2. 替换/添加 package
//                PsiPackage aPackage = JavaDirectoryService.getInstance().getPackage(directory);
//                if (Objects.nonNull(aPackage) && StringUtils.isNotBlank(aPackage.getQualifiedName())) {
//                    String pkg = aPackage.getQualifiedName();
//                    ktFile.setPackageFqName(new FqName(pkg));
//                }
//                ktFile.setName(finalFileName);
//            }, RefactoringBundle.message("copy.handler.copy.files.directories"), null);
            file = ktFile;
        } else {
            // fallback: 无法解析成 Java 文件
            fileName = fileName == null ? "Kotlin" + "." + getExtensionName() : fileName + "." + getExtensionName();
            file = PsiFileFactory.getInstance(project)
                    .createFileFromText(fileName, KotlinFileType.INSTANCE, input);
        }
        rename(directory, file, fileName);
        return true;
    }

    @Override
    public void rename(@NotNull PsiDirectory directory, @NotNull PsiFile psiFile, @NotNull String renameFileName) {
        Project project = psiFile.getProject();
        if (psiFile instanceof KtFile) {
            KtFile ktFile = (KtFile) psiFile;
            KtDeclaration[] declarations = ktFile.getDeclarations().toArray(new KtDeclaration[0]);
            if (declarations.length > 0 && declarations[0] instanceof KtClass) {
                String newClass = renameFileName.replaceAll("." + getExtensionName(), "");
                String oldClass = declarations[0].getName();
                if (!newClass.equals(oldClass)) {
                    KtClass ktClass = (KtClass) declarations[0];
                    IExtensionHandler.super.rename(directory, psiFile, renameFileName);
                    CommandProcessor.getInstance().executeCommand(project, () -> {
//                        RenameUtil.findUsages(ktClass, newClass, GlobalSearchScope.fileScope(ktFile), true, true, Collections.singletonMap(ktClass, newClass));
                        RenameProcessor renameProcessor = new RenameProcessor(project, ktClass, newClass, new LocalSearchScope(ktClass), false, false);

                        renameProcessor.run();
                    }, RefactoringBundle.message("copy.handler.copy.files.directories"), null);
                    WriteCommandAction.runWriteCommandAction(project, () -> {
                        ktClass.setName(newClass);
                    });
                }
            }
        }
        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (!checkFileExist(directory, psiFile.getName())) {
                directory.add(psiFile);
            }
        });
    }
}
