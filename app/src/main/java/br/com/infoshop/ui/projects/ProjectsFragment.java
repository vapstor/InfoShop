package br.com.infoshop.ui.projects;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import br.com.infoshop.R;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProjectsFragment extends Fragment {
    private ProjectsViewModel projectsViewModel;
    private NavController navController;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        navController.navigateUp();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        navController = NavHostFragment.findNavController(this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        projectsViewModel = new ViewModelProvider(this).get(ProjectsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_projects, container, false);
        return root;
    }
}