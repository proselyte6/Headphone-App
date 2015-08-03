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

public class MainActivity extends Activity {

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
}
