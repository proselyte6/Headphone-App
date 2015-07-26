package com.symbol.music_beta;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;


/**
 * Created by MKJ467 on 7/13/2015.
 */
public class PlaylistSelector extends Activity {

    private Button stationary;
    private Button movement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);

        stationary = (Button) findViewById(R.id.stationary);
        movement = (Button) findViewById(R.id.movement);

        stationary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),PlaylistView.class);
                startActivity(i);
            }
        });

        movement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),MovementPlaylistView.class);
                startActivity(i);
            }
        });
    }
}
