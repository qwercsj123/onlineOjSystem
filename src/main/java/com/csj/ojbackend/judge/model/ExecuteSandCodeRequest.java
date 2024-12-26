package com.csj.ojbackend.judge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author:csj
 * @version:1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExecuteSandCodeRequest {
    //前端传来的代码
    private String code;
    //前端传来的输入示例
    private List<String> inputList;
    //前端传来的语言
    private String language;
}
