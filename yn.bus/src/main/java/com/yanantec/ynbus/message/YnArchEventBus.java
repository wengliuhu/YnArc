package com.yanantec.ynbus.message;

//import android.arch.lifecycle.LifecycleOwner;
//import android.arch.lifecycle.Observer;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.yanantec.ynbus.message.event.LiveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2020/11/16
 * Describe:
 *
 */
public class YnArchEventBus
{
    private final Map<String, BusLiveEvent<Object>> bus;

    private YnArchEventBus() {
        bus = new HashMap<>();
    }

    private static class SingletonHolder {
        private static final YnArchEventBus DEFAULT_BUS = new YnArchEventBus();
    }


    /**
     * 获取使用LiveData消息实例
     *
     * @param key  消息通道标识
     * @param type 消息类型Class
     * @param <T>  消息类型
     * @return MutableLiveData
     */
    public static <T> Observable<T> use(String key, Class<T> type) {
        return get().with(key, type);
    }


    /**
     * 获取使用LiveData消息实例
     *
     * @param key 消息通道标识
     * @return MutableLiveData
     */
    public static Observable<Object> use(String key) {
        return use(key, Object.class);
    }

    /**
     * 获取使用LiveData消息实例
     *
     * @param type 消息类型Class
     * @param <T>  消息类型
     * @return MutableLiveData
     */
    public static <T> Observable<T> use(Class<T> type) {
        return use(type.getCanonicalName(), type);
    }

    /**
     * 获取Lego消息总线 实例对象
     *
     * @return LegoEventBus
     */
    public static YnArchEventBus get() {
        return SingletonHolder.DEFAULT_BUS;
    }

    /**
     * 获取LiveData对象实例
     *
     * @param key  消息通道标识
     * @param type 消息类型Class
     * @param <T>  消息类型
     * @return MutableLiveData
     */
    public synchronized <T> Observable<T> with(String key, Class<T> type) {
        if (!bus.containsKey(key)) {
            bus.put(key, new BusLiveEvent<>(key));
        }
        return (Observable<T>) bus.get(key);
    }

    /**
     * 获取LiveData对象实例(Object类型)
     *
     * @param key 消息通道标识
     * @return MutableLiveData
     */
    public Observable<Object> with(String key) {
        return with(key, Object.class);
    }


    /**
     * 获取LiveData对象实例(默认以ClassName作为消息通道标识)
     *
     * @param type 消息类型Class
     * @param <T>  消息类型
     * @return MutableLiveData
     */
    public <T> Observable<T> with(Class<T> type) {
        return with(type.getCanonicalName(), type);
    }

//
//    private boolean lifecycleObserverAlwaysActive = true;
//
//
//    public void lifecycleObserverAlwaysActive(boolean active) {
//        lifecycleObserverAlwaysActive = active;
//    }

    public interface Observable<T> {
        void setValue(T value);

        void postValue(T value);

        void postValueDelay(T value, long delay);

        void postValueDelay(T value, long delay, TimeUnit unit);

        /**
         * 回调与生命周期相关，在LifecycleOwner处于活动状态才会回调onchange，
         * @param owner
         * @param observer
         */
        void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer);

        /**
         * 建立监听
         * @param owner
         * @param observer
         * @param always false-在LifecycleOwner处于活动状态才会回调onchange，true-一直回调
         */
        void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer, boolean always) ;

        /**
         * 建立监听
         * @param owner
         * @param observer
         * @param always false-在LifecycleOwner处于活动状态才会回调onchange，true-一直回调
         * @param discard discard=true 同时 always=false 在LifecycleOwner处于非活动状态下，发送消息不接收
         */
        void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer, boolean always, boolean discard) ;

        /**
         * 与生命周期无关，粘性回调
         * @param observer
         */
        void observeForever(@NonNull Observer<T> observer);

        /**
         * 粘性监听
         * @param owner
         * @param observer
         * @param always
         * @param discard
         */
        void observeSticky(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer, boolean always, boolean discard) ;

        /**
         * 粘性监听
         * @param observer
         */
        void observeStickyForever(@NonNull Observer<T> observer) ;
        /**
         * 移除observer
         * @param observer
         */
        void removeObserver(@NonNull Observer<T> observer);
    }

    private class BusLiveEvent<T> extends LiveEvent<T> implements Observable<T> {

        private class PostValueTask implements Runnable
        {
            private Object newValue;

            public PostValueTask(@NonNull Object newValue) {
                this.newValue = newValue;
            }

            @Override
            public void run() {
                setValue((T) newValue);
            }
        }

        @NonNull
        private final String key;
        private Handler mainHandler = new Handler(Looper.getMainLooper());

        private BusLiveEvent(String key) {
            this.key = key;
        }

//        @Override
//        protected Lifecycle.State observerActiveLevel() {
//            return lifecycleObserverAlwaysActive ? Lifecycle.State.CREATED : Lifecycle.State.STARTED;
//        }

        @Override
        public void postValueDelay(T value, long delay) {
            mainHandler.postDelayed(new PostValueTask(value), delay);
        }

        @Override
        public void postValueDelay(T value, long delay, TimeUnit unit) {
            postValueDelay(value, TimeUnit.MILLISECONDS.convert(delay, unit));
        }

        @Override
        public void removeObserver(@NonNull Observer<T> observer) {
            super.removeObserver(observer);
            if (!hasObservers()) {
                YnArchEventBus.get().bus.remove(key);
            }
        }
    }

}
