package com.csj.ojbackend.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csj.ojbackend.annotation.AuthCheck;
import com.csj.ojbackend.common.BaseResponse;
import com.csj.ojbackend.common.DeleteRequest;
import com.csj.ojbackend.common.ErrorCode;
import com.csj.ojbackend.common.ResultUtils;
import com.csj.ojbackend.constant.UserConstant;
import com.csj.ojbackend.exception.BusinessException;
import com.csj.ojbackend.exception.ThrowUtils;
import com.csj.ojbackend.model.dto.question.*;
import com.csj.ojbackend.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.csj.ojbackend.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.csj.ojbackend.model.entity.Question;
import com.csj.ojbackend.model.entity.QuestionSubmit;
import com.csj.ojbackend.model.entity.User;
import com.csj.ojbackend.model.vo.QuestionSubmitVO;
import com.csj.ojbackend.model.vo.QuestionVo;
import com.csj.ojbackend.service.QuestionService;
import com.csj.ojbackend.service.QuestionSubmitService;
import com.csj.ojbackend.service.UserService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 题目接口
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    private final static Gson GSON = new Gson();
    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;
    /**
     * 创建题目
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping ("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        List<JudgeCase> judgeCase = questionAddRequest.getJudgeCase();
        if(judgeCase!=null){
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCase));
        }
        List<JudgeConfig> judgeConfig = questionAddRequest.getJudgeConfig();
        if (judgeConfig!=null){
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }
        questionService.validQuestion(question, true);
        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }


    /**
     * 删除题目
     * @param deleteRequest 里面是问题的id
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.removeById(id);
        return ResultUtils.success(result);
    }


    /**
     * 更新题目（仅管理员）
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionEditRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        List<JudgeCase> judgeCase = questionUpdateRequest.getJudgeCase();
        if(judgeCase!=null){
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCase));
        }
        List<JudgeConfig> judgeConfig = questionUpdateRequest.getJudgeConfig();
        if (judgeConfig!=null){
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }


    /**
     * 根据 id 获取题目
     * @param id
     * @return
     */
    @GetMapping("/get/questionVoById")
    public BaseResponse<QuestionVo> getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question Question = questionService.getById(id);
        if (Question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionVO(Question, request));
    }

    /**
     * 根据 id 获取题目
     * @param id
     * @return
     */
    @GetMapping("/get/questionById")
    public BaseResponse<Question> getQuestionById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser=userService.getLoginUser(request);
        if(!loginUser.getId().equals(question.getUserId()) && !userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(question);
    }



    /**
     * 分页获取题目列表（仅管理员）
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> QuestionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(QuestionPage);
    }


    /**
     * 分页获取列表（封装类）
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/question")
    public BaseResponse<Page<QuestionVo>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> QuestionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(QuestionPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/question")
    public BaseResponse<Page<QuestionVo>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> QuestionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(QuestionPage, request));
    }



    /**
     * 编辑（用户）
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        List<JudgeCase> judgeCase = questionEditRequest.getJudgeCase();
        if(judgeCase!=null){
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCase));
        }
        List<JudgeConfig> judgeConfig = questionEditRequest.getJudgeConfig();
        if (judgeConfig!=null){
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        User loginUser = userService.getLoginUser(request);
        long id = questionEditRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }
    /**
     * 提交题目
     * @param questionSubmitRequest
     * @param request
     * @return resultNum
     */
    @PostMapping("/question_submit/submit")
    public BaseResponse<Long> doSubmitQuestion(@RequestBody QuestionSubmitAddRequest questionSubmitRequest,
                                               HttpServletRequest request) {
        if (questionSubmitRequest == null || questionSubmitRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能提交题目
        final User loginUser = userService.getLoginUser(request);
        Long result = questionSubmitService.doPostQuestion(questionSubmitRequest, loginUser);
        return ResultUtils.success(result);
    }


    /**
     * 查询自己写过的代码用分页
     * @param questionSubmitQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/question_submit/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(
            @RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 从数据库中查询原始的题目提交分页信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        // 返回脱敏信息
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
    }

}
