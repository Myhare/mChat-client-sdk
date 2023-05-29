package com.ming.chatgptdemo.model.dto;

import lombok.Data;

/**
 * 传递的消息对象
 */
@Data
public class MessageDTO {

    /**
     * 角色 system、user或assistant
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 姓名（可选）
     */
    private String name;

    public MessageDTO(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public MessageDTO(String role, String content, String name) {
        this.role = role;
        this.content = content;
        this.name = name;
    }
}
