package br.com.infoshop.ui.projects;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import java.util.ArrayList;
import java.util.Objects;

import br.com.infoshop.R;
import br.com.infoshop.activities.ProjectDetailActivity;
import br.com.infoshop.adapter.ProjectsAdapter;
import br.com.infoshop.model.Project;
import br.com.infoshop.utils.EndlessRecyclerViewScrollListener;
import br.com.infoshop.utils.ItemClickSupport;
import br.com.infoshop.utils.RecyclerItemTouchHelper;
import br.com.infoshop.viewmodel.ProjectsViewModel;
import dagger.hilt.android.AndroidEntryPoint;

import static br.com.infoshop.utils.Constants.INTENT_OPEN_PROJECT_DETAIL;
import static br.com.infoshop.utils.Constants.MY_LOG_TAG;

@AndroidEntryPoint
public class ProjectsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private ProjectsViewModel projectsViewModel;
    private NavController navController;
    private int categoryId;
    private RecyclerView recyclerProjects;
    private ConstraintLayout backgroundNoProjects;
    private LinearLayoutManager linearLayoutManager;
    private ProjectsAdapter adapterProjects;
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        navController.navigateUp();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        navController = NavHostFragment.findNavController(this);
        if (args != null) {
            categoryId = args.getInt("categoryId");
            ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(args.getString("categoryTitle"));
            }
            setHasOptionsMenu(true);
        } else {
            Toast.makeText(getContext(), "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
            navController.navigateUp();
        }
    }

    private void initViews(View view) {
        //Configura LM do Recycler
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        adapterProjects = new ProjectsAdapter(new ArrayList<>(), getContext());
        adapterProjects.setHasStableIds(true);

        recyclerProjects = view.findViewById(R.id.projects_recycler);
        recyclerProjects.setLayoutManager(linearLayoutManager);
        recyclerProjects.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerProjects.setItemAnimator(new DefaultItemAnimator());
        recyclerProjects.setAdapter(adapterProjects);

        swipeContainer = view.findViewById(R.id.layout_swipe_refresh);
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        swipeContainer.post(() -> {
            if (swipeContainer != null) {
                swipeContainer.setRefreshing(true);
            }
            fetchProjectsByCategory();
        });
        backgroundNoProjects = view.findViewById(R.id.layout_background_home_recycler);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(new LinearLayoutManager(getContext())) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //Workaround para usar swipe e endless
                if (!swipeContainer.isRefreshing()) {
                    fetchProjectsByCategory();
                }

            }
        };

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerProjects);
        ItemClickSupport.addTo(recyclerProjects).setOnItemClickListener((recyclerView, position, v) -> {
            onDetailProject(projectsViewModel.getProjectsByCategoryLiveData().getValue().get(position));
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        projectsViewModel = new ViewModelProvider(this).get(ProjectsViewModel.class);

        View root = inflater.inflate(R.layout.fragment_projects, container, false);
        initViews(root);
        return root;
    }

    @Override
    public void onRefresh() {
        if (!swipeContainer.isRefreshing()) {
            swipeContainer.setRefreshing(true);
        }
        fetchProjectsByCategory();
    }

    private void fetchProjectsByCategory() {
        projectsViewModel.fetchProjectsByCategory(10, categoryId);
        observeProjects();
    }

    private void observeProjects() {
        projectsViewModel.getProjectsByCategoryLiveData().observe(getViewLifecycleOwner(), projects -> {
            if (projects != null) {
                if (projects.size() == 0) {
                    if (recyclerProjects.getVisibility() == View.VISIBLE)
                        recyclerProjects.setVisibility(View.GONE);
                    if (backgroundNoProjects.getVisibility() != View.VISIBLE)
                        backgroundNoProjects.setVisibility(View.VISIBLE);
                    if (swipeContainer.isRefreshing()) {
                        swipeContainer.setRefreshing(false);
                    }
                } else {
                    //Confirme que o frame não bloqueia a UI
                    toggleFrameLoadingVisibility(false);

                    if (backgroundNoProjects.getVisibility() == View.VISIBLE)
                        backgroundNoProjects.setVisibility(View.GONE);
                    if (recyclerProjects.getVisibility() != View.VISIBLE)
                        recyclerProjects.setVisibility(View.VISIBLE);


                    adapterProjects.updateProjectsList(projects);
                    if (recyclerProjects.getVisibility() != View.VISIBLE) {
                        recyclerProjects.setVisibility(View.VISIBLE);
                    }
                    // setRefreshing(false) to signal refresh has finished
                    if (swipeContainer.isRefreshing()) {
                        swipeContainer.setRefreshing(false);
                    }
                }
            } else {
                Toast.makeText(getContext(), "Falhou ao recuperar projetos!", Toast.LENGTH_SHORT).show();
                if (swipeContainer.isRefreshing()) {
                    swipeContainer.setRefreshing(false);
                }
                if (backgroundNoProjects.getVisibility() == View.GONE)
                    backgroundNoProjects.setVisibility(View.VISIBLE);
                if (recyclerProjects.getVisibility() == View.VISIBLE)
                    recyclerProjects.setVisibility(View.GONE);
            }
        });
    }

    private void toggleFrameLoadingVisibility(boolean frameVisibility) {
        if (getActivity() != null) {
            final ViewGroup root = getActivity().findViewById(R.id.root_home);
            if (root != null) {
                FrameLayout frameLayout = root.findViewById(R.id.layout_frame_loading);
                TransitionManager.beginDelayedTransition(root, new Fade().setDuration(750));
                if (frameVisibility) {
                    frameLayout.setVisibility(View.VISIBLE);
                    frameLayout.setClickable(true);
                    frameLayout.setOnClickListener(v -> Toast.makeText(getContext(), "Aguarde", Toast.LENGTH_SHORT).show());
                } else {
                    frameLayout.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void onDetailProject(Project project) {
        Intent intent = new Intent(getContext(), ProjectDetailActivity.class);
        intent.putExtra("selectedProject", project);
        startActivityForResult(intent, INTENT_OPEN_PROJECT_DETAIL);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        Log.d(MY_LOG_TAG, "viewHolder.getAdapterPosition() : " + viewHolder.getAdapterPosition());
        Log.d(MY_LOG_TAG, "POSITION: " + position);
        new AlertDialog.Builder(getContext(), R.style.AlertDialog_Rounded)
                .setTitle(R.string.excluir)
                .setMessage(R.string.voce_tem_certeza_que_nao_deseja_mais_ver_este_projeto)
                .setNegativeButton(R.string.cancelar, (dialog, which) -> {
//                    adapterProjects.notifyItemChanged(position);
                    adapterProjects.notifyItemRemoved(position);
                    adapterProjects.notifyItemInserted(position);
                })
                .setPositiveButton(R.string.excluir, (dialog, which) -> {
                    try {
                        adapterProjects.notifyItemRemoved(position);
                        Project project = Objects.requireNonNull(projectsViewModel.getAllProjectsLiveData().getValue()).get(position);
                        projectsViewModel.removeProject(project);
                    } catch (NullPointerException | IllegalStateException e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(e.getMessage());
                        builder.setPositiveButton("OK", (dialog1, which1) -> {
                        });
                        builder.create().show();
                        Log.e(MY_LOG_TAG, "Erro: " + e.getLocalizedMessage());
                    }

                }).setOnDismissListener(dialog -> adapterProjects.notifyItemChanged(position)).show();
    }
}