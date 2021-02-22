package com.yanantec.ynarc.factory2;

import com.yanantec.annotation.AbstractFactory;
import com.yanantec.annotation.Produce;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/2/21
 * Describe
 */
@AbstractFactory(name = "Worker")
public interface IWorker
{
    @Produce
    IGood createGood(String name);
}
