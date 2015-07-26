package com.symbol.music_beta;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by MKJ467 on 7/8/2015.
 */
public class ListenForHeadphones extends Service {

    private boolean isRunning = false;
    private MediaPlayer mp = new MediaPlayer();
    private int elapsedTime = 600001;
    private int length = 0;
    private String musicState = "";
    private ArrayList<String> songPaths;
    private int mode = 0;
    private int notificationID = 9999;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        try{
            StaticMethods.write("musicState.txt", "play", getBaseContext());//initialize musicState to play
        }catch(IOException e){}
        songPaths = getSongPath();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(receiver, filter);
        AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        ComponentName eventReceiver = new ComponentName(getPackageName(), HeadphoneButtonListener.class.getName());
        am.registerMediaButtonEventReceiver(eventReceiver);//register media button
        // build the PendingIntent for the remote control client
        //Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        //mediaButtonIntent.setComponent(eventReceiver);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch(state){
                    case 1:
                        Toast.makeText(context, "Headphones have been plugged in", Toast.LENGTH_LONG).show();
                        try{
                            StaticMethods.write("musicState.txt", "play", getBaseContext());//always plays when headphones are plugged in
                        }catch(IOException e){}
                        try{
                            mode = Integer.parseInt(StaticMethods.readFirstLine("options.txt",getBaseContext()));
                        }catch(IOException e){}
                        if(mode != 2){
                            isRunning = true;
                            StartMusicTask m = new StartMusicTask();
                            m.execute();
                        }
                        break;
                    case 0:
                        Toast.makeText(context, "Headphones have been unplugged", Toast.LENGTH_LONG).show();
                        isRunning = false;
                        StopMusicTask sm = new StopMusicTask();
                        sm.execute();
                        break;
                }
            }
        }
    };

    class StartMusicTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void ...params) {
            if(elapsedTime >= 600000){ //10 minutes
                mp.stop();
                mp.release();
                mp = new MediaPlayer();
                playSong();
            }else{
                mp.seekTo(length);
                mp.start();
            }
            elapsedTime = 0;

            //keep playing songs until headphones are unplugged
            while(true){
                length = mp.getCurrentPosition();
                if(!isRunning){
                    mp.pause();
                    length = mp.getCurrentPosition();
                    break;
                }
                try{
                    musicState = StaticMethods.readFirstLine("musicState.txt",getBaseContext());
                }catch(IOException e){}
                if(musicState == null){
                    musicState = "";
                }
                if(musicState.equals("pause") && mp.isPlaying()){
                    mp.pause();
                }
                if(musicState.equals("skip song")){
                    onComplete();
                }
                if(musicState.equals("play") && !mp.isPlaying() && (mp.getDuration() <= length + 500)) {//song finished (within 500 milliseconds)
                    onComplete();
                }
                if (musicState.equals("play") && !mp.isPlaying() && (mp.getDuration() > length)) {//resume from pause
                    mp.seekTo(length);
                    mp.start();
                }
                try{
                    mode = Integer.parseInt(StaticMethods.readFirstLine("options.txt",getBaseContext()));
                }catch(IOException e){}
                if(mode == 2){
                    isRunning = false;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }
    class StopMusicTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void ...params) {
            elapsedTime += 1;//elapsed time is only 0 when set in StartMusicTask
            while(elapsedTime <= 600000){
                if(elapsedTime == 0 || isRunning){
                    break;
                }
                int currentMillisecond = StaticMethods.getMillisecond();
                try{Thread.sleep(500);}catch(InterruptedException e){}
                int previousMillisecond = currentMillisecond;
                currentMillisecond = StaticMethods.getMillisecond();
                elapsedTime += StaticMethods.calculateDifferenceInMilliseconds(previousMillisecond, currentMillisecond);
                System.out.println("ELAPSED TIME: " + elapsedTime);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    public void playSong(){
        int song = 0;
        try{
            mode = Integer.parseInt(StaticMethods.readFirstLine("options.txt",getBaseContext()));
        }catch(IOException e){}
        try{
            if(mode == 0){
                Random rand = new Random();
                song = rand.nextInt(songPaths.size() - 7) + 7;//skip preloaded crap
                mp.setDataSource(songPaths.get(song));
                mp.prepare();
                mp.start();
            }
            if(mode == 1){
                //add condition to check if movement is true or not
                ArrayList<String> playListSongs = new ArrayList<String>();
                playListSongs = StaticMethods.readFile("playlist-stationary.txt",getBaseContext());//only stationary for now
                Random rand = new Random();
                song = rand.nextInt(playListSongs.size());
                mp.setDataSource(playListSongs.get(song));
                mp.prepare();
                mp.start();
            }
        }catch(IOException e){
        }
        //build notification for new song
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setContentTitle("Notification Alert");
        notification.setContentText("Hi, This is Android Notification Detail");
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(this, MainActivity.class);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        notification.setContentIntent(resultPendingIntent);
        //notificationID allows you to update the notification later on.
        mNotificationManager.notify(notificationID, notification.build());
    }
    public void onComplete(){
        mp.stop();
        mp.release();
        mp = new MediaPlayer();
        playSong();
        try{
            StaticMethods.write("musicState.txt", "play", getBaseContext());//used for skip function
        }catch(IOException e){}
    }
    public ArrayList<String> getSongPath() {
        ArrayList<String> songPaths = new ArrayList<String>();
        Uri exContent = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = new String[]{
                MediaStore.Audio.Media.DATA
        };
        Cursor cursor = getContentResolver().query(exContent, projection, null, null, MediaStore.Audio.Media.DISPLAY_NAME + " DESC");//table - columns - etc...
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            songPaths.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return songPaths;
    }

}
