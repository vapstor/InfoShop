package br.com.infoshop.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

import br.com.infoshop.R;
import br.com.infoshop.ui.login_signup.login.LoginViewModel;

import static java.lang.Thread.sleep;

public class SplashScreenActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        loginViewModel.getLoggedUserLiveData().observe(this, firebaseUser -> user = firebaseUser);

        new Thread(() -> {
            try {
                sleep(1500);
                runOnUiThread(() -> {
//                    Intent intent;
//                    if(user == null) {
//                        intent = new Intent(this, LoginOrSignUpActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    } else {
                    Intent intent = new Intent(this, LoginOrSignUpActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.startActivity(intent);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}