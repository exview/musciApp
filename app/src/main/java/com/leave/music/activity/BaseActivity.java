package com.leave.music.activity;

import android.app.NotificationManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.leave.music.MyApplication;
import com.leave.music.javabean.Song;
import com.leave.music.service.PlayService;
import com.leave.music.utils.SongUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Created by leave on 2018/4/12.
 */

public class BaseActivity extends AppCompatActivity {
    ImageView control_imageView;
    TextView control_textView;
    ImageButton control_playOrPause, control_next;

    public static NotificationManager notificationManager;
    public static PlayService.PlayBinder playBinder;

    static SongUtils songUtils = new SongUtils();
    public static List<Song> musics = new ArrayList<>();
    public static Deque<Song> recent = new ArrayDeque<>(100);
    static int size = -1;

    public static Song getNextMusic(){
        if (size == -1)
            return null;
        if (size == musics.size() - 1)
            size = -1;
        size++;
        return musics.get(size);
    }

    public void changePlayOrPauseImage(int resourceId){
        control_playOrPause.setImageResource(resourceId);
    }

    public void changeTextView(String text){
        control_textView.setText(text);
    }


    public void addMusicToRecent(Song song){
        if (recent.size() == 100){
            recent.removeLast();
        }else if (recent.contains(song)){
            recent.remove(song);
        }
        recent.addFirst(song);
    }

    public void playNext(Song song){
        playBinder.play(song.getDate());
        control_textView.setText(song.getSongName() + "\n" + song.getArtist());
        control_imageView.setImageBitmap(new SongUtils().getAlbumArtPic(MyApplication.getContext(), song.getAlbum_id()));
        addMusicToRecent(song);
    }
}
