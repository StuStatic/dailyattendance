package com.example.stu.clickmodule;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by stu on 2019/06/13
 * 1.模拟屏幕点击
 * 2.自动解锁
 * 3.极光推送
 * 4.模拟点击home键，确保当前在目标app页面
 * 5.自动锁屏
 * 6.进程不死（本project选择第二种）1.manifest:persisten=true 2.onReceive中开启Intent pushintent=new Intent(context,PushService.class);context.startService(pushintent);
 */

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private static Context context;
    private static final String tag = "bright:";
    //accessbilityservice全局
    public static boolean canClick = true;

    public static int countView = 0;

    public static List<AccessibilityNodeInfo> viewList = new ArrayList<>();//该页面所有view对应的nodeInfo对象的集合

    public static boolean canListAddFlag = true; //因为getChild会重走，所以以textview="北京葡萄智学科技有限公司'为标识

    public static int name = 0;//计算title出现的次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        btn = (Button) findViewById(R.id.btn);
        context = getApplicationContext();
        //init jpush
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                //execute the task
                wakeUpAndUnlock();
            }
        }, 1000);

        //是否需要关闭界面
        if (getIntent().getBooleanExtra("finish", false)) {
            finishActivity();
        }
    }

    public void finishActivity() {
        new Handler().postDelayed(new Runnable(){
            public void run() {
                //execute the task
                finish();
            }
        }, 5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //模拟点击事件
//        List<String> order = new ArrayList<>();
//        order.add("input");
//        order.add("tap");
//        //100*100 109-299
//        //50*50
//        order.add("" + 110);
//        order.add("" + 110);
//        try {
//            new ProcessBuilder(order).start();
//        } catch (IOException e) {
//            Log.e("stu", "error");
//            e.printStackTrace();
//        }
    }

    public void click(View view) {
        Toast.makeText(context, "点击啦！", Toast.LENGTH_LONG).show();
        Log.e("点击了", "点击了");
    }

    public void wakeUpAndUnlock() {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, tag);
            wakeLock.acquire(10000); // 点亮屏幕
            wakeLock.release(); // 释放
        }
        // 屏幕解锁
        KeyguardManager keyguardManager = (KeyguardManager) context
                .getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("unLock");
        // 屏幕锁定
//        keyguardLock.reenableKeyguard();
        keyguardLock.disableKeyguard(); // 解锁

        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
    }
}
