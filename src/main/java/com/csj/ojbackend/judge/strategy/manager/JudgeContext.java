package com.csj.ojbackend.judge.strategy.manager;
import com.csj.ojbackend.model.dto.question.JudgeCase;
import com.csj.ojbackend.model.dto.question.JudgeConfig;
import com.csj.ojbackend.model.dto.questionSubmit.JudgeInfo;
import com.csj.ojbackend.model.entity.Question;
import com.csj.ojbackend.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;


/**
 * @author:csj
 * @version:1.0
 */
@Data
public class JudgeContext {
   private JudgeInfo judgeInfo; //代码沙箱执行后返回的信息

   private List<String> inputList; //输入示例

   private List<String> outputList;//输出示例

   private List<JudgeCase> judgeCaseList;//示例

   private Question question;

   private QuestionSubmit questionSubmit;

}
