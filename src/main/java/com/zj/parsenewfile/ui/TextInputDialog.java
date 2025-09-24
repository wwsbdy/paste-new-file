package com.zj.parsenewfile.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.zj.parsenewfile.utils.language.PluginBundle;
import com.zj.parsenewfile.utils.log.Logger;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public class TextInputDialog extends DialogWrapper {

    private static final Logger logger = Logger.getInstance(TextInputDialog.class);

    private JTextArea textArea;

    public TextInputDialog() {
        super(true);
        setTitle(PluginBundle.get("dialog.title.text-input"));
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        textArea = new JTextArea(15, 50);
        try {
            // 1. 获取系统剪贴板
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            // 2. 获取剪贴板内容
            Transferable contents = clipboard.getContents(null);
            // 检查内容是否为文本类型
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                // 从剪贴板获取文本
                String clipboardText = (String) contents.getTransferData(DataFlavor.stringFlavor);
                textArea.setText(clipboardText);
                // 全选文本区域内容
                textArea.selectAll();
            } else {
                logger.info("剪贴板中没有文本内容或内容类型不支持");
            }
        } catch (Exception ex) {
            logger.info("获取剪贴板内容失败：" + ex.getMessage());
        }
        return new JBScrollPane(textArea);
    }

    public String getInputText() {
        return textArea.getText();
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return textArea;
    }
}