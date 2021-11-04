package com.yanantec.ynarc.superclasses;

import com.yanantec.annotation.AbstractFactory;
import com.yanantec.annotation.Produce;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/11/4 14:29
 * Describe：
 */
@AbstractFactory(name = "ManFactory")
public interface IManFactory {
    @Produce()
    IMan getMan(String key, Teacher teacher);
}
