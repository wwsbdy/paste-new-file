package com.zj.pastenewfile.handler.extension;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.zj.pastenewfile.utils.log.Logger;
import com.zj.pastenewfile.vo.FileInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLFileType;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author : jie.zhou
 * @date : 2025/9/28
 */
public class YamlHandler implements IExtensionHandler {

    private static final Logger logger = Logger.getInstance(YamlHandler.class);

    @Override
    public @NotNull String getExtensionName() {
        return "yaml";
    }

    @Override
    public @Nullable FileInfo support(@NotNull Project project, @Nullable String input) {
        if (StringUtils.isBlank(input)) {
            return null;
        }
        try {
            // 方法1: 使用SnakeYAML直接验证YAML语法
            Yaml yaml = new Yaml();
            Object load = yaml.load(input);
            if (load instanceof Map || load instanceof List) {
                return new FileInfo(getExtensionName());
            }
        } catch (YAMLException e) {
            // YAML解析错误，说明格式无效
            logger.info("Invalid YAML format: " + e.getMessage());
            return null;
        } catch (Exception e) {
            // 其他异常处理
            logger.info("Error processing YAML: " + e.getMessage());
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
                .createFileFromText(fileName + "." + getExtensionName(), YAMLFileType.YML, input);
        addFile(project, directory, file);
        return true;
    }
}
