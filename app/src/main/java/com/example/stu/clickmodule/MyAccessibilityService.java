package com.example.stu.clickmodule;

import android.accessibilityservice.AccessibilityService;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stu on 2019-06-21.
 * <p>
 * Copyright www.putaoabc.com
 * <p>
 * <p>
 * 丁丁上下班页面 使用5个ImageView（不是下班按钮） 3个textview（标题） 19个view  5个button(也不是)
 *
 *
 * 经验总结
 * 不能根据text文字来判断按钮，因为很多高级app都使用view封装，因此rootInfo.getText()返回null，不能获取到text
 * 其他方法的错误或者获取不到有可能导致之后循环的断掉，这也是为什么之前一直卡在这里
 *
 *
 *
 *
 */
public class MyAccessibilityService extends AccessibilityService {

    public static MyAccessibilityService mService;

//    private boolean canClick = true;
//
//    private int countView = 0;
//
//    private List<AccessibilityNodeInfo> viewList = new ArrayList<>();//该页面所有view对应的nodeInfo对象的集合
//
//    private boolean canListAddFlag = true; //因为getChild会重走，所以以textview="北京葡萄智学科技有限公司'为标识
//
//    private int name = 0;//计算title出现的次数


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mService = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.e("stu", "access start");
        try {
            //拿到根节点
            AccessibilityNodeInfo rootInfo = getRootInActiveWindow();
            getChild(rootInfo);
        } catch (Exception e) {
        }
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this, "interrupt!", Toast.LENGTH_LONG).show();
    }

    private void performClick(AccessibilityNodeInfo targetInfo) {
        MainActivity.canClick = false;
        targetInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        MainActivity.countView = 0;
        Log.e("stu_s", String.valueOf(MainActivity.viewList.size()));
        MainActivity.viewList.clear();
        MainActivity.canListAddFlag = true;
        MainActivity.name = 0;
    }

    private void getChild(AccessibilityNodeInfo rootInfo) {
        if (MainActivity.canClick) {

            if (rootInfo.getChildCount() == 0) {
                Log.e("stu_0", String.valueOf(rootInfo.getClassName()));
//            Log.e("stu_1", rootInfo.getViewIdResourceName());
//            Log.e("stu_2", String.valueOf(rootInfo.getChildCount()));
                Log.e("stu_3", String.valueOf(rootInfo.getText()));
//            Log.e("stu_4", String.valueOf(rootInfo.getChild(0).getText()));//此句不能用。用就崩
//            AccessibilityNodeInfo childInfo = rootInfo.getChild(0);
//            String text = childInfo.getText().toString();

                if (String.valueOf(rootInfo.getText()).equals("北京葡萄智学科技有限公司")) {
                    MainActivity.name++;
                    if (MainActivity.name == 2) {
                        MainActivity.name = 0;
                        MainActivity.canListAddFlag = false;
                    }
                }

                if (String.valueOf(rootInfo.getClassName()).equals("android.view.View")) {
                    MainActivity.countView++;
                    if (MainActivity.canListAddFlag) {
                        MainActivity.viewList.add(rootInfo);
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                                for (int i = 0; i < MainActivity.viewList.size(); i++) {
                                    if (i == MainActivity.viewList.size() - 6) {
                                        performClick(MainActivity.viewList.get(i-1));
                                    }
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
//                    if (countView == 13) {
////                        performClick(rootInfo);
//                        countView = 0;
//                        viewList.clear();
//                    }
                }


            } else {
                for (int i = 0; i < rootInfo.getChildCount(); i++) {
//                Log.e("stu_count", String.valueOf(i));
                    getChild(rootInfo.getChild(i));
                }
            }
        }
    }

}
