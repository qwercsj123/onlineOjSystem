package com.csj.ojbackend.judge.strategy.manager;
import cn.hutool.json.JSONUtil;
import com.csj.ojbackend.judge.strategy.manager.JudgeContext;
import com.csj.ojbackend.judge.strategy.manager.JudgeStrategy;
import com.csj.ojbackend.model.dto.question.JudgeCase;
import com.csj.ojbackend.model.dto.question.JudgeConfig;
import com.csj.ojbackend.model.dto.questionSubmit.JudgeInfo;
import com.csj.ojbackend.model.entity.Question;
import com.csj.ojbackend.model.enums.JudgeInfoMessageEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 默认执行步骤
 * @author:csj
 * @version:1.0
 */
public class DefaultJudge implements JudgeStrategy {

    @Override
    public JudgeInfo judge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        Long memory = judgeInfo.getMemory();
        Long time = judgeInfo.getTime();
        List<String> outputList = judgeContext.getOutputList();
        List<String> newOutputList=new ArrayList<>();
        for (int i=0;i<outputList.size();i++){
            String outputStr=outputList.get(i);
            newOutputList.add(outputStr.replace("\n",""));
        }

        Question question = judgeContext.getQuestion();

        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        List<String> needOutputList=new ArrayList<>();
        for (int i=0;i<judgeCaseList.size();i++){
            String outPut = judgeCaseList.get(i).getOutPut();
            if (outPut.length()>1){
                String substring = outPut.substring(1, outPut.length() - 1);
                String[] split = substring.split(",");
                needOutputList = Arrays.asList(split);
            }
            needOutputList=Arrays.asList(outPut);
        }

        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setMemory(memory);
        judgeInfoResponse.setTime(time);



        // 先判断沙箱执行的结果输出数量是否和预期输出数量相等
        if (newOutputList.size() !=needOutputList.size()) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        // 依次判断每一项输出和预期输出是否相等
        for (int i = 0; i < newOutputList.size(); i++) {
            if (!(newOutputList.get(i)).equals(needOutputList.get(i))) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
        }
        // 判断题目限制
        String judgeConfigStr = question.getJudgeConfig();
        String substring = judgeConfigStr.substring(1, judgeConfigStr.length() - 1);
        JudgeConfig judgeConfig = JSONUtil.toBean(substring, JudgeConfig.class);


        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        Long needTimeLimit = judgeConfig.getTimeLimit();
        if (memory > needMemoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        if (time > needTimeLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }
}
