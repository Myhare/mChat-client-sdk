package com.ming.chatgptdemo.model.response;

import com.ming.chatgptdemo.model.dto.GptChatChoice;
import lombok.Data;

import java.util.List;

/**
 * GPT返回对象
 */
@Data
public class GptChatResponse {

    /**
     * 回答的id
     */
    private String id;

    private String object;

    private String created;

    private String model;

    private List<GptChatChoice> choices;
}
