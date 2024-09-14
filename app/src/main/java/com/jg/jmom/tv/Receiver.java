package com.gecko.webview.tv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("broadCastReceiver","onReceiver...");

        Intent mBootIntent = new Intent(context, MainActivity.class);
        // 必须设置FLAG_ACTIVITY_NEW_TASK
        mBootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mBootIntent);

    }

}
