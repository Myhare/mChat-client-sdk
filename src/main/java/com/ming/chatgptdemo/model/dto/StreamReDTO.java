package com.ming.chatgptdemo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Stream流方式请求api方法返回出去的对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreamReDTO {

    /**
     * 完整的回答结果
     */
    private StringBuilder content;

}
