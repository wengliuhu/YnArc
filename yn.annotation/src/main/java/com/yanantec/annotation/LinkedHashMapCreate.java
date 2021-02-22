package com.yanantec.annotation;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/2/22
 * Describe:生成有序的linkedHashMap
 */
public @interface LinkedHashMapCreate
{
    /**
     * map添加的键值对的key
     * @return
     */
    String key();

    /**
     * 位置
     * @return
     */
    int position();

    /**
     * map集合的 属性名
     * @return
     */
    String mapFiled() default "LINKED_HASH_MAP";
}
