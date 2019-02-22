package com.leave.music.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.leave.music.MyApplication;
import com.leave.music.R;
import com.leave.music.asynctask.PlayTask;
import com.leave.music.javabean.Song;
import com.leave.music.notification.NotificationManage;
import com.leave.music.utils.SongUtils;

public class PlayUIActivity extends BaseActivity implements View.OnClickListener{
    private ImageButton control_previous;
    private TextView control_mode;
    private ImageButton control_more;
    private int modeCount = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_ui);

        Intent intent = getIntent();
        Song song = intent.getParcelableExtra("Song");
        if (song != null) {
            initView(song);
        }else{
            initView();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playBinder.isPause())
            control_playOrPause.setImageResource(android.R.drawable.ic_media_play);
        else
            control_playOrPause.setImageResource(android.R.drawable.ic_media_pause);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    private void initView(Song song){
        control_imageView = findViewById(R.id.UI_pic);
        control_mode = findViewById(R.id.UI_mode);
        control_more = findViewById(R.id.UI_more);
        control_previous = findViewById(R.id.UI_previous);
        control_playOrPause = findViewById(R.id.UI_play);
        control_next = findViewById(R.id.UI_next);
        control_mode.setOnClickListener(this);
        control_more.setOnClickListener(this);
        control_previous.setOnClickListener(this);
        control_playOrPause.setOnClickListener(this);
        control_next.setOnClickListener(this);
        changePicAndTitle(song.getAlbum_id(), song.getSongName());
    }

    private void initView(){
        control_imageView = findViewById(R.id.UI_pic);
        control_mode = findViewById(R.id.UI_mode);
        control_more = findViewById(R.id.UI_more);
        control_previous = findViewById(R.id.UI_previous);
        control_playOrPause = findViewById(R.id.UI_play);
        control_next = findViewById(R.id.UI_next);
        control_mode.setOnClickListener(this);
        control_more.setOnClickListener(this);
        control_previous.setOnClickListener(this);
        control_playOrPause.setOnClickListener(this);
        control_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.UI_mode:
                modeCount++;
                if (modeCount%3 == 1){
                    playBinder.changePlayMode(PlayTask.MODE1);
                    control_mode.setText("单曲");
                }else if (modeCount%3 == 2) {
                    playBinder.changePlayMode(PlayTask.MODE2);
                    control_mode.setText("单曲循环");
                }
                else if (modeCount%3 == 0) {
                    playBinder.changePlayMode(PlayTask.MODE3);
                    control_mode.setText("列表循环");
                }
                break;
            case R.id.UI_previous:
                if (size == -1)
                    return;
                if (size == 0)
                    size = musics.size();
                size--;
                playNext(musics.get(size));
                notificationManager.notify(1,new NotificationManage().getNotification(MyApplication.getContext(),musics.get(size), playBinder.isPause()));
                break;
            case R.id.UI_play:
                if (size == -1)
                    return;
                if (playBinder.pause()){
                    changePlayOrPauseImage(android.R.drawable.ic_media_play);
                }else {
                    changePlayOrPauseImage(android.R.drawable.ic_media_pause);
                }
                notificationManager.notify(1,new NotificationManage().getNotification(MyApplication.getContext(),musics.get(size), playBinder.isPause()));
                break;
            case R.id.UI_next:
                if (size == -1)
                    return;
                if (size == musics.size()-1)
                    size = -1;
                size++;
                playNext(musics.get(size));
                notificationManager.notify(1,new NotificationManage().getNotification(MyApplication.getContext(),musics.get(size), playBinder.isPause()));
                break;
            case R.id.UI_more:
                break;
        }
    }

    private void changePicAndTitle(String albumId, String songName){
        control_imageView.setImageBitmap(new SongUtils().getAlbumArtPic(this, albumId));
        setTitle(songName);
    }

    public void playNext(Song song){
        playBinder.play(song.getDate());
        setTitle(song.getSongName());
        control_imageView.setImageBitmap(new SongUtils().getAlbumArtPic(MyApplication.getContext(), song.getAlbum_id()));
        addMusicToRecent(song);
    }
}
