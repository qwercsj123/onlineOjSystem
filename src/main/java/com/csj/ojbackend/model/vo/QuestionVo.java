package com.csj.ojbackend.model.vo;

import cn.hutool.json.JSONUtil;
import com.csj.ojbackend.model.dto.question.JudgeCase;
import com.csj.ojbackend.model.dto.question.JudgeConfig;
import com.csj.ojbackend.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

/**
 * 将数据库中的question信息进行脱敏处理
 * @author:csj
 * @version:1.0
 */
@Data
public class QuestionVo {
    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题目通过数
     */
    private Integer acceptedNum;


    /**
     * 判题配置（json 对象）
     */
    private JudgeConfig judgeConfig;

    private JudgeCase judgeCase;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private UserVO userVO;


    /**
     * 包装类转对象
     *List--->String
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionVo questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        List<String> tagList = questionVO.getTags();
        if (tagList != null) {
            question.setTags(JSONUtil.toJsonStr(tagList));
        }
        JudgeConfig voJudgeConfig = questionVO.getJudgeConfig();
        if (voJudgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(voJudgeConfig));
        }
        return question;
    }

    /**
     * 对象转包装类
     *String---->List
     * @param question
     * @return
     */
    public static QuestionVo objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVo questionVO = new QuestionVo();
        BeanUtils.copyProperties(question, questionVO);
        questionVO.setTags(JSONUtil.toList(question.getTags(),String.class));
        String judgeConfig = question.getJudgeConfig();
        String substring = judgeConfig.substring(1, judgeConfig.length() - 1);
        questionVO.setJudgeConfig(JSONUtil.toBean(substring,JudgeConfig.class));

        String judgeCase = question.getJudgeCase();
        String substringstr = judgeCase.substring(1, judgeCase.length() - 1);
        questionVO.setJudgeCase(JSONUtil.toBean(substringstr,JudgeCase.class));


        return questionVO;
    }


}
