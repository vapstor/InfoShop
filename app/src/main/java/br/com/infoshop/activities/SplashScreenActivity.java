package br.com.infoshop.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import br.com.infoshop.R;
import br.com.infoshop.viewmodel.AuthViewModel;
import dagger.hilt.android.AndroidEntryPoint;

import static java.lang.Thread.sleep;

@AndroidEntryPoint
public class SplashScreenActivity extends AppCompatActivity {

    @Inject
    public AuthViewModel authViewModel;

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
                sleep(1250);
                runOnUiThread(() -> {
                    authViewModel.checkIsFirebaseUserLogged();
                    authViewModel.getLoggedUserLiveData().observe(this, user -> {
                        Intent intent;
                        if (user == null) {
                            intent = new Intent(this, LoginOrSignUpActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        } else {
                            intent = new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        this.startActivity(intent);
                    });
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}