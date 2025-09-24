package com.zj.parsenewfile.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.zj.parsenewfile.handler.ILanguageHandler;
import com.zj.parsenewfile.utils.log.Logger;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public class ParseNewFileAction extends AnAction {

    private static final Logger logger = Logger.getInstance(ParseNewFileAction.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        PsiDirectory directory = e.getData(LangDataKeys.PSI_ELEMENT) instanceof PsiDirectory
                ? (PsiDirectory) e.getData(LangDataKeys.PSI_ELEMENT)
                : null;

        if (project == null || directory == null) {
            Messages.showErrorDialog("请选择一个目录来创建文件。", "错误");
            return;
        }

        // 弹出输入对话框
        TextInputDialog dialog = new TextInputDialog();
        if (!dialog.showAndGet()) {
            return;
        }

        String input = dialog.getInputText();
        if (input.isBlank()) {
            Messages.showWarningDialog("输入不能为空！", "警告");
            return;
        }
//        // 获取 FileTypeManager 实例
//        FileTypeManager fileTypeManager = FileTypeManager.getInstance();
//        // 获取所有已注册的文件类型
//        FileType[] allFileTypes = fileTypeManager.getRegisteredFileTypes();
//        for (FileType fileType : allFileTypes) {
//            if (fileType instanceof LanguageFileType) {
//                logger.info("fileType: " + fileType.getName());
//            }
//        }
        ILanguageHandler.HANDLERS.stream()
                .filter(handler -> handler.support(input))
                .findFirst()
                .map(handler -> handler.handle(project, input, directory));

    }
}