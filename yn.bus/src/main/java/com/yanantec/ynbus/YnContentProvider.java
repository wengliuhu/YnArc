package com.yanantec.ynbus;

import android.app.Activity;
import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.yanantec.ynbus.annotation.handler.YnBusAnnotationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2020/11/16
 * Describe
 */
public class YnContentProvider extends ContentProvider
{
    @Override
    public boolean onCreate()
    {
        Application app = (Application) getContext().getApplicationContext();
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState)
            {
                if (activity instanceof LifecycleOwner) injectBus((LifecycleOwner) activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity)
            {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity)
            {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity)
            {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity)
            {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState)
            {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity)
            {

            }
        });
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder)
    {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values)
    {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        return 0;
    }

    /**
     * 初始化，注册到事件总线
     * @param lifecycleOwner
     */
    private void injectBus(LifecycleOwner lifecycleOwner){
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