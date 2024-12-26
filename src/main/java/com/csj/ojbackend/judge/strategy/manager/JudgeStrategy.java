package com.csj.ojbackend.judge.strategy.manager;

import com.csj.ojbackend.model.dto.questionSubmit.JudgeInfo;

/**
 * 执行判题的过程
 * @author:csj
 * @version:1.0
 */
public interface JudgeStrategy {
    JudgeInfo judge(JudgeContext judgeContext);
}
