package com.jpush;


import android.content.Context;
import android.os.Looper;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class JPushHelper {

    private static Context context;

    public static void init(Context context) {
        JPushHelper.context = context;
        JPushInterface.init(context);
    }

    public static void setAlias(final String alias, final TagAliasCallback tagAliasCallback, final long connectWhenFailureSpace) {
        if (context == null) return;
        TagAliasCallback ta = tagAliasCallback;
        if (ta == null) {
            ta = new TagAliasCallback() {
                @Override
                public void gotResult(int i, String s, Set<String> set) {
                    if (connectWhenFailureSpace <= 0 || i == 0) return;
                    new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(connectWhenFailureSpace);
                                Looper.prepare();
                                setAlias(alias, tagAliasCallback, connectWhenFailureSpace);
                                Looper.loop();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            };
        }
        JPushInterface.setAlias(context, alias, ta);
    }

    public static void stopPush() {
        if (!isPushStopped())
            JPushInterface.stopPush(context);
    }

    public static void resumePush() {
        if (isPushStopped())
            JPushInterface.resumePush(context);
    }

    public static boolean isPushStopped() {
        return JPushInterface.isPushStopped(context);
    }

}
