package br.com.infoshop.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import br.com.infoshop.R;
import br.com.infoshop.auth.AuthViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private NavHostFragment navHostFragment;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        //observa mudanças no objeto firebase user
        authViewModel.getLoggedUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser == null) {
                startActivity(new Intent(this, LoginOrSignUpActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_profile, R.id.navigation_home, R.id.navigation_projects_categories).build();
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
            navView.setOnNavigationItemSelectedListener(item -> {
                NavDestination destination = navController.getCurrentDestination();
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        navController.navigate(R.id.navigation_home);
                        break;
                    case R.id.navigation_profile:
                        navController.navigate(R.id.navigation_profile);
                        break;
                    case R.id.navigation_projects_categories:
                        if (destination != null)
                            if (navController.popBackStack(R.id.navigation_projects_categories, false)) {

                            } else {
                                navController.navigate(R.id.navigation_projects_categories);
                            }
//                            if (destination.getId() == R.id.navigation_projects) {
//                                navController.navigate(R.id.navigation_projects_categories);
//                            }
                        break;
                }
                return false;
            });
            navView.setOnNavigationItemReselectedListener(item -> {
                NavDestination destination = navController.getCurrentDestination();
                switch (item.getItemId()) {
                    case R.id.navigation_projects_categories:
                        if (destination != null) {
                            if (destination.getId() != R.id.navigation_projects_categories) {
                                navController.navigateUp();
                            }
                        }
                        break;
                    default: //do nothing to ignore reselect
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (navHostFragment != null) {
            int backstack = navHostFragment.getChildFragmentManager().getBackStackEntryCount();
            if (backstack == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Logout")
                        .setMessage("Você deseja realmente sair?")
                        .setPositiveButton("Sair", (dialog, id) -> authViewModel.logout())
                        .setNegativeButton("Cancelar", (dialog, id) -> { });
                builder.create().show();
            } else {
                super.onBackPressed();
            }
        }
    }
}