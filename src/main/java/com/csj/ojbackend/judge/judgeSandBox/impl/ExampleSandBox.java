package com.csj.ojbackend.judge.judgeSandBox.impl;

import com.csj.ojbackend.judge.judgeSandBox.JudgeSandBox;
import com.csj.ojbackend.judge.model.ExecuteSandCodeRequest;
import com.csj.ojbackend.judge.model.ExecuteSandCodeResponse;
import com.csj.ojbackend.model.dto.questionSubmit.JudgeInfo;

import java.util.Arrays;
import java.util.List;

/**
 * 示例代码沙箱（用来调试代码）
 * @author:csj
 * @version:1.0
 */
public class ExampleSandBox implements JudgeSandBox {
    @Override
    public ExecuteSandCodeResponse judge(ExecuteSandCodeRequest executeSandCodeRequest) {
        List<String> ouputList = Arrays.asList("0", "1");
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMemory(1L);
        judgeInfo.setTime(2L);
        judgeInfo.setMessage("");
        ExecuteSandCodeResponse executeSandCodeResponse = new ExecuteSandCodeResponse();
        executeSandCodeResponse.setOutputList(ouputList);
        executeSandCodeResponse.setJudgeInfo(judgeInfo);
        System.out.println("示例代码沙箱");
        return executeSandCodeResponse;
    }
}
