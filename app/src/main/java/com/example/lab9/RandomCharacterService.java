package com.example.lab9;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import java.util.Random;

public class RandomCharacterService extends Service {
    private boolean isRandomGeneratorOn;
    private final String TAG = "RandomCharacterService";
    
    char[] alphabet = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".toCharArray();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Сервис запущен", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Сервис запущен...");
        Log.i(TAG, "ID потока в onStartCommand: " + Thread.currentThread().getId());
        isRandomGeneratorOn = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                startRandomGenerator();
            }
        }).start();

        return START_STICKY;
    }

    private void startRandomGenerator() {
        while(isRandomGeneratorOn) {
            try {
                Thread.sleep(1000);
                if(isRandomGeneratorOn) {
                    int MIN = 0;
                    int MAX = alphabet.length - 1;
                    int randomIdx = new Random().nextInt(MAX - MIN + 1) + MIN;
                    char randomChar = alphabet[randomIdx];
                    Log.i(TAG, "ID потока: " + Thread.currentThread().getId() + ", Случайная буква: " + randomChar);

                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("my.custom.action.tag.lab9");
                    broadcastIntent.putExtra("randomCharacter", randomChar);
                    sendBroadcast(broadcastIntent);
                }
            } catch (InterruptedException e) {
                Log.i(TAG, "Поток прерван.");
            }
        }
    }

    private void stopRandomGenerator() {
        isRandomGeneratorOn = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRandomGenerator();
        Toast.makeText(getApplicationContext(), "Сервис остановлен", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Сервис уничтожен...");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
} 