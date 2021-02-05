package com.yanantec.ynarc.factory;

import com.yanantec.annotation.AbstractFactory;
import com.yanantec.annotation.Produce;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/2/4
 * Describe
 */
@AbstractFactory()
public interface Factory
{
    @Produce
    IProduct creatProduct(String key);

//    @Produce
//    void create();

//    IProduct creatProduct2(String key);
}
