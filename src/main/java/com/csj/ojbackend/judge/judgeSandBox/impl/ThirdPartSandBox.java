package com.csj.ojbackend.judge.judgeSandBox.impl;

import com.csj.ojbackend.judge.judgeSandBox.JudgeSandBox;
import com.csj.ojbackend.judge.model.ExecuteSandCodeRequest;
import com.csj.ojbackend.judge.model.ExecuteSandCodeResponse;


/**
 * 调用第三方的代码沙箱
 * @author:csj
 * @version:1.0
 */
public class ThirdPartSandBox implements JudgeSandBox {
    @Override
    public ExecuteSandCodeResponse judge(ExecuteSandCodeRequest executeSandCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
