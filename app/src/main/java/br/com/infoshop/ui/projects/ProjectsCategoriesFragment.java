package br.com.infoshop.ui.projects;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class ProjectsCategoriesFragment extends Fragment {

    private GridView gridViewCategories;
    private ProjectsCategoriesAdapter adapterCategories;
    private ProjectsViewModel projectsViewModel;
    private Button btnCategorie;
    private NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_projects_categories, container, false);
        gridViewCategories = root.findViewById(R.id.grid_view_categories);
        projectsViewModel = new ViewModelProvider(requireParentFragment()).get(ProjectsViewModel.class);
        navController = NavHostFragment.findNavController(this);
        //Click na categoria
        gridViewCategories.setOnItemClickListener((parent, view, position, id) -> {

            Categorie categorie = projectsViewModel.getCategoriesLiveData().getValue().get(position);
            adapterCategories.notifyDataSetChanged();
            //podemos pesquisar via id ou nome, por questões de perfomance colocarei por ID

            Bundle bundle = new Bundle();
            bundle.putInt("categoryId", categorie.getId());
            Toast.makeText(getContext(), "Category Id " + categorie.getId(), Toast.LENGTH_SHORT).show();

            projectsViewModel.addFavorite(categorie.getId());
            navController.navigate(R.id.navigation_projects, bundle);
        });

        projectsViewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {

            if (categories.size() == 0) {
                fetchCategories();
            } else {
                if (gridViewCategories.getVisibility() != View.VISIBLE)
                    gridViewCategories.setVisibility(View.VISIBLE);
                adapterCategories = new ProjectsCategoriesAdapter(categories, getContext());
                gridViewCategories.setAdapter(adapterCategories);
            }

        });
        return root;
    }

    private void fetchCategories() {
        ArrayList<Categorie> categories = new ArrayList<>();
        categories.add(new Categorie(1, "Titulo 01", R.drawable.empty_image_placeholder));
        categories.add(new Categorie(2, "Titulo 02", R.drawable.empty_image_placeholder));
        categories.add(new Categorie(3, "Titulo 03", R.drawable.empty_image_placeholder));
        categories.add(new Categorie(4, "Titulo 04", R.drawable.empty_image_placeholder));
        projectsViewModel.setCategories(categories);
    }
}