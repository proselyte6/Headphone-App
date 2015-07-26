package com.symbol.music_beta;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mkj467 on 7/24/2015.
 */
public class MovementPlaylistView extends Activity {
    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;//displays song titles
    private StringBuilder sb = new StringBuilder();//used to write paths to file
    private ArrayList<String> movementPlaylistPaths = new ArrayList<String>();//holds paths of all songs in playlist

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_layout);

        mainListView = (ListView) findViewById( R.id.mainListView );

        movementPlaylistPaths = StaticMethods.readFile("playlist-movement.txt",getBaseContext());

        for(String s: movementPlaylistPaths){
            sb.append(s + "\n");
        }

        listAdapter = new ArrayAdapter<String>(this, R.layout.example_row);
        listAdapter.add("Tap to add more");
        if(movementPlaylistPaths.size() > 0){
            for(String s: movementPlaylistPaths){
                String temp = StaticMethods.getTitleFromUriString(s);
                listAdapter.add(temp);
            }
        }
        mainListView.setAdapter( listAdapter );

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Tap to add more")){
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i,1);
                }else{
                    Toast.makeText(getBaseContext(), parent.getItemAtPosition(position).toString() + " has been deleted.", Toast.LENGTH_LONG).show();
                    deleteSongFromPlaylist(position - 1, parent.getItemAtPosition(position).toString());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri path =  data.getData();
            String realPath = StaticMethods.getPathFromMediaUri(getBaseContext(),path);
            sb.append(realPath + "\n");
            try{
                StaticMethods.write("playlist-movement.txt",sb.toString(),getBaseContext());
            }catch(IOException e){}
            String title = StaticMethods.getTitleFromUriString(realPath);
            listAdapter.add(title);
        }else{
            //user cancelled
        }
    }

    private void deleteSongFromPlaylist(int index, String title){
        movementPlaylistPaths = StaticMethods.readFile("playlist-movement.txt",getBaseContext());
        movementPlaylistPaths.remove(index);
        sb = new StringBuilder();
        for(String s: movementPlaylistPaths){
            sb.append(s + "\n");
        }
        try{
            StaticMethods.write("playlist-movement.txt",sb.toString(),getBaseContext());
        }catch(IOException e){}
        listAdapter.remove(title);
    }
}
