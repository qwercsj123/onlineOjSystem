package com.csj.ojbackend.judge.strategy;

import com.csj.ojbackend.model.entity.QuestionSubmit;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 真正进行判题的地方
 * @author:csj
 * @version:1.0
 */
public interface JudgeService {
    QuestionSubmit doJudge(Long questionId);
}
