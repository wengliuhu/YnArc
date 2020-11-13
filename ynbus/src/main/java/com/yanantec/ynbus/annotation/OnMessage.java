package com.yanantec.ynbus.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2020/11/13
 * Describe:事件注册
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OnMessage
{
    /**
     * 事件的key
     * @return
     */
    String value() default "";

    /**
     * 是否一直监听
     * @return
     */
    boolean always() default false;

    /**
     * 非生命周期活跃下，是否丢弃
     * @return
     */
    boolean discard() default false;
}
