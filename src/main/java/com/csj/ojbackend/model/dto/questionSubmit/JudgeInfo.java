package com.csj.ojbackend.model.dto.questionSubmit;

import lombok.Data;

/**
 * @author:csj
 * @version:1.0
 * 判题信息
 */
@Data
public class JudgeInfo {
    private String message;
    private Long memory;
    private Long time;
}
