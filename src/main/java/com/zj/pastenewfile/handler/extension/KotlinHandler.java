package com.zj.pastenewfile.handler.extension;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.refactoring.rename.RenameProcessor;
import com.zj.pastenewfile.enums.HandlerEnum;
import com.zj.pastenewfile.utils.log.Logger;
import com.zj.pastenewfile.vo.FileInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.idea.KotlinFileType;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.psi.*;

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
        PsiFile tempFile = PsiFileFactory.getInstance(project)
                .createFileFromText("Temp" + "." + getExtensionName(), KotlinFileType.INSTANCE, input);
        String fileName = Objects.isNull(fileInfo) ? null : fileInfo.getFileName() + "." + getExtensionName();
        PsiFile file;

        if (tempFile instanceof KtFile) {
            KtFile ktFile = (KtFile) tempFile;
            KtDeclaration[] declarations = ktFile.getDeclarations().toArray(new KtDeclaration[0]);
            if (declarations.length > 0 && declarations[0] instanceof KtClass
                    && StringUtils.isNotEmpty(fileName)) {
                String newClass = fileName.replaceAll("." + getExtensionName(), "");
                String oldClass = declarations[0].getName();
                if (!newClass.equals(oldClass)) {
                    KtClass ktClass = (KtClass) declarations[0];
                    renameClass(newClass, ktClass, project);
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
            KtDeclaration[] declarations = ktFile.getDeclarations().toArray(new KtDeclaration[0]);
            if (declarations.length == 0) {
                return;
            }

            KtDeclaration decl = declarations[0];
            if (!(decl instanceof KtClass)) {
                return;
            }

            KtClass ktClass = (KtClass) decl;
            String oldName = ktClass.getName();
            if (oldName == null || oldName.equals(renameFileName)) {
                return;
            }
            renameClass(renameFileName, ktClass, project);
        }
        IExtensionHandler.super.rename(psiFile, renameFileName);
    }


    public void renameClass(String newName, KtClass ktClass, Project project) {
        String oldName = ktClass.getName();
        if (oldName == null) {
            return;
        }

        WriteCommandAction.runWriteCommandAction(project, () -> {
            // 1. 重命名类声明
            new RenameProcessor(project, ktClass, newName, true, true).run();
            // 2. 遍历文件中所有类型引用，更新旧类名为新类名
            ktClass.accept(new KtTreeVisitorVoid() {
                @Override
                public void visitTypeReference(@NotNull KtTypeReference typeReference) {
                    super.visitTypeReference(typeReference);
                    KtTypeElement typeElement = typeReference.getTypeElement();
                    if (typeElement != null) {
                        replaceOldNameType(project, typeElement, oldName, newName);
                    }
                }
            });
        });
    }

    private static void replaceOldNameType(@NotNull Project project,
                                           @NotNull KtTypeElement typeElement,
                                           @NotNull String oldName,
                                           @NotNull String newName) {
        if (typeElement instanceof KtUserType) {
            KtUserType userType = (KtUserType) typeElement;

            // 替换最外层名称
            if (oldName.equals(userType.getReferencedName())) {
                KtPsiFactory psiFactory = new KtPsiFactory(project);
                KtTypeReference newTypeRef = psiFactory.createType(newName);
                if (newTypeRef.getTypeElement() != null) {
                    newTypeRef.getTypeElement().replace(typeElement);
                }
                return;
            }

            // 递归处理泛型参数
            KtTypeArgumentList argList = userType.getTypeArgumentList();
            if (argList != null) {
                for (KtTypeProjection proj : argList.getArguments()) {
                    KtTypeReference projRef = proj.getTypeReference();
                    if (projRef != null) {
                        KtTypeElement inner = projRef.getTypeElement();
                        if (inner != null) {
                            replaceOldNameType(project, inner, oldName, newName);
                        }
                    }
                }
            }

        } else if (typeElement instanceof KtNullableType) {
            KtNullableType nullableType = (KtNullableType) typeElement;
            KtTypeElement inner = nullableType.getInnerType();
            if (inner != null) {
                replaceOldNameType(project, inner, oldName, newName);
            }
        } else if (typeElement instanceof KtFunctionType) {
            KtFunctionType funcType = (KtFunctionType) typeElement;

            // 返回值
            KtTypeReference returnType = funcType.getReturnTypeReference();
            if (returnType != null && returnType.getTypeElement() != null) {
                replaceOldNameType(project, returnType.getTypeElement(), oldName, newName);
            }

            // 参数类型列表通过 value parameters 遍历
            for (KtParameter param : funcType.getParameters()) {
                KtTypeReference paramType = param.getTypeReference();
                if (paramType != null && paramType.getTypeElement() != null) {
                    replaceOldNameType(project, paramType.getTypeElement(), oldName, newName);
                }
            }
        }
    }
}
