package com.yanantec.ynarc.factory2;

import com.yanantec.annotation.Product;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/2/21
 * Describe
 */
@Product(superClass = IGood.class, key = "phone")
public class PhoneGood implements IGood
{
    @Override
    public double price()
    {
        return 3000;
    }
}
