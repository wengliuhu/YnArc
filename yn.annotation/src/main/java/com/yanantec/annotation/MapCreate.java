package com.yanantec.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/2/2
 * Describe:自动生成Map的key-value键值对,value为当前类名
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface MapCreate
{
    /**
     * map添加的键值对的key
     * @return
     */
    String key();

    /**
     * map集合的 属性名
     * @return
     */
    String mapFiled() default "MAP_CREATE";

}
