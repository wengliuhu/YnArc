package com.yanantec.ynarc.superclasses;

import com.yanantec.annotation.AbstractFactory;
import com.yanantec.annotation.Produce;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/11/4 14:52
 * Describe：
 */
@AbstractFactory(name = "WomanFactory")
public interface IWomanFactory {

    @Produce()
    IWoman getWoMan(String key, Teacher teacher);
}
