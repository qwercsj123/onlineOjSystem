package com.csj.ojbackend.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:csj
 * @version:1.0
 */
@Data
public class JudgeConfig implements Serializable {
    //时间限制单位ms
    private Long timeLimit;
    //内存限制单位kb
    private Long memoryLimit;
    //栈的限制单位kb
    private Long stackLimit;

}
