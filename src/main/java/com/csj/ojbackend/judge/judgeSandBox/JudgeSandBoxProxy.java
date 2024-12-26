package com.csj.ojbackend.judge.judgeSandBox;

import com.csj.ojbackend.judge.model.ExecuteSandCodeRequest;
import com.csj.ojbackend.judge.model.ExecuteSandCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码沙箱的代理模式,用来增强每个代码沙箱执行代码的能力
 * @author:csj
 * @version:1.0
 */
@Slf4j
public class JudgeSandBoxProxy implements JudgeSandBox {
    private JudgeSandBox judgeSandBox;
    public JudgeSandBoxProxy(JudgeSandBox judgeSandBox){
        this.judgeSandBox=judgeSandBox;
    }

    @Override
    public ExecuteSandCodeResponse judge(ExecuteSandCodeRequest executeSandCodeRequest) {
        log.info("请求的信息:"+executeSandCodeRequest.getInputList());
        ExecuteSandCodeResponse judge = judgeSandBox.judge(executeSandCodeRequest);
        log.info("输出的信息:"+judge.getOutputList());
        return judge;
    }
}
