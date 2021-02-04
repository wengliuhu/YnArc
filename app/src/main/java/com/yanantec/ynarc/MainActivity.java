package com.yanantec.ynarc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yanantec.annotation.MapCreate;
import com.yanantec.annotation.OnMessage;
import com.yanantec.ynbus.message.YnArcEventBusListener;
import com.yanantec.ynbus.message.YnMessageManager;

@MapCreate(key = "22")
public class MainActivity extends BaseActivity  implements YnArcEventBusListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        injectBus(this);
    }

    @Override
    public void printStr(String msg)
    {
        Log.e("wlh", "---------MainActivityMainActivity------printStrprintStrprintStrprintStr---" + msg);

    }

    public void click(View view){
        YnMessageManager.getInstance().sendMessage("12345", "当前时间：" + System.currentTimeMillis());
        YnMessageManager.getInstance().sendMessage("123456", "当前时间：" + System.currentTimeMillis());
    }

    @OnMessage(value = "12345")
    public void changestr(Object msg){
        Toast.makeText(this, "接受到的消息：" + msg, Toast.LENGTH_LONG).show();
        ((TextView)findViewById(R.id.tv1)).setText(String.valueOf(msg));
    }




}
