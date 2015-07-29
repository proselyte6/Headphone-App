package com.symbol.music_beta;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;

public class NotificationSkipReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        try{
            StaticMethods.write("musicState.txt","skip song",context);
        }catch(IOException e){}
    }
}
