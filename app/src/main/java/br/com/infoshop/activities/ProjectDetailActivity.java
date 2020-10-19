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
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import br.com.infoshop.R;
import br.com.infoshop.model.Project;
import br.com.infoshop.ui.projects.ProjectsViewModel;
import dagger.hilt.android.AndroidEntryPoint;

import static androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation;
import static br.com.infoshop.utils.Constants.MY_LOG_TAG;
import static br.com.infoshop.utils.Util.RSmask;

@AndroidEntryPoint
public class ProjectDetailActivity extends AppCompatActivity {

    private Project project;
    private View imagem;
    private boolean fav;
    private String actionTextExplained = "";
    public ProjectsViewModel projectsViewModel;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private TextView projectDetailTitle, projectDetailPrice, projetDetailDescription;

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

            projectsViewModel = new ViewModelProvider(this).get(ProjectsViewModel.class);
            projectsViewModel.fetchFavorites();
            projectsViewModel.getFavoritesLiveData().observe(this, projects -> {
                if (projects != null) {
                    projectsViewModel.checkIsFavorite(project);
                }
            });

            initViews();

            projectsViewModel.getProjectIsFavoriteLiveData().observe(this, this::updateProjectStatus);


            projectDetailTitle.setText(project.getTitulo());
            projectDetailPrice.setText(RSmask(project.getPreco()));
            projetDetailDescription.setText(project.getDescricao());
            imagem.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProjectDetailImageActivity.class);
                Bundle bundle = makeSceneTransitionAnimation(Objects.requireNonNull(this)).toBundle();
                intent.putExtra("imageURL", project.getImagem());
                startActivity(intent, bundle);
            });

            setSupportActionBar(toolbar);
            CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
            toolBarLayout.setTitle(getTitle());
        }
    }

    private void initViews() {
        fab = findViewById(R.id.fab);
        projectDetailTitle = findViewById(R.id.project_detail_title);
        projetDetailDescription = findViewById(R.id.project_detail_description);
        projectDetailPrice = findViewById(R.id.project_detail_price);
        toolbar = findViewById(R.id.toolbar);
        imagem = findViewById(R.id.imagem_project_detail);
    }

    private void updateProjectStatus(boolean fav) {
        if (fav) {
            actionTextExplained = "Você desfavoritou este projeto!";
            fab.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_star_24, null));
        } else {
            actionTextExplained = "Você favoritou este projeto!";
            fab.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_star_border_24, null));

        }
        Snackbar snackbar = Snackbar.make(fab, actionTextExplained, Snackbar.LENGTH_LONG)
                .setAction("Desfazer", v -> {
                    try {
                        if (fav) {
                            projectsViewModel.addFavorite(project);
                        } else {
                            projectsViewModel.removeFavorite(project);
                        }
                    } catch (Exception e) {
                        showErrorDialog(e);
                    }
                });
        fab.setOnClickListener(view -> {
            try {
                if (fav) {
                    projectsViewModel.removeFavorite(project);
                } else {
                    projectsViewModel.addFavorite(project);
                }
                snackbar.show();
            } catch (Exception e) {
                showErrorDialog(e);
            }
        });
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