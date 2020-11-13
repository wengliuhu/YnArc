package com.yanantec.ynbus.message.event;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

/**
 * @author : yuanbingbing
 * @since : 2019/3/22 17:11
 *
 * https://github.com/JeremyLiao/LiveEventBus
 */
public class MainThreadManager {

    private static class SingletonHolder {
        private static final MainThreadManager INSTANCE = new MainThreadManager();
    }

    public static MainThreadManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private final Object mLock = new Object();

    private MainThreadManager() {
    }

    @Nullable
    private volatile Handler mMainHandler;

    public void postToMainThread(Runnable runnable) {
        if (mMainHandler == null) {
            synchronized (mLock) {
                if (mMainHandler == null) {
                    mMainHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        //noinspection ConstantConditions
        mMainHandler.post(runnable);
    }

    public boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}