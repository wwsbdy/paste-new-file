package com.zj.pastenewfile.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.TextFieldWithAutoCompletionListProvider;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.textCompletion.TextFieldWithCompletion;
import com.zj.pastenewfile.handler.ILanguageHandler;
import com.zj.pastenewfile.utils.LanguageUtils;
import com.zj.pastenewfile.utils.language.PluginBundle;
import com.zj.pastenewfile.utils.log.Logger;
import com.zj.pastenewfile.vo.FileInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public class TextInputDialog extends DialogWrapper {

    private static final Logger logger = Logger.getInstance(TextInputDialog.class);

    private final Project project;

    private JTextArea textArea;

    private TextFieldWithCompletion extensionNameTextField;

    private EditorTextField fileNameTextField;

    /**
     * 剪贴板内容
     */
    private String clipboardText;

    private FileInfo fileInfo;

    public TextInputDialog(Project project) {
        super(true);
        this.project = project;
        setTitle(PluginBundle.get("dialog.title.text-input"));

        try {
            // 1. 获取系统剪贴板
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            // 2. 获取剪贴板内容
            Transferable contents = clipboard.getContents(null);
            // 检查内容是否为文本类型
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                // 从剪贴板获取文本
                clipboardText = (String) contents.getTransferData(DataFlavor.stringFlavor);
                fileInfo = LanguageUtils.findLanguage(project, clipboardText);
            } else {
                logger.info("剪贴板中没有文本内容或内容类型不支持");
            }
        } catch (Exception ex) {
            logger.info("获取剪贴板内容失败：" + ex.getMessage());
        }
        init();
    }

    @Override
    protected @Nullable JComponent createNorthPanel() {
        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        fileNameTextField = new EditorTextField();
        fileNameTextField.setText(fileInfo.getFileName());
        fileNameTextField.setEnabled(true);
        fileNameTextField.setPreferredSize(new Dimension(200, 30));
        jPanel.add(fileNameTextField);
        jPanel.add(new JLabel("."));
        extensionNameTextField = getExtensionNameTextField();
        extensionNameTextField.setPreferredSize(new Dimension(80, 30));
        jPanel.add(extensionNameTextField);
        return jPanel;
    }

    private @NotNull TextFieldWithCompletion getExtensionNameTextField() {
        List<String> languageList = LanguageUtils.getAllHandlers()
                .stream().map(ILanguageHandler::getExtensionName)
                .collect(Collectors.toList());
        TextFieldWithAutoCompletionListProvider<String> provider = new TextFieldWithAutoCompletionListProvider<>(languageList) {
            @Override
            protected @NotNull String getLookupString(@NotNull String s) {
                return s;
            }
        };

        // 2. 创建带补全的文本字段
        return new TextFieldWithCompletion(
                project,
                provider,
                fileInfo.getExtensionName(),
                true,
                true,
                false
        );
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        this.textArea = new JTextArea(15, 50);
        this.textArea.setPreferredSize(new Dimension(500, 700));
        if (StringUtils.isNotEmpty(this.clipboardText)) {
            this.textArea.setText(this.clipboardText);
            // 全选文本区域内容
            this.textArea.selectAll();
        }
        return new JBScrollPane(this.textArea);
    }

    public String getInputText() {
        return this.textArea.getText();
    }

    public String getLanguage() {
        if (Objects.isNull(this.extensionNameTextField)) {
            return null;
        }
        return this.extensionNameTextField.getText();
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return this.textArea;
    }

    public FileInfo getFileInfo() {
        return new FileInfo(fileNameTextField.getText(), extensionNameTextField.getText());
    }
}