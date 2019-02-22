package com.leave.music.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.leave.music.MyApplication;
import com.leave.music.R;
import com.leave.music.asynctask.FindTask;
import com.leave.music.javabean.Song;
import com.leave.music.service.FindService;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by leave on 2018/4/13.
 */

public class SongUtils {
    private final int SONGNAME = 7;
    private final int DATA = 1;

    public List<Song> getSongList(){
        List<Song> songList = DataSupport.findAll(Song.class);
        if (songList.size() < 0) {
            FindTask task = new FindTask();
            task.execute(new File(""));
        }
        return DataSupport.findAll(Song.class);
    }

    public String getData(String songName){
        Cursor cursor = DataSupport.findBySQL("select * from Song where songName='" + songName + "'");
        String data = "";
        if (cursor != null) {
            data = cursor.getString(DATA);
        }else {
            Toast.makeText(MyApplication.getContext(),"文件不存在", Toast.LENGTH_SHORT).show();
        }
        return data;
    }

    private synchronized String getAlbumArtPicPath(Context context, String albumId) {

        String[] projection = {MediaStore.Audio.Albums.ALBUM_ART};
        String imagePath = null;
        Uri uri = Uri.parse("content://media" + MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI.getPath() + "/" + albumId);

        Cursor cur = context.getContentResolver().query(uri, projection, null, null, null);
        if (cur == null) {
            return null;
        }

        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            imagePath = cur.getString(0);
        }
        cur.close();


        return imagePath;
    }

    public Bitmap getAlbumArtPic(Context context, String albumId){
        String path = getAlbumArtPicPath(context, albumId);
        if (path == null)
            return null;
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(path));
            return bitmap;
        } catch (FileNotFoundException e) {
            Log.d(TAG, "getAlbumArtPic: " + path);
            e.printStackTrace();
        }
        return null;
    }
}
