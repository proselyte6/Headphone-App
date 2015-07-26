package com.symbol.music_beta;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by MKJ467 on 7/14/2015.
 */
public class HeadphoneButtonListener extends BroadcastReceiver {

    private String state = "";
    private long DOUBLE_CLICK_DELAY = 450;
    private boolean doubleClick = false;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if ((event.getKeyCode() == 79)&& (event.getAction() == KeyEvent.ACTION_DOWN)) {
                SharedPreferences settings = context.getSharedPreferences("name", 0);
                long last = settings.getLong("last", 0);
                long delta = System.currentTimeMillis() - last;
                if (delta < DOUBLE_CLICK_DELAY) {
                    doubleClick = true;
                    doubleClick(context);
                }
                SharedPreferences.Editor editor = settings.edit();
                editor.putLong("last", System.currentTimeMillis());
                editor.commit();

                //single click will fire alone fine, but will also fire before double click.
                //for the purposes of this application, it doesnt matter, as double click will change song
                //and make sure state == "play"
                if(!doubleClick){
                    singleClick(context);
                }
                doubleClick = false;
            }
        }
        abortBroadcast();
    }

    public void singleClick(Context context){
        try{
            state = StaticMethods.readFirstLine("musicState.txt",context);
        }catch(IOException e){}
        if(state.equals("play")){
            try{
                StaticMethods.write("musicState.txt","pause",context);
                Toast.makeText(context, "pause", Toast.LENGTH_LONG).show();
            }catch(IOException e){}
        }else if(state.equals("pause")){
            try{
                StaticMethods.write("musicState.txt","play",context);
                Toast.makeText(context, "play", Toast.LENGTH_LONG).show();
            }catch(IOException e){}
        }
    }

    public void doubleClick(Context context){
        try{
            StaticMethods.write("musicState.txt","skip song",context);
        }catch(IOException e){}
    }
}
