package com.symbol.music_beta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class PlaylistSelector extends Activity {

    private Button playList1;
    private Button playList2;
    private Button playList3;
    private Button set1;
    private Button set2;
    private Button set3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);

        playList1 = (Button) findViewById(R.id.playList_1);
        playList2 = (Button) findViewById(R.id.playList_2);
        playList3 = (Button) findViewById(R.id.playList_3);
        set1 = (Button) findViewById(R.id.set1);
        set2 = (Button) findViewById(R.id.set2);
        set3 = (Button) findViewById(R.id.set3);

        playList1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), PlaylistView.class);
                i.putExtra("playlist-index", 1);
                startActivity(i);
            }
        });

        playList2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), PlaylistView.class);
                i.putExtra("playlist-index", 2);
                startActivity(i);
            }
        });

        playList3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), PlaylistView.class);
                i.putExtra("playlist-index", 3);
                startActivity(i);
            }
        });

        set1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    StaticMethods.write("playlist-choice.txt", "1", getBaseContext());
                } catch (IOException e) {}
                Toast.makeText(getBaseContext(), "Playlist 1 has been set", Toast.LENGTH_LONG).show();
            }
        });

        set2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    StaticMethods.write("playlist-choice.txt", "2", getBaseContext());
                } catch (IOException e) {}
                Toast.makeText(getBaseContext(), "Playlist 2 has been set", Toast.LENGTH_LONG).show();
            }
        });

        set3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    StaticMethods.write("playlist-choice.txt", "3", getBaseContext());
                } catch (IOException e) {}
                Toast.makeText(getBaseContext(), "Playlist 3 has been set", Toast.LENGTH_LONG).show();
            }
        });
    }
}
