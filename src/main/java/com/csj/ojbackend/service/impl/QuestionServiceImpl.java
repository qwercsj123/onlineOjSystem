package com.csj.ojbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csj.ojbackend.common.ErrorCode;
import com.csj.ojbackend.constant.CommonConstant;
import com.csj.ojbackend.exception.BusinessException;
import com.csj.ojbackend.exception.ThrowUtils;
import com.csj.ojbackend.model.dto.question.QuestionQueryRequest;
import com.csj.ojbackend.model.entity.*;
import com.csj.ojbackend.model.vo.QuestionVo;
import com.csj.ojbackend.model.vo.UserVO;
import com.csj.ojbackend.model.entity.mapper.QuestionMapper;
import com.csj.ojbackend.service.QuestionService;
import com.csj.ojbackend.service.UserService;
import com.csj.ojbackend.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
* 针对表【question(题目)】的数据库操作Service实现
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
implements QuestionService {
    @Resource
    private UserService userService;

    /**
     * 校验前端创建的题目是否合法
     * @param question
     * @param add  如果是创建题目的话，那么add为true，如果是更新题目的话，因为在创建的时候已经检验过了，所以add为false
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title,tags, content,answer,judgeCase,judgeConfig),
                    ErrorCode.PARAMS_ERROR);
        }
        // 对标题的校验
        if (StringUtils.isNotBlank(title) && title.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        //题目的内容
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        //标准答案
        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        //判题用例
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        //判题配置
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
    }

    /**
     * 获取查询包装类
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        int current = questionQueryRequest.getCurrent();
        int pageSize = questionQueryRequest.getPageSize();
        String sortField = questionQueryRequest.getSortField();//排序字段
        String sortOrder = questionQueryRequest.getSortOrder();//排序顺序


        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 专门为管理题目设计的方法
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper1(QuestionQueryRequest questionQueryRequest) {
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        int current = questionQueryRequest.getCurrent();
        int pageSize = questionQueryRequest.getPageSize();
        String sortField = questionQueryRequest.getSortField();//排序字段
        String sortOrder = questionQueryRequest.getSortOrder();//排序顺序


        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionVo getQuestionVO(Question question, HttpServletRequest request) {
        QuestionVo questionVO = QuestionVo.objToVo(question);
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = new User();
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUserVO(userVO);
        return questionVO;
    }



    @Override
    public Page<QuestionVo> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVo> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());//查出所有的用户的id
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<QuestionVo> questionVOList = questionList.stream().map(question -> {
            QuestionVo questionVO = QuestionVo.objToVo(question);
            Long userId = question.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);//唯一性
            }
            questionVO.setUserVO(userService.getUserVO(user));
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

}




