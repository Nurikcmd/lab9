package com.example.lab9;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;

public class RandomCharacterService extends Service {
    private boolean isRandomGeneratorOn;
    private final String TAG = "RandomCharacterService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Сервис запущен", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Сервис запущен...");
        isRandomGeneratorOn = true;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRandomGeneratorOn = false;
        Toast.makeText(getApplicationContext(), "Сервис остановлен", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Сервис уничтожен...");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
} 