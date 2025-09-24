package com.zj.parsenewfile.utils;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.zj.parsenewfile.handler.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : jie.zhou
 * @date : 2025/9/24
 */
public class HandlerUtils {

    private static final List<ILanguageHandler> HANDLERS;
    private static final ExtensionPointName<ILanguageHandler> EP_NAME =
            ExtensionPointName.create("com.zj.parse-new-file.languageFileHandler");

    static {
        HANDLERS = new ArrayList<>();
        HANDLERS.addAll(EP_NAME.getExtensionList());

        HANDLERS.add(new JsonHandler());
        HANDLERS.add(new HtmlHandler());
        HANDLERS.add(new XmlHandler());
        HANDLERS.add(new TxtHandler());
    }

    public static List<ILanguageHandler> getAllHandlers() {
        return HANDLERS;
    }
}
