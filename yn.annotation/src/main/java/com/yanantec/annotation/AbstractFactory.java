package com.yanantec.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/2/4
 * Describe：抽象工厂，作用于接口或者抽象类
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface AbstractFactory
{

    /**
     * 实现类的类名
     * @return
     */
    String name() default "AbstractFactoryImp";

}
