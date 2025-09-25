package com.zj.parsenewfile.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.zj.parsenewfile.handler.ILanguageHandler;
import com.zj.parsenewfile.utils.LanguageUtils;
import com.zj.parsenewfile.utils.log.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public class ParseNewFileAction extends AnAction {

    private static final Logger logger = Logger.getInstance(ParseNewFileAction.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        PsiDirectory directory = getTargetDirectory(e);

        if (project == null || directory == null) {
            logger.info("directory == null");
            return;
        }

        // 弹出输入对话框
        TextInputDialog dialog = new TextInputDialog(project);
        if (!dialog.showAndGet()) {
            return;
        }

        String input = dialog.getInputText();
        ILanguageHandler handler = LanguageUtils.getHandler(dialog.getLanguage());
        if (handler == null) {
            logger.info("handler == null");
            return;
        }
        if (handler.handle(project, input, directory, dialog.getFileInfo())) {
            logger.info("处理成功");
        }
    }

    /**
     * 改进的获取目标目录方法，支持多种上下文
     *
     * @param e AnActionEvent
     * @return 目标PsiDirectory，如果无法获取则返回null
     */

    private PsiDirectory getTargetDirectory(@NotNull AnActionEvent e) {
        // 1. 首先尝试直接获取右键选中的PsiDirectory
        PsiDirectory selectedDirectory = e.getData(LangDataKeys.PSI_ELEMENT) instanceof PsiDirectory
                ? (PsiDirectory) e.getData(LangDataKeys.PSI_ELEMENT)
                : null;
        if (selectedDirectory != null) {
            logger.info("获取目录");
            return selectedDirectory;
        }
        Project project = e.getProject();
        if (project == null) {
            logger.info("无法获取项目");
            return null;
        }

        PsiManager psiManager = PsiManager.getInstance(project);

        // 2. 如果直接获取目录失败，尝试获取右键选中的PsiFile，然后取其所在目录
        PsiFile selectedFile = e.getData(LangDataKeys.PSI_FILE);
        if (selectedFile != null) {
            logger.info("获取文件所在目录");
            return selectedFile.getContainingDirectory();
        }

        // 3. 如果上述方式都失败（例如在编辑器内部右键），尝试通过VirtualFile和当前编辑器获取
        // 获取当前编辑器的VirtualFile（如果存在）
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (virtualFile != null) {
            if (virtualFile.isDirectory()) {
                // 如果是目录，直接查找对应的PsiDirectory
                logger.info("virtualFile.isDirectory()");
                return psiManager.findDirectory(virtualFile);
            } else {
                // 如果是文件，获取其父目录（即所在目录）对应的PsiDirectory
                VirtualFile parentDir = virtualFile.getParent();
                if (parentDir != null) {
                    logger.info("parentDir != null");
                    return psiManager.findDirectory(parentDir);
                }
            }
        }
        logger.info("无法获取目录");
        return null;
    }
}