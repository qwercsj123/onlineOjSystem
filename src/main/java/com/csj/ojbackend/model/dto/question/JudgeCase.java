package com.csj.ojbackend.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:csj
 * @version:1.0
 * 题目用例
 */
@Data
public class JudgeCase {
   private String inPut;
   private String outPut;
}
