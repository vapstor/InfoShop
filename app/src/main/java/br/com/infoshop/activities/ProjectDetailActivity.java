package br.com.infoshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import javax.inject.Inject;

import br.com.infoshop.R;
import br.com.infoshop.model.Project;
import br.com.infoshop.viewmodel.ProjectsViewModel;
import dagger.hilt.android.AndroidEntryPoint;

import static androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation;
import static br.com.infoshop.utils.Constants.MY_LOG_TAG;
import static br.com.infoshop.utils.Util.RSmask;

@AndroidEntryPoint
public class ProjectDetailActivity extends AppCompatActivity {

    private Project project;
    private View imagem;
    private String actionTextExplained = "";
    @Inject
    public ProjectsViewModel projectsViewModel;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private TextView projectDetailTitle, projectDetailPrice, projetDetailDescription;
    private ConstraintLayout frameLoading;
    private boolean isFavorite;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Toast.makeText(this, "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
            Log.e(MY_LOG_TAG, "Falhou ao resgatar dados da intent");
            finish();
        } else {
            project = extras.getParcelable("selectedProject");
            setContentView(R.layout.activity_project_detail);

            //resgata lista atualizada de favoritos do firebase
            projectsViewModel.fetchFavorites();

            //observa alterações na lista para verificar se pertence ou não aos favoritos
            projectsViewModel.getFavoritesLiveData().observe(this, favorites -> {
                if (favorites != null) {
                    if (favorites.size() > 0) {
                        for (int i = 0; i < favorites.size(); i++) {
                            //O projeto pode ser o mesmo mas o endereço de memória dos dois objetos pode não ser o mesmo.
                            //Por causa disso não pode ser contains(o).
                            if (project.getId() == favorites.get(i).getId()) {
                                projectsViewModel.getIsFavoriteLiveData().setValue(true);
                                removeLoadingFrame();
                                return;
                            }
                        }
                    }
                    projectsViewModel.getIsFavoriteLiveData().setValue(false);
                    removeLoadingFrame();
                } else {
                    Toast.makeText(this, "Sem favoritos!", Toast.LENGTH_SHORT).show();
                    Log.e(MY_LOG_TAG, "Favoritos Nulo!");
                }
            });
        }
        initViews();

        projectDetailTitle.setText(project.getTitulo());
        projectDetailPrice.setText(RSmask(project.getPreco()));
        projetDetailDescription.setText(project.getDescricao());
        imagem.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProjectDetailImageActivity.class);
            try {
                Bundle bundle = makeSceneTransitionAnimation(Objects.requireNonNull(this)).toBundle();
                intent.putExtra("imageURL", project.getImagem());
                startActivity(intent, bundle);
            } catch (Exception e) {
                Toast.makeText(this, "Ocorreu um erro ao carregar a imagem!", Toast.LENGTH_SHORT).show();
            }
        });

        projectsViewModel.getIsFavoriteLiveData().observe(this, fav -> {
            if (fav) {
                setDrawableFull();
            } else {
                setDrawableEmpty();
            }
        });

        fab.setOnClickListener(v -> updateProjectStatus());

        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());
        setSupportActionBar(toolbar);
    }

    private void updateProjectStatus() {
        try {
            boolean fav = projectsViewModel.getIsFavoriteLiveData().getValue().booleanValue();
            if (fav) {
                projectsViewModel.removeFavorite(project);
                actionTextExplained = "Você desfavoritou este projeto!";
            } else {
                projectsViewModel.addFavorite(project);
                actionTextExplained = "Você favoritou este projeto!";
            }
            createAndShowSnackBar();
        } catch (Exception e) {
            showErrorDialog(e);
        }
    }

    private void removeLoadingFrame() {
        frameLoading.setVisibility(View.INVISIBLE);
    }

    private void initViews() {
        fab = findViewById(R.id.fab);
        projectDetailTitle = findViewById(R.id.project_detail_title);
        frameLoading = findViewById(R.id.frame_loading_project_detail);
        projetDetailDescription = findViewById(R.id.project_detail_description);
        projectDetailPrice = findViewById(R.id.project_detail_price);
        imagem = findViewById(R.id.imagem_project_detail);
        toolbar = findViewById(R.id.toolbar);
    }

    public void createAndShowSnackBar() {
        snackbar = Snackbar.make(fab, actionTextExplained, Snackbar.LENGTH_LONG)
                .setAction("Desfazer", v1 -> updateProjectStatus());
        snackbar.show();
    }

    private void setDrawableFull() {
        fab.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_star_24, null));
    }

    private void setDrawableEmpty() {
        fab.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_star_border_24, null));
    }

    private void showErrorDialog(Exception e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(e.getMessage());
        builder.setTitle("Ocorreu um erro!");
        builder.setPositiveButton("Ok", (dialog, which) -> {
        });
        builder.create().show();
    }
}