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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

public class MainActivity extends AppCompatActivity {
    private EditText randomCharacterEditText;
    private TextView statusTextView;
    private Button startButton;
    private Button stopButton;
    private BroadcastReceiver broadcastReceiver;
    private Intent serviceIntent;
    private boolean isServiceRunning = false;
    private static final String KEY_IS_SERVICE_RUNNING = "is_service_running";
    private static final String KEY_LAST_CHARACTER = "last_character";
    private Animation fadeInAnimation;
    private Animation buttonAnimation;
    private final Handler handler = new Handler(Looper.getMainLooper());

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
        statusTextView = findViewById(R.id.textView_status);
        startButton = findViewById(R.id.button_start);
        stopButton = findViewById(R.id.button_end);
        broadcastReceiver = new MyBroadcastReceiver();
        serviceIntent = new Intent(getApplicationContext(), RandomCharacterService.class);
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.button_press);
        
        setupButtonAnimations();

        if (savedInstanceState != null) {
            isServiceRunning = savedInstanceState.getBoolean(KEY_IS_SERVICE_RUNNING, false);
            String lastChar = savedInstanceState.getString(KEY_LAST_CHARACTER, "");
            if (!lastChar.isEmpty()) {
                randomCharacterEditText.setText(lastChar);
            }
            if (isServiceRunning) {
                startService(serviceIntent);
                updateStatusView(true);
            }
        }
    }

    private void setupButtonAnimations() {
        buttonAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                handler.postDelayed(() -> {
                    startButton.clearAnimation();
                    stopButton.clearAnimation();
                }, 50);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void updateStatusView(boolean isRunning) {
        if (isRunning) {
            statusTextView.setText("Статус: работает");
            statusTextView.setTextColor(Color.parseColor("#4CAF50"));
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        } else {
            statusTextView.setText("Статус: остановлен");
            statusTextView.setTextColor(Color.parseColor("#F44336"));
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
    }

    private void updateCharacterWithAnimation(String character) {
        randomCharacterEditText.setText(character);
        randomCharacterEditText.startAnimation(fadeInAnimation);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_SERVICE_RUNNING, isServiceRunning);
        outState.putString(KEY_LAST_CHARACTER, randomCharacterEditText.getText().toString());
    }

    public void onClick(View view) {
        view.startAnimation(buttonAnimation);
        
        if (view.getId() == R.id.button_start) {
            startService(serviceIntent);
            isServiceRunning = true;
            updateStatusView(true);
        } else if (view.getId() == R.id.button_end) {
            stopService(serviceIntent);
            randomCharacterEditText.setText("");
            isServiceRunning = false;
            updateStatusView(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("my.custom.action.tag.lab9");
        registerReceiver(broadcastReceiver, intentFilter);
        updateStatusView(isServiceRunning);
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
                updateCharacterWithAnimation(String.valueOf(data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}