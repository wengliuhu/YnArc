package com.yanantec.ynbus.message;

import androidx.lifecycle.LifecycleOwner;

import com.yanantec.ynbus.annotation.handler.YnBusAnnotationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        List<Method> allMethods = new ArrayList<>();
        allMethods.addAll(Arrays.asList(methods));
        if (lifecycleOwner.getClass().getSuperclass() != null){
            Method superMethods[] = lifecycleOwner.getClass().getSuperclass().getDeclaredMethods();
            if (superMethods != null && superMethods.length != 0){
                allMethods.addAll(Arrays.asList(superMethods));
            }
        }
        YnBusAnnotationHandler handler = new YnBusAnnotationHandler();
        for (Method method : allMethods) {

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
