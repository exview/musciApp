package com.leave.music.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.leave.music.MyApplication;
import com.leave.music.activity.PlayUIActivity;
import com.leave.music.asynctask.PlayTask;
import com.leave.music.javabean.Song;
import com.leave.music.notification.NotificationManage;

import static android.content.ContentValues.TAG;

public class PlayService extends Service {
    private PlayBinder playBinder = new PlayBinder(new PlayTask());

    public PlayService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        if (playBinder instanceof IBinder)
            return playBinder;
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void stopService(){
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class PlayBinder extends Binder{
        PlayTask playTask;
        private boolean isFirst = true;

        public PlayBinder(PlayTask playTask) {
            this.playTask = playTask;
        }

        public void changePlayMode(String playMode){
            playTask.changePlayMode(playMode);
        }

        //播放音乐
        public void play(String path){
            if (isFirst) {
                playTask.execute(path);
                isFirst = false;
            }
            else {
                playTask.SecondPlay(path);
            }
        }

        public void play(Song song){
            String path = song.getDate();
            if (isFirst) {
                playTask.execute(path);
                isFirst = false;
                //startForeground(1, new NotificationManage().getPlayUInotification(MyApplication.getContext(), song));
                startForeground(1, new NotificationManage().getNotification(MyApplication.getContext(),song,isPause()));
            }
            else {
                playTask.SecondPlay(path);
            }
        }

        //播放过程中暂停或暂停后恢复播放
        public boolean pause(){
            if (playTask == null)
                return true;
            else
                return playTask.PlayOrPause();
        }

        public boolean isPause(){
            return playTask.isPause();
        }

        public void finish(){
            if (playTask != null){
                finish();
            }
            stopForeground(true);
            stopService();
        }
    }
}
