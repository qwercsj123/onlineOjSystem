package com.csj.ojbackend.judge.strategy.imp;

import cn.hutool.json.JSONUtil;
import com.csj.ojbackend.common.ErrorCode;
import com.csj.ojbackend.exception.BusinessException;
import com.csj.ojbackend.judge.strategy.JudgeService;
import com.csj.ojbackend.judge.strategy.manager.DefaultJudge;
import com.csj.ojbackend.judge.strategy.manager.impl.JudgeManager;
import com.csj.ojbackend.model.dto.question.JudgeCase;
import com.csj.ojbackend.model.dto.questionSubmit.JudgeInfo;
import com.csj.ojbackend.model.entity.Question;
import com.csj.ojbackend.model.entity.QuestionSubmit;
import com.csj.ojbackend.model.enums.JudgeInfoMessageEnum;
import com.csj.ojbackend.model.enums.QuestionSubmitStatusEnum;
import com.csj.ojbackend.service.QuestionService;
import com.csj.ojbackend.service.QuestionSubmitService;
import com.csj.ojbackend.judge.judgeSandBox.JudgeSandBox;
import com.csj.ojbackend.judge.judgeSandBox.JudgeSandBoxFactory;
import com.csj.ojbackend.judge.judgeSandBox.JudgeSandBoxProxy;
import com.csj.ojbackend.judge.model.ExecuteSandCodeRequest;
import com.csj.ojbackend.judge.model.ExecuteSandCodeResponse;
import com.csj.ojbackend.judge.strategy.manager.JudgeContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author:csj
 * @version:1.0
 */
@Service
public class JudgeServiceImp implements JudgeService {

    @Resource
    private QuestionService questionService;

    @Resource
    private JudgeManager judgeManager;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Value("${SandBox.type:example}")
    private String type;


    @Override
    public QuestionSubmit doJudge(Long questionSubmitId) {

        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "该问题未提交");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        question.setSubmitNum(question.getSubmitNum()+1);
        boolean update1 = questionService.updateById(question);
        if (!update1){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"添加人数错误");
        }

        if (question == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "该题目不存在");
        }
        if (questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.RUNNING.getValue())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "改题目正在判题");
        }
        //先更新题目的状态
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setQuestionId(questionId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目状态更新错误");
        }
        //调用沙箱执行代码
        JudgeSandBox judgeSandBox = JudgeSandBoxFactory.getJudgeSandBox(type);
        JudgeSandBoxProxy judgeSandBoxProxy = new JudgeSandBoxProxy(judgeSandBox);
        String code = questionSubmit.getCode();
        String language = questionSubmit.getLanguage();
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInPut).collect(Collectors.toList());

        ExecuteSandCodeRequest executeSandCodeRequest = ExecuteSandCodeRequest.builder().
                code(code).
                language(language).
                inputList(inputList).build();
        ExecuteSandCodeResponse executeSandCodeResponse = judgeSandBoxProxy.judge(executeSandCodeRequest);
        //executeSandCodeResponse就是执行的结果
        List<String> outputList = executeSandCodeResponse.getOutputList();

        //此处已经执行完了,下面的过程就是进行比较
        //1.比较输入和输出的个数是否相同
        //2.判断两者是否是完全相等的
        //3.内存和时间的比较
        // 5）根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeSandCodeResponse.getJudgeInfo());//这里面的judgeInfo是代码沙箱执行后的结果
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        //此时输出的judgeInfo是最终的结果(已经比较过了和预期数据)
        DefaultJudge defaultJudge = new DefaultJudge();
        JudgeInfo judgeInfo = defaultJudge.judge(judgeContext);
        if(judgeInfo.getMemory()==0){
            judgeInfo.setMemory(10000l);
        }
        // 6）修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        //此处加个判断
        String message = judgeInfo.getMessage();
        QuestionSubmitStatusEnum questionSubmitStatusEnum=QuestionSubmitStatusEnum.FAILED;
        if (message.equals(JudgeInfoMessageEnum.ACCEPTED.getValue())){
            questionSubmitStatusEnum=QuestionSubmitStatusEnum.SUCCEED;
            questionSubmitUpdate.setStatus(questionSubmitStatusEnum.getValue());
        }
       if (message.equals(JudgeInfoMessageEnum.WAITING.getValue())){
           questionSubmitStatusEnum=QuestionSubmitStatusEnum.WAITING;
           questionSubmitUpdate.setStatus(questionSubmitStatusEnum.getValue());
       }
       questionSubmitUpdate.setStatus(questionSubmitStatusEnum.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionSubmitId);
        if (questionSubmitResult.getStatus().equals(QuestionSubmitStatusEnum.SUCCEED.getValue())){
            question.setAcceptedNum(question.getAcceptedNum()+1);
            boolean result = questionService.updateById(question);
            if (!result){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"修改通过人数错误");
            }
        }
        return questionSubmitResult;
    }
}
