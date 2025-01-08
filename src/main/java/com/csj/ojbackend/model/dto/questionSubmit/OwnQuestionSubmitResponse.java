package com.csj.ojbackend.model.dto.questionSubmit;


import com.csj.ojbackend.model.dto.question.JudgeCase;
import com.csj.ojbackend.model.dto.question.JudgeConfig;
import lombok.Data;

/**
 * 返回给前端的数据（查看自己的配置）
 */

@Data
public class OwnQuestionSubmitResponse {


    private JudgeConfig judgeConfig;

    private JudgeCase judgeCase;

    private String language;
    private String code;

}
