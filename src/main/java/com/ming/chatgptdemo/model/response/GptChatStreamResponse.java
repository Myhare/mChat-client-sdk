package com.ming.chatgptdemo.model.response;

import com.ming.chatgptdemo.model.dto.GptChatStreamChoice;
import lombok.Data;

import java.util.List;

/**
 * GPT流式回答对象
 */
@Data
public class GptChatStreamResponse {

    /**
     * 回答的id
     */
    private String id;

    private String object;

    private String created;

    private String model;

    private List<GptChatStreamChoice> choices;
}
