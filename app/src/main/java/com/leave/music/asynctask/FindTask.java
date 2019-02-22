package com.leave.music.asynctask;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.leave.music.MyApplication;
import com.leave.music.javabean.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by leave on 2018/4/12.
 */

public class FindTask extends AsyncTask<File,Void,Integer> {
    private final int SUCCESS = 0;
    private final int FAILED = 1;

    private int count = 1;

    private int stateResult;
    private List<File> fileList;

    @Override
    protected Integer doInBackground(File... files) {
        findByUri();
        send();
        return SUCCESS;
    }

    private void findMusicInStorage(File path){
        File[] files = path.listFiles();
        for (File file:files){
            if (file.isDirectory())
                findMusicInStorage(file);
            else if (file.getName().endsWith(".mp3") && file.length() > 1000000)
                Log.d(TAG, "findMusicInStorage: " + file.getName());
        }
    }

    private void findByUri(){
        Cursor cursor = MyApplication.getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, null);
        while (cursor.moveToNext()){
            Song song = new Song();
            song.set_id(cursor.getLong(0));
            song.setAlbum_id(cursor.getString(13));
            song.setSongName(cursor.getString(8));
            song.setArtist(cursor.getString(25));
            song.setDate(cursor.getString(1));
            song.setDuration(cursor.getLong(9));
            song.setSize(cursor.getLong(3));
            song.save();
            //Log.d(TAG, "findByUri: " + song);
        }
        cursor.close();
    }

    public void send(){
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(MyApplication.getContext());
        manager.sendBroadcast(new Intent("android.intent.finishFind"));
    }
}
