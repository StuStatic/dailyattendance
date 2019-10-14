package com.example.stu.clickmodule;

import android.app.Instrumentation;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.service.PushService;

import static android.content.Context.KEYGUARD_SERVICE;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JIGUANG-Example";

    @Override
    public void onReceive(Context context, Intent intent) {
        //启动极光推送的服务，保app不被杀死
        Intent pushintent = new Intent(context, PushService.class);
        context.startService(pushintent);
        try {
            Bundle bundle = intent.getExtras();
            Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle, context));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                processCustomMessage(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
                //打开自定义的Activity
                Intent i = new Intent(context, Main2Activity.class);
                i.putExtras(bundle);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {

        }

    }

    // 打印所有的 intent extra 数据
    private String printBundle(Bundle bundle, Context context) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
                if (bundle.get(key).equals("home")) {
                    //do sth
                    doSthInteresting(context, "home", false);
                } else if (bundle.get(key).equals("start")) {
                    doSthInteresting(context, "start", false);
                } else if (bundle.get(key).equals("startandstop")) {
                    doSthInteresting(context, "startandstop", true);
                } else if (bundle.get(key).equals("test")) {
                    doSthInteresting(context, "test", false);
                } else if (bundle.get(key).equals("lock")) {
                    doSthInteresting(context, "lock", false);
                } else if (bundle.get(key).equals("tap")) {
                    doSthInteresting(context, "tap", false);
                }else if(bundle.get(key).equals("dingding")){
                    doSthInteresting(context,"dingding",false);
                }
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {

    }

    /**
     * 模拟点击
     * adb shell input tap X Y
     * 模拟滑动
     * adb shell input swipe X1 Y1 X2 Y2
     *
     * @param context
     */
    private void doSthInteresting(Context context, String type, boolean finish) {
        if (type.equals("home")) {
            //模拟点击home键
            Intent intent_home = new Intent();
            intent_home.setAction(Intent.ACTION_MAIN);
            intent_home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果是服务里调用，必须加入new task标识
            intent_home.addCategory(Intent.CATEGORY_HOME);
            context.startActivity(intent_home);
        } else if (type.equals("startandstop")) {
            //打开clickmodule app
            PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage("com.example.stu.clickmodule");
            if (finish) {
                intent.putExtra("finish", true);
            }
            context.startActivity(intent);

        } else if (type.equals("start")) {
            //打开clickmodule app
            PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage("com.example.stu.clickmodule");
            if (finish) {
                intent.putExtra("finish", false);
            }
            context.startActivity(intent);
        } else if (type.equals("test")) {
            Log.e("stu", "else");
            List<String> order = new ArrayList<>();
            order.add("input");
            order.add("tap");
            order.add("" + 40);
            order.add("" + 40);
            try {
                new ProcessBuilder(order).start();
            } catch (IOException e) {
                Log.e("stu", "error");
                e.printStackTrace();
            }
        } else if (type.equals("lock")) {
            KeyguardManager keyguardManager = (KeyguardManager) context
                    .getSystemService(KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("unLock");
            // 屏幕锁定
            keyguardLock.reenableKeyguard();
        } else if (type.equals("tap")) {
            Log.e("stu","tap");
//            List<String> order = new ArrayList<>();
//            order.add("input");
//            order.add("tap");
//            //109-299
//            order.add("" + 540);
//            order.add("" + 1000);
//            try {
//                new ProcessBuilder(order).start();
//            } catch (IOException e) {
//                Log.e("stu", "error");
//                e.printStackTrace();
//            }


//            try {
//                OutputStream os = Runtime.getRuntime().exec("su").getOutputStream();
//                os.write("input tap 540 1000".getBytes());
//                os.flush();
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e("GK", e.getMessage());
//            }


//            new Thread() {     //不可在主线程中调用
//                public void run() {
//                    try {
//                        Instrumentation inst = new Instrumentation();
//                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }.start();

//            Instrumentation inst = new Instrumentation();
//            inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
//                    MotionEvent.ACTION_DOWN, 540, 1000, 0));
//            inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
//                    MotionEvent.ACTION_UP, 540, 1000, 0));


        }else if(type.equals("dingding")){
            Log.e("stu","dingding");
            //打开dingding app
            PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage("com.alibaba.android.rimet");
            context.startActivity(intent);
        }

    }
}
