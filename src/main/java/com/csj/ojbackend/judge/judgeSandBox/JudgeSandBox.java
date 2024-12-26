package com.csj.ojbackend.judge.judgeSandBox;

import com.csj.ojbackend.judge.model.ExecuteSandCodeResponse;
import com.csj.ojbackend.judge.model.ExecuteSandCodeRequest;
import org.springframework.stereotype.Service;

/**
 * 执行代码的代码沙箱
 * @author:csj
 * @version:1.0
 */
@Service
public interface JudgeSandBox {
    ExecuteSandCodeResponse judge(ExecuteSandCodeRequest executeSandCodeRequest);
}
