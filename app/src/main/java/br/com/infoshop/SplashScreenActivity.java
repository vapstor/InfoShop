package br.com.infoshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import static androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation;
import static java.lang.Thread.sleep;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> {
            try {
                sleep(1500);
                runOnUiThread(() -> {
                    Intent intent = new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.startActivity(intent);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}