package br.com.infoshop.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import br.com.infoshop.R;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginOrSignUpActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_fragments);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_login).build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.login_signup_nav_host_fragment);
        NavController navController;
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if(destination.getId() == R.id.navigation_login) {

                }
            });
        }

    }

}