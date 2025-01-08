package com.csj.ojbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csj.ojbackend.common.ErrorCode;
import com.csj.ojbackend.constant.CommonConstant;
import com.csj.ojbackend.exception.BusinessException;
import com.csj.ojbackend.model.dto.question.JudgeCase;
import com.csj.ojbackend.model.dto.question.JudgeConfig;
import com.csj.ojbackend.model.dto.questionSubmit.OwnQuestionSubmitResponse;
import com.csj.ojbackend.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.csj.ojbackend.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.csj.ojbackend.model.entity.*;
import com.csj.ojbackend.model.enums.QuestionSubmitLanguageEnum;
import com.csj.ojbackend.model.enums.QuestionSubmitStatusEnum;
import com.csj.ojbackend.model.vo.QuestionSubmitVO;
import com.csj.ojbackend.model.vo.QuestionVo;
import com.csj.ojbackend.service.QuestionService;
import com.csj.ojbackend.service.QuestionSubmitService;
import com.csj.ojbackend.model.entity.mapper.QuestionSubmitMapper;
import com.csj.ojbackend.service.UserService;
import com.csj.ojbackend.utils.SqlUtils;
import com.csj.ojbackend.judge.strategy.JudgeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
* @author 23200
* @description 针对表【question_submit(题目提交)】的数据库操作Service实现
* @createDate 2024-01-03 18:25:08
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService{
    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private JudgeService judgeService;

    /**
     * 提交题目
     * @param questionSubmitRequest
     * @param loginUser
     * @return
     */
    @Override
    public Long doPostQuestion(QuestionSubmitAddRequest questionSubmitRequest, User loginUser) {

        String language = questionSubmitRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum==null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"编程语言错误");
        }
        Long questionId = questionSubmitRequest.getQuestionId();
        // 查询题目，并且判断题目是否存在
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        long userId = loginUser.getId();
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setLanguage(language);
        questionSubmit.setCode(questionSubmitRequest.getCode());
        questionSubmit.setJudgeInfo("{}");//todo
        boolean result = this.save(questionSubmit);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"提交错误");
    }
        Long questionSubmitId = questionSubmit.getId();
//         执行判题服务
        CompletableFuture.runAsync(() -> {
            //用消息队列来实现
            judgeService.doJudge(questionSubmitId);
        });

        return questionSubmit.getId();
    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        long userId = loginUser.getId();
        // 处理脱敏
        if (userId != questionSubmit.getUserId() && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    /**
     * 分页查询自己写的代码
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }

        // 1. 关联查询用户信息
//        Set<Long> userIdSet = questionSubmitList.stream().map(questionSubmit -> {
//            boolean admin = userService.isAdmin(loginUser);
//            if (admin){
//                return questionSubmit.getUserId();
//            }
//            return loginUser.getId();
//        }).collect(Collectors.toSet());//查出所有的用户的id
        Set<Long> userIdSet=new HashSet<>();
        for(QuestionSubmit questionSubmit:questionSubmitList){
            if (userService.isAdmin(loginUser)){
                //是管理员的情况
                userIdSet.add(questionSubmit.getUserId());
                continue;
            }else{
                userIdSet.add(loginUser.getId());
                break;
            }
        }
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        List<QuestionSubmitVO> questionSubmitVOList = new ArrayList<>();


        for (QuestionSubmit questionSubmit:questionSubmitList){
            Long userId = questionSubmit.getUserId();
            if (userIdUserListMap.containsKey(userId)){
                //包含在其中
                QuestionSubmitVO questionVO = QuestionSubmitVO.objToVo(questionSubmit);
                User user=userIdUserListMap.get(userId).get(0);
                questionVO.setUserVO(userService.getUserVO(user));
                questionSubmitVOList.add(questionVO);
            }
            questionSubmitVOPage.setRecords(questionSubmitVOList);
            questionSubmitVOPage.setTotal(questionSubmitVOList.size());
        }

        // 填充信息

        return questionSubmitVOPage;
//        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
//                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
//                .collect(Collectors.toList());
//        questionSubmitVOPage.setRecords(questionSubmitVOList);
//        return questionSubmitVOPage;
    }

    @Override
    public OwnQuestionSubmitResponse getMySubmitted(long id) {
        if (id<0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"未取到题目的id");
        }
        QuestionSubmit questionSubmit = this.getById(id);
        OwnQuestionSubmitResponse ownQuestionSubmitResponse = new OwnQuestionSubmitResponse();

        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();



        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        QuestionVo questionVo = QuestionVo.objToVo(question);



        JudgeCase judgeCase = questionVo.getJudgeCase();
        JudgeConfig judgeConfig = questionVo.getJudgeConfig();




        ownQuestionSubmitResponse.setJudgeConfig(judgeConfig);
        ownQuestionSubmitResponse.setJudgeCase(judgeCase);
        ownQuestionSubmitResponse.setLanguage(language);
        ownQuestionSubmitResponse.setCode(code);


        return ownQuestionSubmitResponse;


    }

    @Override
    public String getRightAnswer(long id) {
        QuestionSubmit questionSubmit = this.getById(id);
        Long questionId = questionSubmit.getQuestionId();

        QueryWrapper<QuestionSubmit> questionSubmitQueryWrapper = new QueryWrapper<>();
        questionSubmitQueryWrapper.eq("questionId",questionId);
        List<QuestionSubmit> questionSubmitList = this.list(questionSubmitQueryWrapper);
        for (QuestionSubmit question:questionSubmitList){
            if (question.getStatus().equals(QuestionSubmitStatusEnum.SUCCEED)){
                return question.getCode();
            }
            break;
        }
        return "抱歉，目前无人做对该题目";
    }

}




