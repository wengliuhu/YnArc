package com.yanantec.ynbus.annotation.handler;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import com.yanantec.ynbus.annotation.OnMessage;
import com.yanantec.ynbus.annotation.OnMessageIncludeSuper;
import com.yanantec.ynbus.message.YnArchEventBus;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2020/11/13
 * Describe:事件总线注解处理 实现
 */
public class YnBusAnnotationHandler
{
    private String TAG = YnBusAnnotationHandler.class.toString();
    Map<MessageAnnotation, Method> nActionMethodMap;

    /**
     * 收集注解方法
     * @param method
     * @param annotation
     */
    public void collectMethodAnnotation(Method method, Annotation annotation) {
        if (annotation instanceof OnMessage) {
            // 开始注册
            String annotationValue = ((OnMessage) annotation).value();

            //默认使用OnMessage 方法名
            String action = method.getDeclaringClass().getSimpleName() + "&&" + method.getName();
            if (nActionMethodMap == null) {
                nActionMethodMap = new HashMap<>();
            }
            MessageAnnotation messageAnnotation = new MessageAnnotation();
            messageAnnotation.setAction(action);
            messageAnnotation.setAlwaysActive(((OnMessage) annotation).always());
            messageAnnotation.setDiscard(((OnMessage) annotation).discard());
            nActionMethodMap.put(messageAnnotation, method);
            if (!TextUtils.isEmpty(annotationValue)) {
                //如果OnMessage未指定名称，那么只使用 : ClassName&&methodName
                MessageAnnotation assignMessageAnnotation = new MessageAnnotation();
                assignMessageAnnotation.setAction(annotationValue);
                assignMessageAnnotation.setAlwaysActive(((OnMessage) annotation).always());
                assignMessageAnnotation.setDiscard(((OnMessage) annotation).discard());
                nActionMethodMap.put(assignMessageAnnotation, method);
            }
        }else if (annotation instanceof OnMessageIncludeSuper){
            // 开始注册
            String annotationValue = ((OnMessageIncludeSuper) annotation).value();

            //默认使用OnMessage 方法名
            String action = method.getDeclaringClass().getSimpleName() + "&&" + method.getName();
            if (nActionMethodMap == null) {
                nActionMethodMap = new HashMap<>();
            }
            MessageAnnotation messageAnnotation = new MessageAnnotation();
            messageAnnotation.setAction(action);
            messageAnnotation.setAlwaysActive(((OnMessageIncludeSuper) annotation).always());
            messageAnnotation.setDiscard(((OnMessageIncludeSuper) annotation).discard());
            nActionMethodMap.put(messageAnnotation, method);
            if (!TextUtils.isEmpty(annotationValue)) {
                //如果OnMessage未指定名称，那么只使用 : ClassName&&methodName
                MessageAnnotation assignMessageAnnotation = new MessageAnnotation();
                assignMessageAnnotation.setAction(annotationValue);
                assignMessageAnnotation.setAlwaysActive(((OnMessageIncludeSuper) annotation).always());
                assignMessageAnnotation.setDiscard(((OnMessageIncludeSuper) annotation).discard());
                nActionMethodMap.put(assignMessageAnnotation, method);
            }
        }
    }


    /**
     * 处理注解方法
     * @param annotationOwner
     */
    public void handleMethodAnnotation(Object annotationOwner) {

        if (annotationOwner instanceof LifecycleOwner) {
            if (nActionMethodMap != null && nActionMethodMap.size() > 0) {
                Iterator<Map.Entry<MessageAnnotation, Method>> iterator = nActionMethodMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<MessageAnnotation, Method> entry = iterator.next();
                    MessageAnnotation messageAnnotation = entry.getKey();

                    final Method finalMethod = entry.getValue();
                    if(messageAnnotation.isAlwaysActive()){
                        YnArchEventBus.get().with(messageAnnotation.getAction(), Object.class).observe((LifecycleOwner) annotationOwner, data -> {
                            invokeMessageMethod(finalMethod,annotationOwner,data);
                        },true);

                    }else {
                        if(messageAnnotation.isDiscard()){
                            YnArchEventBus.get().with(messageAnnotation.getAction(), Object.class).observe((LifecycleOwner) annotationOwner, data -> {
                                invokeMessageMethod(finalMethod,annotationOwner,data);
                            },false,true);
                        }else{
                            YnArchEventBus.get().with(messageAnnotation.getAction(), Object.class).observe((LifecycleOwner) annotationOwner, data -> {
                                invokeMessageMethod(finalMethod,annotationOwner,data);
                            });
                        }

                    }

                }
                nActionMethodMap.clear();
                nActionMethodMap = null;
            }
        }

    }

    /**
     * 反射注解的方法
     * @param finalMethod
     * @param annotationOwner
     * @param data
     */
    private void invokeMessageMethod(Method finalMethod,Object annotationOwner,Object data){
        try {
            Class<?>[] parameterTypes = finalMethod.getParameterTypes();
            // 防止使用者把方法定义为私有的
            finalMethod.setAccessible(true);
            if (parameterTypes == null || parameterTypes.length == 0) {
                finalMethod.invoke(annotationOwner);
            } else if (parameterTypes.length == 1) {
                if (data == null) {
                    finalMethod.invoke(annotationOwner, data);
                } else {
                    Class parameterClass = parameterTypes[0];

                    if (parameterClass.isAssignableFrom(data.getClass())) {
                        finalMethod.invoke(annotationOwner, data);
                    }  else {
                        finalMethod.invoke(annotationOwner, data);
                    }

                }

            }
        } catch (IllegalAccessException e) {
            Log.e(TAG, "onMessage method invoke fail IllegalAccessException:" + e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e(TAG, "onMessage method invoke fail InvocationTargetException"+ e.getMessage());
            throw new RuntimeException(e.getTargetException());
        }
    }

    class MessageAnnotation{
        String action;
        boolean isAlwaysActive;
        boolean discard;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public boolean isAlwaysActive() {
            return isAlwaysActive;
        }

        public void setAlwaysActive(boolean alwaysActive) {
            isAlwaysActive = alwaysActive;
        }

        public boolean isDiscard() {
            return discard;
        }

        public void setDiscard(boolean discard) {
            this.discard = discard;
        }
    }

}
