package com.zj.pastenewfile.utils;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.zj.pastenewfile.handler.ILanguageHandler;
import com.zj.pastenewfile.handler.TxtHandler;
import com.zj.pastenewfile.handler.extension.IExtensionHandler;
import com.zj.pastenewfile.vo.FileInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : jie.zhou
 * @date : 2025/9/24
 */
public class LanguageUtils {

    private static final List<ILanguageHandler> HANDLERS;
    private static final ExtensionPointName<ILanguageHandler> EP_NAME =
            ExtensionPointName.create("com.zj.paste-new-file.languageFileHandler");

    static {
        Function<ILanguageHandler, Integer> order = (handler) -> {
            // 第一优先级：IExtensionHandler 排在最前（返回0），其他类型排在后面（返回1）
            return (handler instanceof IExtensionHandler) ? 0 : 1;
        };
        HANDLERS = EP_NAME.getExtensionList()
                .stream()
                .sorted(Comparator.comparing(order).thenComparing(ILanguageHandler::order))
                .collect(Collectors.toList());
    }

    public static List<ILanguageHandler> getAllHandlers() {
        return HANDLERS;
    }

    @Nullable
    public static ILanguageHandler getHandler(String language) {
        if (Objects.isNull(language)) {
            return null;
        }
        for (ILanguageHandler handler : HANDLERS) {
            if (language.equals(handler.getExtensionName())) {
                return handler;
            }
        }
        return null;
    }

    public static FileInfo findLanguage(Project project, String content) {
        if (StringUtils.isBlank(content)) {
            return TxtHandler.FILE_INFO;
        }
        for (ILanguageHandler handler : HANDLERS) {
            FileInfo fileInfo = handler.support(project, content);
            if (Objects.isNull(fileInfo)) {
                continue;
            }
            return fileInfo;
        }
        return TxtHandler.FILE_INFO;
    }
}
