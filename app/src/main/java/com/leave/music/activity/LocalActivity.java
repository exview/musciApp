package com.leave.music.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.leave.music.MyApplication;
import com.leave.music.R;
import com.leave.music.asynctask.FindTask;
import com.leave.music.service.FindService;

import java.util.ArrayList;
import java.util.List;

public class LocalActivity extends BaseActivity implements
        View.OnClickListener{
    private List<String> local_musics = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);

        initView();
        getPermission();
    }

    private void initView(){
        control_imageView = findViewById(R.id.control_imageView);
        control_textView = findViewById(R.id.control_textView);
        control_playOrPause = findViewById(R.id.control_playOrPause);
        control_next = findViewById(R.id.control_next);
        control_textView.setOnClickListener(this);
        control_playOrPause.setOnClickListener(this);
        control_next.setOnClickListener(this);

        ListView local_listView = findViewById(R.id.local_listView);
        ArrayAdapter<String> local_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, local_musics);
        local_listView.setAdapter(local_adapter);
        local_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void getPermission(){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1){
            if (grantResults.length < 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MyApplication.getContext(),"禁止该权限会导致应用无法使用", Toast.LENGTH_SHORT);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.local_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.local_find){
        }else if (id == R.id.local_help){

        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.control_playOrPause:
                break;
            case R.id.control_next:
                break;
        }
    }
}
