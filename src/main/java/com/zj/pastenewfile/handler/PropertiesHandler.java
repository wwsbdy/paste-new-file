package com.zj.pastenewfile.handler;

import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.zj.pastenewfile.vo.FileInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * @author : jie.zhou
 * @date : 2025/9/28
 */
public class PropertiesHandler implements ILanguageHandler {
    @Override
    public @NotNull String getExtensionName() {
        return "properties";
    }

    @Override
    public FileInfo support(@NotNull Project project, @Nullable String input) {
        if (StringUtils.isBlank(input)) {
            return null;
        }

        Properties properties = new Properties();

        // 将字符串转换为输入流进行Properties格式验证
        try (InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))) {
            // 尝试加载Properties，如果格式错误会抛出IOException
            properties.load(inputStream);
            if (!properties.isEmpty()) {
                return new FileInfo(getExtensionName());
            }

        } catch (IOException e) {
            // 捕获IOException说明Properties格式无效
            System.err.println("Invalid Properties format: " + e.getMessage());
            return null;
        } catch (Exception e) {
            // 处理其他可能的异常
            System.err.println("Error processing Properties: " + e.getMessage());
            return null;
        }

        return null;
    }

    @Override
    public boolean handle(@NotNull Project project, @Nullable String input, @NotNull PsiDirectory directory, @Nullable FileInfo fileInfo) {
        if (Objects.isNull(input)) {
            input = "";
        }
        String fileName = Objects.isNull(fileInfo) ? "unknown" : fileInfo.getFileName();
        PsiFile file = PsiFileFactory.getInstance(project)
                .createFileFromText(fileName + "." + getExtensionName(), PlainTextFileType.INSTANCE, input);
        addFile(project, directory, file);
        return true;
    }
}
