package com.csj.ojbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csj.ojbackend.model.dto.questionSubmit.OwnQuestionSubmitResponse;
import com.csj.ojbackend.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.csj.ojbackend.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.csj.ojbackend.model.entity.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.csj.ojbackend.model.entity.User;
import com.csj.ojbackend.model.vo.QuestionSubmitVO;

/**
* @author 23200
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2024-01-03 18:25:08
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 题目提交
     *
     * @param questionSubmitRequest
     * @param loginUser
     * @return
     */
    Long doPostQuestion(QuestionSubmitAddRequest questionSubmitRequest, User loginUser);

    /**
     * 帖子点赞（内部服务）
     * @param userId
     * @param questionId
     * @return
     */
    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);



    OwnQuestionSubmitResponse getMySubmitted(long id);

    String getRightAnswer(long id);
}
