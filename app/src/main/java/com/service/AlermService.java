package com.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * 创建一个服务，该服务主要用来接收广播和创建定时器
 * @author 林培东
 */
public class AlermService extends Service {
    private static final int NOTIFY_ID = 1234;//通知的唯一标识符
    int count=0;
    boolean isOn=true;
    //主要功能，广播接收器
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences sp = getSharedPreferences("actm", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                count=0;
                isOn=true;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                count=0;
                isOn=false;
            }else if(intent.getAction().equals(Intent.ACTION_TIME_TICK)) {//监听Intent.ACTION_TIME_TICK这个广播，因为它一分钟就发送一次
                if(!isOn)
                    return;
                int limit=(int) sp.getLong("limit", 0L);
                if(limit==0)
                    return;
                Log.d("1111","limit="+limit);
                count++;
                if (limit <= count) {
                    count=0;
                    /*final NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);//获取通知管理器
                    Notification notification = new Notification(R.drawable.ic_launcher, "用眼提醒", System.currentTimeMillis());//通知的时机
                    notification.flags = Notification.FLAG_AUTO_CANCEL;//点击一次通知就自动消失
                    PendingIntent pIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);//跳转到主界面
                    notification.tickerText= "使用时长已超过预定时限...";//通知栏显示内容
                    manager.notify(NOTIFY_ID, notification);//执行
                    */
                    Toast.makeText(
                            getApplicationContext(),
                                   "使用时长已超过预定时限...", Toast.LENGTH_LONG).show();
                }
            }
        }

    };

    @Override
    public void onCreate() {
        //添加过滤器并注册
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, filter);
        Toast.makeText(getApplicationContext(),
                "用眼时长提醒已启动", Toast.LENGTH_SHORT).show();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        Toast.makeText(getApplicationContext(),
                "用眼时长提醒已关闭", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}

