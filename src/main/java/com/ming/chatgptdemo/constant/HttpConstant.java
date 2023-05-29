package com.ming.chatgptdemo.constant;

/**
 * http请求的常量
 */
public class HttpConstant {

    /**
     * 以前调用GPT的api
     */
    public static final String OLD_API_URL = "https://api.openai.com/v1/completions";

    /**
     * 最新调用GPT的api
     */
    public static final String API_URL = "https://api.openai.com/v1/chat/completions";

    /**
     * 请求类型
     */
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     * JSON 格式
     */
    public static final String APPLICATION_JSON = "application/json;charset=utf-8";

    /**
     * 请求信息头密钥
     */
    public static final String AUTHORIZATION = "Authorization";

    /**
     * api密钥前面需要加上的值
     */
    public static final String BEARER = "Bearer ";
}
