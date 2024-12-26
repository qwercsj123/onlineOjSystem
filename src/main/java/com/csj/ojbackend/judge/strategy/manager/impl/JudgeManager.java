package com.csj.ojbackend.judge.strategy.manager.impl;

import com.csj.ojbackend.judge.strategy.manager.JavaJudge;
import com.csj.ojbackend.model.dto.questionSubmit.JudgeInfo;
import com.csj.ojbackend.model.entity.QuestionSubmit;
import com.csj.ojbackend.judge.strategy.manager.DefaultJudge;
import com.csj.ojbackend.judge.strategy.manager.JudgeContext;
import com.csj.ojbackend.judge.strategy.manager.JudgeStrategy;
import org.springframework.stereotype.Service;

/**
 * @author:csj
 * @version:1.0
 */
@Service
public class JudgeManager {
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudge();
        if ("java".equals(language)) {
            judgeStrategy = new JavaJudge();
        }
        return judgeStrategy.judge(judgeContext);
    }

}
