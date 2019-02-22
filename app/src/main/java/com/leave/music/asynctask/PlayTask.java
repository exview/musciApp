package com.leave.music.asynctask;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.leave.music.MyApplication;
import com.leave.music.activity.MainActivity;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by leave on 2018/4/13.
 */

public class PlayTask extends AsyncTask<String, Integer, Void> {
    private MediaPlayer mediaPlayer;
    private String playMode;
    public static final String MODE1 = "single";
    public static final String MODE2 = "singleLoop";
    public static final String MODE3 = "listLoop";
    private boolean isPause = true;

    public String getPlayMode() {
        return playMode;
    }

    public void setPlayMode(String playMode) {
        this.playMode = playMode;
    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public PlayTask() {
        this.mediaPlayer = new MediaPlayer();
        WhileFinish();
    }

    public PlayTask(String playMode) {
        this.mediaPlayer = new MediaPlayer();
        this.playMode = playMode;
        WhileFinish();
    }

    @Override
    protected Void doInBackground(String... strings) {
        playMode = MODE3;
        try {
            mediaPlayer.setDataSource(strings[0]);
            mediaPlayer.prepare();
            if (playMode == "singleLoop")
                mediaPlayer.setLooping(true);
            mediaPlayer.start();
            isPause = false;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public void SecondPlay(String path){
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.reset();
            try{
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }catch (IOException e){
                Log.e(TAG, "SecondPlay: " + "初始化失败");
                e.printStackTrace();
            }
        }
    }

    public boolean PlayOrPause(){
        if (mediaPlayer != null){
            if (isPause){
                mediaPlayer.start();
                isPause = false;
            }else {
                mediaPlayer.pause();
                isPause = true;
            }
            return isPause;
        }
        return true;
    }

    public void finish(){
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    public void changePlayMode(String playMode){
        this.playMode = playMode;
    }

    private void WhileFinish(){
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (playMode == "single"){
                    mediaPlayer.setLooping(false);
                }else if (playMode == "singleLoop"){
                    mediaPlayer.start();
                }else if (playMode == "listLoop"){
                }
                Intent intent = new Intent("android.intent.action.addToRecent");
                intent.putExtra("playMode", playMode);
                LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);
            }
        });
    }
}
