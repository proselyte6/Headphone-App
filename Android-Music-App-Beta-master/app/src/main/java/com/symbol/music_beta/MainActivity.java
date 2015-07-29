package com.symbol.music_beta;

import android.app.Activity;
import android.content.Intent;
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
                        StaticMethods.write("speaker-status", "yes", getBaseContext());
                    }catch(IOException e){}
                }else{
                    Toast.makeText(getBaseContext(), "speakers unchecked", Toast.LENGTH_LONG).show();
                    try {
                        StaticMethods.write("speaker-status", "no", getBaseContext());
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

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),Help.class);
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

    }
}
