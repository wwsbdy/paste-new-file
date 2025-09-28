package com.zj.pastenewfile.utils;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.zj.pastenewfile.handler.*;
import com.zj.pastenewfile.vo.FileInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author : jie.zhou
 * @date : 2025/9/24
 */
public class LanguageUtils {

    private static final List<ILanguageHandler> HANDLERS;
    private static final ExtensionPointName<ILanguageHandler> EP_NAME =
            ExtensionPointName.create("com.zj.paste-new-file.languageFileHandler");
    private static final JsonHandler JSON_HANDLER = new JsonHandler();
    private static final HtmlHandler HTML_HANDLER = new HtmlHandler();
    private static final XmlHandler XML_HANDLER = new XmlHandler();
    private static final TxtHandler TXT_HANDLER = new TxtHandler();
    private static final PropertiesHandler PROPERTIES_HANDLER = new PropertiesHandler();

    static {
        HANDLERS = new ArrayList<>();
        HANDLERS.addAll(EP_NAME.getExtensionList());

        HANDLERS.add(JSON_HANDLER);
        HANDLERS.add(HTML_HANDLER);
        HANDLERS.add(XML_HANDLER);
        HANDLERS.add(PROPERTIES_HANDLER);
        HANDLERS.add(TXT_HANDLER);
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
