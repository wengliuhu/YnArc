package com.yanantec.ynbus.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/1/29
 * Describe:事件注册,支持在父类中处理
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface OnMessageIncludeSuper
{
    /**
     * 事件的key
     * @return key
     */
    String value() default "";

    /**
     * 是否一直监听
     * @return 是否总是接收
     */
    boolean always() default false;

    /**
     * 非生命周期活跃下，是否丢弃
     * @return 是否丢弃
     */
    boolean discard() default false;
}
