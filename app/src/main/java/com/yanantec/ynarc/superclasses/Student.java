package com.yanantec.ynarc.superclasses;

import com.yanantec.annotation.Product;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/11/4 14:24
 * Describe：
 */

@Product(superClasses = {IMan.class, IWoman.class}, key = "11")
public class Student implements IMan, IWoman{

    public Student(Teacher teacher) {
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
