package com.leave.music.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

import com.leave.music.asynctask.FindTask;

import java.io.File;

public class FindService extends Service {
    private FindTask findTask;

    public FindService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        findTask = new FindTask();
        findTask.execute(Environment.getExternalStorageDirectory());
        return super.onStartCommand(intent, flags, startId);
    }
}
