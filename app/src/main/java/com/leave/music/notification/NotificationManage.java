package com.leave.music.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.leave.music.R;
import com.leave.music.activity.PlayUIActivity;
import com.leave.music.javabean.Song;
import com.leave.music.utils.SongUtils;

/**
 * Created by leave on 2018/4/15.
 */

public class NotificationManage{

    private Notification PlayUInotification;

    public NotificationManage(){
    }

    public Notification getPlayUInotification(Context context,Song song){
        Notification.Builder builder = new Notification.Builder(context);
        Intent intent = new Intent(context,PlayUIActivity.class);
        intent.putExtra("Song",song);
        Intent intent1 = new Intent("android.intent.action.playOrPause");
        Notification.Action action = new Notification.Action.Builder(Icon.createWithResource(context,R.drawable.ic_menu_camera), "pause", PendingIntent.getBroadcast(context,0,intent1,0)).build();
        builder.setContentTitle(song.getSongName())
                .setContentText(song.getArtist())
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setLargeIcon(new SongUtils().getAlbumArtPic(context, song.getAlbum_id()))
                .addAction(action)
                .setContentIntent(PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT));
        PlayUInotification = builder.build();
        return PlayUInotification;
    }

    public Notification getNotification(Context context, Song song, boolean isPause){
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.icon);

        Intent intent = new Intent(context,PlayUIActivity.class);
        intent.putExtra("Song", song);
        RemoteViews rv = new RemoteViews(context.getPackageName(),R.layout.notification_bar);
        rv.setTextViewText(R.id.notification_title,song.getSongName());
        rv.setTextViewText(R.id.notification_content,song.getArtist());
        rv.setImageViewBitmap(R.id.notification_img,new SongUtils().getAlbumArtPic(context,song.getAlbum_id()));
        rv.setOnClickPendingIntent(R.id.notification_view,PendingIntent.getActivity(context,1,intent,PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setCustomContentView(rv);
        return builder.build();
    }
}
