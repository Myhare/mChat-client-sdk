package com.ming.chatgptdemo.client;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.ming.chatgptdemo.ChatGptConfig;
import com.ming.chatgptdemo.constant.MessageRoleConstant;
import com.ming.chatgptdemo.model.dto.GptChatStreamChoice;
import com.ming.chatgptdemo.model.dto.MessageDTO;
import com.ming.chatgptdemo.model.dto.StreamReDTO;
import com.ming.chatgptdemo.model.request.GptChatRequestBody;
import com.ming.chatgptdemo.model.request.GptRequestBody;
import com.ming.chatgptdemo.model.response.GptChatResponse;
import com.ming.chatgptdemo.model.response.GptChatStreamResponse;
import com.ming.chatgptdemo.model.response.GptResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static com.ming.chatgptdemo.constant.GptModelConstant.GPT_TURBO;
import static com.ming.chatgptdemo.constant.GptModelConstant.TEXT_DAVINCI_003;
import static com.ming.chatgptdemo.constant.HttpConstant.*;

@Component
public class ChatClient {

    @Autowired
    private ChatGptConfig chatGptConfig;

    @Autowired
    private WebClient webClient;

    /**
     * 创建一个线程池
     */
    private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    /**
     * 历史消息列表
     */
    private List<MessageDTO> messageList = new LinkedList<>();

    /**
     * 文本生成api，
     * @param prompt 问的消息
     * @return       回答
     */
    public String sendOld(String prompt){
        // 请求体
        GptRequestBody requestBody = GptRequestBody.builder()
                .model(TEXT_DAVINCI_003)
                .prompt(prompt)
                .temperature(chatGptConfig.temperature)
                .max_tokens(2000)
                .build();

        HttpResponse response = HttpUtil.createPost(OLD_API_URL)
                .setHttpProxy(chatGptConfig.proxyUrl, chatGptConfig.proxyPort)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + chatGptConfig.apiKey)
                .body(JSONUtil.toJsonStr(requestBody))
                .execute();
        String responseBodyJson = response.body();
        GptResponse responseBody = JSONUtil.toBean(responseBodyJson, GptResponse.class);
        return responseBody.getChoices().get(0).getText();
    }

    /**
     * 对话api（支持上下文）
     * @param message 问的消息
     * @return        回答
     */
    public MessageDTO send(String message){
        try {
            if (StrUtil.isBlank(message)){
                throw new RuntimeException("消息为空");
            }
            sizeControl(this.messageList);
            this.messageList.add(new MessageDTO(MessageRoleConstant.USER, message));
            // 再次获取，判断是否是引用类型
            // 请求体
            GptChatRequestBody requestBody = GptChatRequestBody.builder()
                    .model(GPT_TURBO)
                    .messages(this.messageList)
                    .build();
            // 发送请求
            HttpResponse response = getChatGptRequest(requestBody, API_URL).execute();
            String responseBodyJson = response.body();

            // System.out.println(responseBodyJson);
            GptChatResponse responseData = JSONUtil.toBean(responseBodyJson, GptChatResponse.class);
            // 判断请求是否成功
            if (responseData.getId() == null){
                return new MessageDTO(MessageRoleConstant.ASSISTANT,"请求失败，可能是没有正确的apiKey");
            }
            // 将回答的信息存入上下文中
            MessageDTO reMessage = responseData.getChoices().get(0).getMessage();
            String content = reMessage.getContent();
            String role = reMessage.getRole();
            // 将GPT的回答存入消息列表中
            this.messageList.add(new MessageDTO(role, content));
            return reMessage;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new MessageDTO(MessageRoleConstant.ASSISTANT,"发送消息错误，如果错误持续出现，请修改代理或联系我");
        }
    }

    /**
     * 对话api 通过方法传参传递历史消息
     * @param message 问的消息
     * @return        回答
     */
    public List<MessageDTO> send(String message, List<MessageDTO> messageList){
        try {
            if (StrUtil.isBlank(message)){
                throw new RuntimeException("消息为空");
            }
            sizeControl(messageList);
            messageList.add(new MessageDTO(MessageRoleConstant.USER, message));
            // 再次获取，判断是否是引用类型
            // 请求体
            GptChatRequestBody requestBody = GptChatRequestBody.builder()
                    .model(GPT_TURBO)
                    .messages(messageList)
                    .build();
            // 发送请求
            HttpResponse response = getChatGptRequest(requestBody, API_URL).execute();
            String responseBodyJson = response.body();

            // System.out.println(responseBodyJson);
            GptChatResponse responseData = JSONUtil.toBean(responseBodyJson, GptChatResponse.class);
            // 判断请求是否成功
            if (responseData.getId() == null){
                messageList.add(new MessageDTO(MessageRoleConstant.ASSISTANT, "发生错误，请过段时间再尝试或者修改代理"));
                return messageList;
            }
            // 将回答的信息存入上下文中
            String content = responseData.getChoices().get(0).getMessage().getContent();
            String role = responseData.getChoices().get(0).getMessage().getRole();
            // 将GPT的回答存入消息列表中
            messageList.add(new MessageDTO(role, content));
            return messageList;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return messageList;
        }
    }

    /**
     * 流式发送消息，消息发送完成后执行函数式接口
     */
    public void streamSend(SseEmitter sseEmitter ,String message, Consumer<StreamReDTO> consumer) {

        StringBuilder reStringBuffer = new StringBuilder();

        sizeControl(this.messageList);
        this.messageList.add(new MessageDTO(MessageRoleConstant.USER, message));

        // 发送HTTP请求，设置stream参数为true
        GptChatRequestBody requestBody = GptChatRequestBody.builder()
                .model(GPT_TURBO)
                .messages(this.messageList)
                .stream(true)
                .build();

        cachedThreadPool.execute(() -> {

            webClient.post()
                    .uri("https://api.openai.com/v1/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER + chatGptConfig.apiKey)
                    .bodyValue(JSONUtil.toJsonStr(requestBody))
                    .retrieve() // 获取响应体
                    .bodyToFlux(String.class)// 处理多个响应流
                    // 正常处理的结果
                    .subscribe(responseJson -> {
                        if (DONE.equals(responseJson)){
                            // 说明消息传递完成
                            // 用户通过函数式接口自己选择对完整的数据进行操作
                            consumer.accept(new StreamReDTO(reStringBuffer));
                            // 将GPT的回答存入消息列表
                            messageList.add(new MessageDTO(MessageRoleConstant.ASSISTANT, reStringBuffer.toString()));
                            sseEmitter.complete();
                            return;
                        }
                        GptChatStreamResponse chatStreamResponse = JSONUtil.toBean(responseJson, GptChatStreamResponse.class);
                        GptChatStreamChoice choice = chatStreamResponse.getChoices().get(0);
                        String content = choice.getDelta().getContent();
                        if (StrUtil.isNotBlank(content)){
                            // System.out.println(choice.getDelta().getContent());
                            reStringBuffer.append(choice.getDelta().getContent());
                            try {
                                sseEmitter.send(choice);
                                // TimeUnit.SECONDS.sleep(1);
                            } catch (Exception e) {
                                // 将当前的消息删除
                                messageList.remove(message.length()-1);
                                sseEmitter.completeWithError(e);
                            }
                        }
                    });

        });
    }

    /**
     * 获取历史消息
     */
    public List<MessageDTO> getHistoricalMessage(){
        return this.messageList;
    }

    /**
     * 控制历史消息信息的长度
     */
    private void sizeControl(List<MessageDTO> messageList){
        if (messageList.size() >= chatGptConfig.getChatLargestContext() * 2){
            messageList.remove(0);
            messageList.remove(0);
        }
    }

    /**
     * 直接获取gpt请求对象
     * @param requestBody 请求体
     * @return            请求对象
     */
    private HttpRequest getChatGptRequest(GptChatRequestBody requestBody, String url){
        return HttpUtil.createPost(url)
                .setHttpProxy(chatGptConfig.proxyUrl, chatGptConfig.proxyPort)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + chatGptConfig.apiKey)
                .body(JSONUtil.toJsonStr(requestBody));
    }

}
