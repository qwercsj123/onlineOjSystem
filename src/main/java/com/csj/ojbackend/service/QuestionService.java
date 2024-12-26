package com.csj.ojbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.csj.ojbackend.model.dto.question.QuestionQueryRequest;
import com.csj.ojbackend.model.entity.Question;
import com.csj.ojbackend.model.vo.QuestionVo;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author:csj
 * @version:1.0
 */
public interface QuestionService extends IService<Question> {
    /**
     * 校验题目是否合法
     * @param question
     * @param add
     */
    void validQuestion(Question question, boolean add);

    /**
     * 进行分页查询的条件
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);



    QueryWrapper<Question> getQueryWrapper1(QuestionQueryRequest questionQueryRequest);


    /**
     * 获取问题封装
     * @param question
     * @param request
     * @return
     */
    QuestionVo getQuestionVO(Question question, HttpServletRequest request);



    /**
     * 分页获取问题封装
     * @param questionPage
     * @param request
     * @return
     */
    Page<QuestionVo> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);


}
