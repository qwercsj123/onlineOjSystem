package com.csj.ojbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csj.ojbackend.common.BaseResponse;
import com.csj.ojbackend.common.ErrorCode;
import com.csj.ojbackend.common.ResultUtils;
import com.csj.ojbackend.exception.BusinessException;
import com.csj.ojbackend.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.csj.ojbackend.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.csj.ojbackend.model.entity.QuestionSubmit;
import com.csj.ojbackend.model.entity.User;
import com.csj.ojbackend.model.vo.QuestionSubmitVO;
import com.csj.ojbackend.service.QuestionSubmitService;
import com.csj.ojbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 提交题目接口
 */
@RestController
@RequestMapping("/questionSubmit")
@Slf4j
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

//    /**
//     * 提交题目
//     * @param questionSubmitRequest
//     * @param request
//     * @return resultNum
//     */
//    @PostMapping("/submit")
//    public BaseResponse<Long> doSubmitQuestion(@RequestBody QuestionSubmitAddRequest questionSubmitRequest,
//                                         HttpServletRequest request) {
//        if (questionSubmitRequest == null || questionSubmitRequest.getQuestionId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        // 登录才能提交题目
//        final User loginUser = userService.getLoginUser(request);
//        Long result = questionSubmitService.doPostQuestion(questionSubmitRequest, loginUser);
//        return ResultUtils.success(result);
//    }
//
//
//    /**
//     * 查询自己写过的代码用分页
//     * @param questionSubmitQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/list/page")
//    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(
//            @RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
//       long current = questionSubmitQueryRequest.getCurrent();
//      long size = questionSubmitQueryRequest.getPageSize();
//       // 从数据库中查询原始的题目提交分页信息
//       Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
//               questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
//       final User loginUser = userService.getLoginUser(request);
//       // 返回脱敏信息
//      return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
//    }



}
