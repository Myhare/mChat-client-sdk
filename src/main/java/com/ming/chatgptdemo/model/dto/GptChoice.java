package com.ming.chatgptdemo.model.dto;

import lombok.Data;

/**
 * GPT-3 返回choice对象
 */
@Data
public class GptChoice {

    private String text;

    private Integer index;
}
