package com.ming.chatgptdemo.model.dto;

import lombok.Data;
import org.springframework.stereotype.Component;


/**
 * GPT返回choice对象
 */
@Data
@Component
public class GptChatChoice {

    /**
     * 下标
     */
    private Integer index;

    /**
     * 回答消息
     */
    private MessageDTO message;

    /**
     * 完成原因
     */
    private String finish_reason;
}
