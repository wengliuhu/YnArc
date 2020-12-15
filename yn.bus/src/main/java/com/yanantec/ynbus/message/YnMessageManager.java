package com.yanantec.ynbus.message;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2020/11/16
 * Describe:事件总线管理类
 *
 */
public class YnMessageManager
{
    private static final Object nLock = new Object();
    private static YnMessageManager nInstance;

    /**
     * 在使用本地消息的时候需要初始化实例
     *
     * @return
     */
    private static YnMessageManager initInstance() {
        synchronized (nLock) {
            if (nInstance == null) {
                nInstance = new YnMessageManager();
            }
            return nInstance;
        }
    }

    public static YnMessageManager getInstance() {
        if (nInstance == null) {
            return initInstance();
        }
        return nInstance;
    }


    private YnMessageManager() {

    }

    /**
     * 发送无数据消息
     *
     * @param action 消息来源
     */
    public void sendEmptyMessage(String action) {
        sendMessage(action, null);
    }

    /**
     * 发送有数据消息
     *
     * @param action
     * @param data
     */
    public <T> void sendMessage(String action, T data) {
        YnArchEventBus.use(action, Object.class).postValue(data);
    }
}
