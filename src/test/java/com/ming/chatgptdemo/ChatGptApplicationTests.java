package com.ming.chatgptdemo;

import com.ming.chatgptdemo.client.ChatClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChatGptApplicationTests {

    @Autowired
    private ChatClient chatClient;

    @Test
    void contextLoads() {
        ChatClient chatClient1 = new ChatClient();
        System.out.println(chatClient.send("你好啊"));
    }



    // @Test
    // public void testGPT(){
    //     System.out.println(chatService.send("你好"));
    // }

}
