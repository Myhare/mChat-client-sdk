package com.ming.chatgptdemo.utils;

import com.ming.chatgptdemo.model.dto.MessageDTO;

import java.util.LinkedList;
import java.util.List;

/**
 * 存储消息工具类
 */
public class MessageUtil {

    public static final ThreadLocal<List<MessageDTO>> MESSAGE_HOLDER = new ThreadLocal<>();

    /**
     * 获取消息列表
     */
    public static List<MessageDTO> getMessageList(){
        if (MESSAGE_HOLDER.get() == null){
            setMessageList(new LinkedList<>());
        }
        return MESSAGE_HOLDER.get();
    }

    /**
     * 添加消息列表
     */
    public static void setMessageList(List<MessageDTO> messageList){
        MESSAGE_HOLDER.set(messageList);
    }


}
