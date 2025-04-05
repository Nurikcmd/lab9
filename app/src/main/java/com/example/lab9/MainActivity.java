package com.example.lab9;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private EditText randomCharacterEditText;
    private BroadcastReceiver broadcastReceiver;
    private Intent serviceIntent;
    private boolean isServiceRunning = false;
    private static final String KEY_IS_SERVICE_RUNNING = "is_service_running";
    private static final String KEY_LAST_CHARACTER = "last_character";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        randomCharacterEditText = findViewById(R.id.editText_randomCharacter);
        broadcastReceiver = new MyBroadcastReceiver();
        serviceIntent = new Intent(getApplicationContext(), RandomCharacterService.class);

        if (savedInstanceState != null) {
            isServiceRunning = savedInstanceState.getBoolean(KEY_IS_SERVICE_RUNNING, false);
            String lastChar = savedInstanceState.getString(KEY_LAST_CHARACTER, "");
            if (!lastChar.isEmpty()) {
                randomCharacterEditText.setText(lastChar);
            }
            if (isServiceRunning) {
                startService(serviceIntent);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_SERVICE_RUNNING, isServiceRunning);
        outState.putString(KEY_LAST_CHARACTER, randomCharacterEditText.getText().toString());
    }

    public void onClick(View view) {
        if (view.getId() == R.id.button_start) {
            startService(serviceIntent);
            isServiceRunning = true;
        } else if (view.getId() == R.id.button_end) {
            stopService(serviceIntent);
            randomCharacterEditText.setText("");
            isServiceRunning = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("my.custom.action.tag.lab9");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                char data = intent.getCharExtra("randomCharacter", '?');
                randomCharacterEditText.setText(String.valueOf(data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}