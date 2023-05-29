package com.ming.chatgptdemo.model.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * chatGPT请求体（旧api）
 */
@Data
@Builder
public class GptRequestBody {

    /**
     * GPT使用的模型
     */
    private String model;

    /**
     * 你的提问
     */
    private String prompt;

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
}
