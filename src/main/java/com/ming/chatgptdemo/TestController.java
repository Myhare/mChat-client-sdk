package com.ming.chatgptdemo;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.ming.chatgptdemo.client.ChatClient;
import com.ming.chatgptdemo.constant.HttpConstant;
import com.ming.chatgptdemo.constant.MessageRoleConstant;
import com.ming.chatgptdemo.model.dto.GptChatChoice;
import com.ming.chatgptdemo.model.dto.GptChatStreamChoice;
import com.ming.chatgptdemo.model.dto.MessageDTO;
import com.ming.chatgptdemo.model.request.GptChatRequestBody;
import com.ming.chatgptdemo.model.response.GptChatResponse;
import com.ming.chatgptdemo.model.response.GptChatStreamResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.ming.chatgptdemo.constant.GptModelConstant.GPT_TURBO;
import static com.ming.chatgptdemo.constant.HttpConstant.*;
import static com.ming.chatgptdemo.constant.HttpConstant.BEARER;

/**
 * 测试
 */
@RestController
public class TestController {

    @Autowired
    private ChatGptConfig chatGptConfig;
    @Autowired
    private ChatClient chatClient;

    @Autowired
    private WebClient webClient;

    @GetMapping("/")
    public String test(){
        return "hello，成功进入";
    }

    private static final String[] WORDS = "The quick brown fox jumps over the lazy dog.".split(" ");

    // 创建一个线程池
    private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    private SseEmitter emitter;

    @GetMapping("/testGpt")
    public MessageDTO te(){
        return chatClient.send("你好");
    }

    /**
     * 测试SSE,创建连接
     */
    @GetMapping(path="/sse", produces= MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createConnection(){
        SseEmitter emitter = new SseEmitter();

        // 提交一个任务给线程池，没有返回值
        cachedThreadPool.execute(() -> {
            try {
                Scanner scanner = new Scanner(System.in);
                while (scanner.hasNext()) {
                    String next = scanner.next();
                    emitter.send(next);
                }

                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    // 测试流式请求数据
    @GetMapping("/stream")
    public SseEmitter streamData() {
        SseEmitter emitter = new SseEmitter();

        StringBuffer reBuffer = new StringBuffer();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        cachedThreadPool.execute(() -> {
            try {
                // 发送HTTP请求，设置stream参数为true\
                ArrayList<MessageDTO> messageDTOList = new ArrayList<>();
                messageDTOList.add(new MessageDTO(MessageRoleConstant.USER, "写一个50字左右的自我介绍"));
                GptChatRequestBody requestBody = GptChatRequestBody.builder()
                        .model(GPT_TURBO)
                        .messages(messageDTOList)
                        .stream(true)
                        .build();
                InputStream inputStream = HttpUtil.createPost("https://api.openai.com/v1/chat/completions")
                        .setHttpProxy(chatGptConfig.proxyUrl, chatGptConfig.proxyPort)
                        .header(CONTENT_TYPE, APPLICATION_JSON)
                        .header(AUTHORIZATION, BEARER + chatGptConfig.apiKey)
                        .body(JSONUtil.toJsonStr(requestBody))
                        .execute()
                        .bodyStream();

                // byte[] buffer = new byte[4096];
                // int bytesRead;
                // while ((bytesRead = inputStream.read(buffer)) != -1) {
                //     // 将数据发送给前端
                //     String data = new String(buffer, CharsetUtil.CHARSET_UTF_8);
                //     System.out.println(data);
                // }

                // System.out.println("=====================================");

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data:")) {
                        String dataJson = line.substring("data:".length()).trim();
                        // 判断是不是最后的终止符
                        if (!DONE.equals(dataJson)){
                            GptChatStreamResponse chatStreamResponse = JSONUtil.toBean(dataJson, GptChatStreamResponse.class);
                            System.out.println(chatStreamResponse);
                            GptChatStreamChoice streamChoice = chatStreamResponse.getChoices().get(0);
                            reBuffer.append(streamChoice.getDelta().getContent());
                            emitter.send(streamChoice);
                        }
                    }
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            } finally {
                emitter.complete();
            }
        });
        System.out.println(11111);
        System.out.println(reBuffer);
        return emitter;
    }

    // 测试流式请求数据
    @GetMapping("/stream2")
    public SseEmitter streamData2() {
        SseEmitter emitter = new SseEmitter();

        StringBuffer reStringBuffer = new StringBuffer();

        // 发送HTTP请求，设置stream参数为true\
        ArrayList<MessageDTO> messageDTOList = new ArrayList<>();
        messageDTOList.add(new MessageDTO(MessageRoleConstant.USER, "写一个50字左右的自我介绍"));
        GptChatRequestBody requestBody = GptChatRequestBody.builder()
                .model(GPT_TURBO)
                .messages(messageDTOList)
                .stream(true)
                .build();

        cachedThreadPool.execute(() -> {

            try {
                webClient.post()
                        .uri("https://api.openai.com/v1/chat/completions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, BEARER + chatGptConfig.apiKey)
                        .bodyValue(JSONUtil.toJsonStr(requestBody))
                        .retrieve() // 获取响应体
                        .bodyToFlux(String.class)// 处理多个响应流
                        .subscribe(responseJson -> {
                            if (DONE.equals(responseJson)){
                                // 说明消息传递完成
                                System.out.println("消息传输完成!!!");
                                System.out.println("拼接字符串完成，结果1--->");
                                System.out.println(reStringBuffer);
                                emitter.complete();
                                return;
                            }
                            GptChatStreamResponse chatStreamResponse = JSONUtil.toBean(responseJson, GptChatStreamResponse.class);
                            GptChatStreamChoice choice = chatStreamResponse.getChoices().get(0);
                            String content = choice.getDelta().getContent();
                            if (StrUtil.isNotBlank(content)){
                                System.out.println(choice.getDelta().getContent());
                                reStringBuffer.append(choice.getDelta().getContent());
                                try {
                                    emitter.send(choice);
                                    // TimeUnit.SECONDS.sleep(1);
                                } catch (Exception e) {
                                    emitter.completeWithError(e);
                                }
                            }
                        });
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
            System.out.println("拼接字符串完成，结果2--->");
            System.out.println(reStringBuffer);
        });



        return emitter;
    }



}
