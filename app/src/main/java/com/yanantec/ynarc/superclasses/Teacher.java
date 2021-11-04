package com.yanantec.ynarc.superclasses;

import com.yanantec.annotation.Product;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/11/4 14:28
 * Describe：
 */
@Product(superClasses = {IMan.class, IWoman.class}, key = "22")
public class Teacher implements IMan, IWoman{

    public Teacher(Teacher teacher) {
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getAge() {
        return 0;
    }
}
