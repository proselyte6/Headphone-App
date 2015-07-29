package com.symbol.music_beta;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ShuffleSongsView extends Activity {

    private ArrayAdapter<String> listAdapter;
    private ListView mainListView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_layout);
        mainListView = (ListView) findViewById( R.id.mainListView );

        listAdapter = new ArrayAdapter<String>(getBaseContext(),R.layout.example_row);
        ArrayList<String> songs = StaticMethods.getSongPath(getBaseContext());
        for(String s: songs){
            listAdapter.add(s);
        }
        mainListView.setAdapter( listAdapter );
    }
}
