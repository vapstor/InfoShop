package br.com.infoshop.ui.projects;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;

import br.com.infoshop.R;
import br.com.infoshop.adapter.ProjectsCategoriesAdapter;
import br.com.infoshop.model.Categorie;
import br.com.infoshop.viewmodel.ProjectsViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProjectsCategoriesFragment extends Fragment {

    private GridView gridViewCategories;
    private ProjectsCategoriesAdapter adapterCategories;
    private ProjectsViewModel projectsViewModel;
    private NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_projects_categories, container, false);
        navController = NavHostFragment.findNavController(this);

        gridViewCategories = root.findViewById(R.id.grid_view_categories);
        //Click na categoria
        gridViewCategories.setOnItemClickListener((parent, view, position, id) -> {
            Categorie categorie = projectsViewModel.getCategories().getValue().get(position);
            adapterCategories.notifyDataSetChanged();
            //podemos pesquisar via id ou nome, por questões de perfomance colocarei por ID

            Bundle bundle = new Bundle();
            bundle.putInt("categoryId", categorie.getId());
            bundle.putString("categoryTitle", categorie.getTitle());
            navController.navigate(R.id.navigation_projects, bundle);
        });

        adapterCategories = new ProjectsCategoriesAdapter(new ArrayList<>(), getContext());
        gridViewCategories.setAdapter(adapterCategories);

        projectsViewModel = new ViewModelProvider(this).get(ProjectsViewModel.class);
        projectsViewModel.fetchCategories();
        projectsViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                if (categories.size() == 0) {
                    Toast.makeText(getContext(), "Sem categorias!", Toast.LENGTH_SHORT).show();
                } else {
                    if (gridViewCategories.getVisibility() != View.VISIBLE)
                        gridViewCategories.setVisibility(View.VISIBLE);
                    adapterCategories.updateList(categories);
                }
            } else {
                Toast.makeText(getContext(), "Falhou ao recuperar categorias!", Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }
}