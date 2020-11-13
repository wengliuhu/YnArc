package com.yanantec.ynbus.message;


public class LegoMessageManager {

    private static final Object nLock = new Object();
    private static LegoMessageManager nInstance;

    /**
     * 在使用本地消息的时候需要初始化实例
     *
     * @return
     */
    private static LegoMessageManager initInstance() {
        synchronized (nLock) {
            if (nInstance == null) {
                nInstance = new LegoMessageManager();
            }
            return nInstance;
        }
    }

    public static LegoMessageManager getInstance() {
        if (nInstance == null) {
            return initInstance();
        }
        return nInstance;
    }


    private LegoMessageManager() {

    }

    /**
     * 发送无数据消息
     *
     * @param action 消息来源
     * @return
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
        LegoEventBus.use(action, Object.class).postValue(data);
    }
}
