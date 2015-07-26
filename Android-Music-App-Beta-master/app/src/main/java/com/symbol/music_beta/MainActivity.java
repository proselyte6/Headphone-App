package com.symbol.music_beta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends Activity {

    private RadioGroup options;
    private RadioButton shuffle;
    private RadioButton playlist;
    private RadioButton disable;
    private Button playlistEdit;
    private Button saveSettings;
    private Button pausePlay;
    private Button skip;

    private int optionSelected;
    private String musicState = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        options = (RadioGroup) findViewById(R.id.options);
        playlistEdit = (Button) findViewById(R.id.playlistEdit);
        saveSettings = (Button) findViewById(R.id.saveSettings);
        pausePlay = (Button) findViewById(R.id.pause_play);
        skip = (Button) findViewById(R.id.skip);
        shuffle = (RadioButton) findViewById(R.id.shuffle);
        playlist = (RadioButton) findViewById(R.id.playlist);
        disable = (RadioButton) findViewById(R.id.disable);
        try{
            optionSelected = Integer.parseInt(StaticMethods.readFirstLine("options.txt", getBaseContext()));//might cause first time setup error
        }catch(IOException e){}

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
                Toast.makeText(v.getContext(), "Settings have been saved", Toast.LENGTH_LONG).show();
            }
        });

        playlistEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),PlaylistSelector.class);
                startActivity(i);
            }
        });

        pausePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    musicState = StaticMethods.readFirstLine("musicState.txt",getBaseContext());
                }catch(IOException e){}
                if(musicState != null){
                    try{
                        if(musicState.equals("play")){
                            StaticMethods.write("musicState.txt","pause",getBaseContext());
                            pausePlay.setText("play");
                        }else if(musicState.equals("pause")){
                            StaticMethods.write("musicState.txt","play",getBaseContext());
                            pausePlay.setText("pause");
                        }
                    }catch(IOException e){}
                }
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    StaticMethods.write("musicState.txt","skip song",getBaseContext());
                }catch(IOException e){}
            }
        });
    }

}
