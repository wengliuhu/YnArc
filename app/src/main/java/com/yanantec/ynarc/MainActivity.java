package com.yanantec.ynarc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yanantec.ynbus.message.YnMessageManager;
import com.yanantec.ynbus.message.YnArcEventBusListener;
import com.yanantec.annotation.OnMessage;
public class MainActivity extends AppCompatActivity implements YnArcEventBusListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        injectBus(this);
    }

    public void click(View view){
        YnMessageManager.getInstance().sendMessage("12345", "当前时间：" + System.currentTimeMillis());
    }

    @OnMessage(value = "12345")
    public void changestr(String msg){
        Toast.makeText(this, "接受到的消息：" + msg, Toast.LENGTH_LONG).show();
        ((TextView)findViewById(R.id.tv1)).setText(msg);
    }
}
