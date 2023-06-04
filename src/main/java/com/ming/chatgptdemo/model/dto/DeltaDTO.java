package com.ming.chatgptdemo.model.dto;

import lombok.Data;

@Data
public class DeltaDTO {

    /**
     * 流式结果返回的数据
     */
    private String content;

}
