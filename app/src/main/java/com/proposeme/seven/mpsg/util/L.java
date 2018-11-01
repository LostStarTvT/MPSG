package com.proposeme.seven.mpsg.util;

import android.util.Log;

public class L {

    private static boolean debug = true;
    private static final String TAG = "Imooc_okhttp";
    public static void e(String msg){
        if (debug)
            Log.e(TAG,msg);
    }
}
