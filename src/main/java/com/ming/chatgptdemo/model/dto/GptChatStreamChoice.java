package com.ming.chatgptdemo.model.dto;

import lombok.Data;

@Data
public class GptChatStreamChoice {

    /**
     * 回答的结果
     */
    private DeltaDTO delta;

    private Integer index;

    private String finish_reason;

}
