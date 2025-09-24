package com.zj.parsenewfile.ui;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author : jie.zhou
 * @date : 2025/9/23
 */
public class TextInputDialog extends DialogWrapper {
    private JTextArea textArea;

    public TextInputDialog() {
        super(true);
        setTitle("输入代码内容");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        textArea = new JTextArea(15, 50);
        return new JScrollPane(textArea);
    }

    public String getInputText() {
        return textArea.getText();
    }
}