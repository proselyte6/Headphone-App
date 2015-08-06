package com.symbol.music_beta;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.io.IOException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

public class MainActivity extends Activity implements SensorEventListener{

    private RadioGroup options;
    private RadioButton shuffle;
    private RadioButton playlist;
    private RadioButton disable;
    private Button playlistEdit;
    private Button saveSettings;
    private Button help;
    private Button shuffleSongs;
    private Button playSong;
    private Button playPause;
    private Button skip;
    private CheckBox speakers;
    private SensorManager mSensorManager;
    private Sensor mAcc;

    private int optionSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        options = (RadioGroup) findViewById(R.id.options);
        playlistEdit = (Button) findViewById(R.id.playlistEdit);
        saveSettings = (Button) findViewById(R.id.saveSettings);
        help = (Button) findViewById(R.id.help);
        shuffleSongs = (Button) findViewById(R.id.shuffleSongs);
        playSong = (Button) findViewById(R.id.playSong);
        playPause = (Button) findViewById(R.id.playPause);
        skip = (Button) findViewById(R.id.skip);
        shuffle = (RadioButton) findViewById(R.id.shuffle);
        playlist = (RadioButton) findViewById(R.id.playlist);
        disable = (RadioButton) findViewById(R.id.disable);
        speakers = (CheckBox) findViewById(R.id.checkBox);

        try{
            optionSelected = Integer.parseInt(StaticMethods.readFirstLine("options.txt", getBaseContext()));//might cause first time setup error
        }catch(IOException e){}

        speakers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(speakers.isChecked()){
                    Toast.makeText(getBaseContext(), "speakers checked", Toast.LENGTH_LONG).show();
                    try {
                        StaticMethods.write("speaker-status.txt", "yes", getBaseContext());
                    }catch(IOException e){}
                }else{
                    Toast.makeText(getBaseContext(), "speakers unchecked", Toast.LENGTH_LONG).show();
                    try {
                        StaticMethods.write("speaker-status.txt", "no", getBaseContext());
                    }catch(IOException e){}
                }
            }
        });

        switch(optionSelected){
            case 0:
                shuffle.setChecked(true);
                break;
            case 1:
                playlist.setChecked(true);
                break;
            case 2:
                disable.setChecked(true);
                break;
        }

        options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.shuffle:
                        optionSelected = 0;
                        break;
                    case R.id.playlist:
                        optionSelected = 1;
                        break;
                    case R.id.disable:
                        optionSelected = 2;
                        break;
                }
            }
        });

        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    StaticMethods.write("options.txt", Integer.toString(optionSelected), getBaseContext());
                } catch (IOException e) {
                }
                if (optionSelected == 2) {
                    Toast.makeText(getBaseContext(), "AutoBeats has been disabled", Toast.LENGTH_LONG).show();
                    Toast.makeText(getBaseContext(), "Change settings and replug to enable", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(v.getContext(), "Settings have been saved", Toast.LENGTH_LONG).show();
                }
            }
        });

        playlistEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), PlaylistSelector.class);
                startActivity(i);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Help.class);
                startActivity(i);
            }
        });

        shuffleSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),ShuffleSongsView.class);
                startActivity(i);
            }
        });

        playSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
            }
        });

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String musicState = "";
                try{
                    musicState = StaticMethods.readFirstLine("musicState.txt", getBaseContext());
                }catch(IOException e){}
                if(musicState.equals("play")){
                    try{
                        StaticMethods.write("musicState.txt", "pause", getBaseContext());
                    }catch(IOException e){}
                }else{
                    try{
                        StaticMethods.write("musicState.txt", "play", getBaseContext());
                    }catch(IOException e){}
                }
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    StaticMethods.write("musicState.txt", "skip song", getBaseContext());
                }catch(IOException e){}
            }
        });

    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        /** Using this means that in the android manifest file you must put
         * <uses-feature android:name="android.hardware.sensor.accelerometer"
         android:required="true" />
         Otherwise  add this:

         if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
         mAcc = mSensorManager.getDefaultSensor(Sensos.TYPE_ACCELEROMETER);
         mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);
         }
         else {
         // not sure what to put if there is no accelereomter
         }
         */
        mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri path =  data.getData();
            String realPath = StaticMethods.getPathFromMediaUri(getBaseContext(), path);
            String title = StaticMethods.getTitleFromUriString(realPath);
            try {
                StaticMethods.write("nextSong.txt", realPath, getBaseContext());
            } catch (IOException e) {}
            Toast.makeText(getBaseContext(), title+" is next", Toast.LENGTH_LONG).show();
        }else{
            try {
                StaticMethods.write("nextSong.txt", "none", getBaseContext());
            } catch (IOException e) {}
        }
    }
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // Many sensors return 3 values, one for each axis.
        // Do something with this sensor value.
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        final float alpha = 0.8;

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {S
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
