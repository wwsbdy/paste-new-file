package com.zj.parsenewfile.vo;

import lombok.Data;

import java.util.Objects;

/**
 * @author : jie.zhou
 * @date : 2025/9/25
 */
@Data
public class StateVo {

    private String fileType;

    public boolean isEmpty() {
        return Objects.isNull(fileType);
    }

}
