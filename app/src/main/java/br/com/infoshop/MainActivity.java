package br.com.infoshop;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_profile, R.id.navigation_home, R.id.navigation_projects_categories).build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController;
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
            navView.setOnNavigationItemSelectedListener(item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        navController.navigate(R.id.navigation_home);
                        break;
                    case R.id.navigation_profile:
                        navController.navigate(R.id.navigation_profile);
                        break;
                    case R.id.navigation_projects_categories:
                        navController.navigate(R.id.navigation_projects_flow);
                        break;
                }
                return false;
            });
            navView.setOnNavigationItemReselectedListener(item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_projects_categories:
                        if (navController.getCurrentDestination() != navController.getGraph().findNode(R.id.navigation_projects_categories)) {
                            navController.navigateUp();
                        }
                        break;
                    default: //do nothing to ignore reselect
                }
            });
        }
    }


}