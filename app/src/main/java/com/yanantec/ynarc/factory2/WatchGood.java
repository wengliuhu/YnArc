package com.yanantec.ynarc.factory2;

import com.yanantec.annotation.Product;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/2/21
 * Describe
 */
@Product(superClass = IGood.class, key = "watch")
public class WatchGood implements IGood
{
    @Override
    public double price()
    {
        return 500;
    }
}
