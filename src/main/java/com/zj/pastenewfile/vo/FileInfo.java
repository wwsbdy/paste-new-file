package com.zj.pastenewfile.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * @author : jie.zhou
 * @date : 2025/9/25
 */
@Data
@AllArgsConstructor
public class FileInfo {
    @NotNull
    private final String fileName;

    @NotNull
    private final String extensionName;


    public FileInfo(@NotNull String extensionName) {
        this.fileName = "";
        this.extensionName = extensionName;
    }
}
