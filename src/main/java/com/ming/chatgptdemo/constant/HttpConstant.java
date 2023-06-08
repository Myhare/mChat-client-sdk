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

    /**
     * 流式消息结束终止符
     */
    public static final String DONE = "[DONE]";

    /**
     * 代理本机url
     */
    public static final String PROXY_URL = "127.0.0.1";

    /**
     * 代理本机默认端口
     */
    public static final Integer PROXY_PORT = 7890;
}
