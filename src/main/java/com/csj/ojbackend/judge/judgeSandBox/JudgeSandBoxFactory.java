package com.csj.ojbackend.judge.judgeSandBox;

import com.csj.ojbackend.judge.judgeSandBox.impl.ExampleSandBox;
import com.csj.ojbackend.judge.judgeSandBox.impl.RemoteSandBox;
import com.csj.ojbackend.judge.judgeSandBox.impl.ThirdPartSandBox;

/**
 * 采用工厂模式进行代码沙箱类别的选择
 * @author:csj
 * @version:1.0
 */
public class JudgeSandBoxFactory {
    public static JudgeSandBox getJudgeSandBox(String type){
            switch (type){
                case "example":
                    return new ExampleSandBox();
                case "remote":
                    return new RemoteSandBox();
                case "thirtPart":
                    new ThirdPartSandBox();
                default: return new ExampleSandBox();

        }
    }
}
