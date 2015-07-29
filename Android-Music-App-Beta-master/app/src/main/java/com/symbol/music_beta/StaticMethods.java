package com.symbol.music_beta;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;

public class StaticMethods {

    public static void write (String filename, String data, Context c) throws IOException{
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(c.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
        }
    }

    public static String readFirstLine (String filename,Context c) throws IOException{
        String ret = "0";
        try {
            InputStream inputStream = c.openFileInput(filename);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(inputStreamReader);
                ret = br.readLine();
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return ret;
    }

    //get current number of Milliseconds in second
    public static int getMillisecond(){
        Calendar calendar = Calendar.getInstance();
        int millisecond = calendar.get(Calendar.MILLISECOND);
        return millisecond;
    }
    //take difference between 2 times
    public static int calculateDifferenceInMilliseconds(int previous, int current){
        int difference = 0;
        if(current > previous){
            difference = (current - previous);
        }else{
            difference = ((1000 - previous)+current);
        }
        return difference;
    }

    public static ArrayList<String> getSongPath(Context c) {
        ArrayList<String> songPaths = new ArrayList<String>();
        Uri exContent = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = new String[]{
                MediaStore.Audio.Media.DATA
        };
        Cursor cursor = c.getContentResolver().query(exContent, projection, null, null, MediaStore.Audio.Media.DISPLAY_NAME + " DESC");//table - columns - etc...
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            songPaths.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return songPaths;
    }

    public static String getPathFromMediaUri(Context context,Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Audio.Media.DATA};//
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static ArrayList<String> readFile(String filename, Context c){
        ArrayList<String> ret = new ArrayList<String>();
        String currentLine = "";
        try {
            InputStream inputStream = c.openFileInput(filename);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(inputStreamReader);
                while((currentLine = br.readLine()) != null){
                    ret.add(currentLine);
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return ret;
    }

    public static String getTitleFromUriString(String uri){
        String[] titleParts = uri.split("/");
        String[] title = titleParts[titleParts.length-1].split("\\.");
        return title[title.length-2];
    }

    public static String getArtistFromUriString(String uri){
        String[] titleParts = uri.split("/");
        String artist = titleParts[titleParts.length-3];
        return artist;
    }
}
