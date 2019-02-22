package com.leave.music.activity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.leave.music.MyApplication;
import com.leave.music.R;
import com.leave.music.asynctask.FindTask;
import com.leave.music.fragment.CollectFragment;
import com.leave.music.fragment.LocalFragment;
import com.leave.music.fragment.RecentFragment;
import com.leave.music.fragment.dummy.DummyContent;
import com.leave.music.javabean.Song;
import com.leave.music.notification.NotificationManage;
import com.leave.music.service.FindService;
import com.leave.music.service.PlayService;
import com.leave.music.utils.SongUtils;

import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        CollectFragment.OnFragmentInteractionListener,
        RecentFragment.OnListFragmentInteractionListener,
        LocalFragment.OnLocalFragmentInteractionListener{

    private static final String TAG = "MainActivity";

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playBinder = (PlayService.PlayBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private SQLiteDatabase db;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private LocalFragment localFragment;
    private RecentFragment recentFragment;
    private CollectFragment collectFragment;

    public static LocalBroadcastManager manager = LocalBroadcastManager.getInstance(MyApplication.getContext());
    private FinishReceiver finishReceiver = new FinishReceiver();
    private NotificationReceiver notificationReceiver = new NotificationReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermission();

        Intent playServiceIntent = new Intent(this, PlayService.class);
        startService(playServiceIntent);
        bindService(playServiceIntent, connection, BIND_AUTO_CREATE);

        db = LitePal.getDatabase();

        musics = new SongUtils().getSongList();
        localFragment = LocalFragment.newInstance(new SongUtils().getSongList());
        recentFragment = RecentFragment.newInstance(recent);
        collectFragment = CollectFragment.newInstance("","");
        initView();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.registerReceiver(finishReceiver, new IntentFilter("android.intent.action.addToRecent"));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.playOrPause");
        intentFilter.addAction("android.intent.action.next");
        manager.registerReceiver(notificationReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        manager.unregisterReceiver(finishReceiver);
        manager.unregisterReceiver(notificationReceiver);
        playBinder.finish();
    }

    private void initView(){
        //播放器控制栏
        control_imageView = findViewById(R.id.control_imageView);
        control_textView = findViewById(R.id.control_textView);
        control_playOrPause = findViewById(R.id.control_playOrPause);
        control_next = findViewById(R.id.control_next);
        control_playOrPause.setOnClickListener(this);
        control_next.setOnClickListener(this);
        control_textView.setOnClickListener(this);

        //标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_camera);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_fragment, localFragment);
        transaction.commit();
    }
    private void getPermission(){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.action_find){
            Intent findIntent = new Intent(MyApplication.getContext(), FindService.class);
            startService(findIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            transaction.replace(R.id.main_fragment, localFragment);
        } else if (id == R.id.nav_gallery) {
            transaction.replace(R.id.main_fragment,recentFragment);
        }
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.control_playOrPause:
                if (size == -1)
                    return;
                if (playBinder.pause()){
                    changePlayOrPauseImage(android.R.drawable.ic_media_play);
                }else {
                    changePlayOrPauseImage(android.R.drawable.ic_media_pause);
                }
                notificationManager.notify(1,new NotificationManage().getNotification(MyApplication.getContext(),musics.get(size), playBinder.isPause()));
                break;
            case R.id.control_next:
                if (size == -1)
                    return;
                else if (size == musics.size()-1){
                    size = -1;
                }
                size++;
                Song song = musics.get(size);
                playNext(song);
                changePlayOrPauseImage(android.R.drawable.ic_media_pause);
                notificationManager.notify(1,new NotificationManage().getNotification(MyApplication.getContext(),song, playBinder.isPause()));
                break;
            case R.id.control_textView:
                Intent intent = new Intent(MyApplication.getContext(), PlayUIActivity.class);
                if (size != -1)
                    intent.putExtra("Song", musics.get(size));
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onListFragmentInteraction(Song song, int position) {
        playNext(song);
        changePlayOrPauseImage(android.R.drawable.ic_media_pause);
        musics.clear();
        musics.addAll(recent);
        notificationManager.notify(1,new NotificationManage().getNotification(MyApplication.getContext(),song,playBinder.isPause()));
        size = position;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onLocalFragmentInteraction(Song song, int position) {
        //playNext(song);
        musics.clear();
        musics.addAll(new SongUtils().getSongList());
        playBinder.play(song);
        control_textView.setText(song.getSongName() + "\n" + song.getArtist());
        control_imageView.setImageBitmap(new SongUtils().getAlbumArtPic(MyApplication.getContext(), song.getAlbum_id()));
        addMusicToRecent(song);
        changePlayOrPauseImage(android.R.drawable.ic_media_pause);
        notificationManager.notify(1,new NotificationManage().getNotification(MyApplication.getContext(),song,playBinder.isPause()));
        size = position;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1){
            if (grantResults.length < 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MyApplication.getContext(),"禁止该权限会导致应用无法使用", Toast.LENGTH_SHORT);
            }
        }
    }

    class FinishReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {            
            String playMode = intent.getStringExtra("playMode");
            if (playMode == "single") {
                //change Button Image
                changePlayOrPauseImage(android.R.drawable.ic_media_play);
                notificationManager.notify(1,new NotificationManage().getNotification(MyApplication.getContext(),musics.get(size), playBinder.isPause()));
            }else if (playMode == "singleLoop"){
                //as usual
            }else if (playMode == "listLoop"){
                //play next music
                //add next music to recent queue
                Song song = getNextMusic();
                playBinder.play(song.getDate());
                control_textView.setText(song.getSongName() + "\n" + song.getArtist());
                addMusicToRecent(song);
                control_imageView.setImageBitmap(new SongUtils().getAlbumArtPic(MyApplication.getContext(), song.getAlbum_id()));
                notificationManager.notify(1,new NotificationManage().getNotification(MyApplication.getContext(),song, playBinder.isPause()));
            }
        }
    }

    class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive: " + action);
            if ("android.intent.action.playOrPause" == action){
                if (playBinder.pause()){
                    changePlayOrPauseImage(android.R.drawable.ic_media_play);
                }else {
                    changePlayOrPauseImage(android.R.drawable.ic_media_pause);
                }
                notificationManager.notify(1,new NotificationManage().getNotification(MyApplication.getContext(),musics.get(size),playBinder.isPause()));
            }else if ("android.intent.action.next" == action){
                if (size == -1)
                    return;
                else if (size == musics.size()-1){
                    size = -1;
                }
                size++;
                Song song = musics.get(size);
                playNext(song);
                notificationManager.notify(1,new NotificationManage().getNotification(MyApplication.getContext(),song, playBinder.isPause()));
            }
        }
    }
}
