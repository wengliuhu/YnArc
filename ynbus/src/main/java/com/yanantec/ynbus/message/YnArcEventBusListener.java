package com.yanantec.ynbus.message;

import androidx.lifecycle.LifecycleOwner;

import com.yanantec.ynbus.annotation.handler.YnBusAnnotationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2020/11/13
 * Describe:通过继承此接口，实现事件注册
 */
public interface YnArcEventBusListener
{
    /**
     * 初始化，注册到事件总线
     * @param lifecycleOwner
     */
    default void injectBus(LifecycleOwner lifecycleOwner){
        Method[] methods = lifecycleOwner.getClass().getDeclaredMethods();
        if (methods == null || methods.length == 0) {
            return;
        }
        YnBusAnnotationHandler handler = new YnBusAnnotationHandler();
        for (Method method : methods) {

            Annotation[] annotations = method.getAnnotations();
            if (annotations == null || annotations.length == 0) {
                continue;
            }

            for (Annotation annotation : annotations) {
                handler.collectMethodAnnotation(method, annotation);
            }
        }
        handler.handleMethodAnnotation(lifecycleOwner);
    }
}
