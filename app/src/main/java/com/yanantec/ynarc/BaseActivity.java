package com.yanantec.ynarc;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.yanantec.annotation.MapCreate;
import com.yanantec.ynbus.annotation.OnMessageIncludeSuper;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/1/29
 * Describe
 */
@MapCreate(key = "666", mapFiled = "MyMap")
public class BaseActivity extends AppCompatActivity
{
    @OnMessageIncludeSuper(value = "123456")
    public void printStr(String msg){
        Log.e("wlh", "---------------printStrprintStrprintStrprintStr---" + msg);
    }
}
