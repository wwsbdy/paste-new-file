package com.zj.pastenewfile.handler.extension;

import com.zj.pastenewfile.enums.HandlerEnum;
import org.jetbrains.annotations.NotNull;

/**
 * @author : jie.zhou
 * @date : 2025/9/28
 */
public class YmlHandler extends YamlHandler {

    @Override
    public int order() {
        return HandlerEnum.YML.getSort();
    }

    @Override
    public @NotNull String getExtensionName() {
        return "yml";
    }
}
