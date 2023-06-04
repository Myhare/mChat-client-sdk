package com.ming.chatgptdemo.model.request;

import com.ming.chatgptdemo.model.dto.MessageDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * https://api.openai.com/v1/chat/completions 接口请求体
 */
@Data
@Builder
public class GptChatRequestBody {

    /**
     * 模型
     */
    private String model;

    /**
     * 消息列表
     */
    private List<MessageDTO> messages;

    /**
     * 生成结果的最大token数量
     */
    private Integer max_tokens;

    /**
     * 随机因子  0-2之间
     * 较高的值（如0.8）将使输出更随机，而较低的值（如0.2）将使其更集中和确定性。
     * 如果希望结果更有创意可以尝试 0.9，或者希望有固定结果可以尝试 0.0 .
     */
    private Double temperature;

    /**
     * 停止字符，里面最大有4个字符串列表，一旦生成的token包括里面的内容，就停止生成并返回结果
     */
    private List<String> stop;

    /**
     * 是否以流的形式返回
     */
    private boolean stream;

}
