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
 * Describe:工厂生产的产品,用于修饰类
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Product
{
    /**
     * 该类的映射
     * @return
     */
    String key();

    /**
     * 要转化为的基类，作用于多态（因为个类，最多有一个父类，但却可以实现多个接口，所以此处要定义该类型属于哪个基类的）
     * @return
     */
    Class superClass();
}
