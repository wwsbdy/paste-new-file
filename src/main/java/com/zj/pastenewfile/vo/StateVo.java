package com.zj.pastenewfile.vo;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author : jie.zhou
 * @date : 2025/9/25
 */
@Data
public class StateVo {

    /**
     * 是否自动解析
     */
    private Boolean autoParse;

    /**
     * 自定义扩展名
     */
    private String extensionName;

    public boolean isEmpty() {
        return Objects.isNull(autoParse);
    }

    @NotNull
    public String getExtensionName() {
        String extensionName = null;
        if (Objects.nonNull(autoParse) && !autoParse) {
            extensionName = this.extensionName;
        }
        if (Objects.isNull(extensionName)) {
            extensionName = "";
        }
        return extensionName;
    }

    public void setExtensionName(String extensionName) {
        if (Objects.nonNull(autoParse) && !autoParse) {
            this.extensionName = extensionName;
        }
    }
}
