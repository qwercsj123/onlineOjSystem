package com.csj.ojbackend.model.entity.mapper;

import com.csj.ojbackend.model.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 23200
* @description 针对表【question(题目)】的数据库操作Mapper
* @createDate 2024-01-03 18:25:03
* @Entity com.csj.ojbackend.model.entity.Question
*/
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

}




