package com.csj.ojbackend.judge.judgeSandBox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.csj.ojbackend.common.ErrorCode;
import com.csj.ojbackend.exception.BusinessException;
import com.csj.ojbackend.judge.judgeSandBox.JudgeSandBox;
import com.csj.ojbackend.judge.model.ExecuteSandCodeRequest;
import com.csj.ojbackend.judge.model.ExecuteSandCodeResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * 远程调用代码沙箱
 * @author:csj
 * @version:1.0
 */
public class RemoteSandBox implements JudgeSandBox {

    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";


    @Override
    public ExecuteSandCodeResponse judge(ExecuteSandCodeRequest executeSandCodeRequest) {
        System.out.println("远程代码沙箱");
        String url = "http://8.152.203.125:8090/api/executeCode";
        String json = JSONUtil.toJsonStr(executeSandCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "executeCode remoteSandbox error, message = " + responseStr);
        }
        return JSONUtil.toBean(responseStr, ExecuteSandCodeResponse.class);
    }
}
