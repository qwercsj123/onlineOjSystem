package com.csj.ojbackend.judge.model;

import com.csj.ojbackend.model.dto.questionSubmit.JudgeInfo;
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
public class ExecuteSandCodeResponse {
    //代码沙箱执行后的状态
    private Integer status;
    //代码沙箱执行后结果
    private List<String> outputList;
    //代码沙箱执行后的信息
    private String message;
    private JudgeInfo judgeInfo;
}
