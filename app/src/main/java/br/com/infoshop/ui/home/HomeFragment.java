package br.com.infoshop.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import br.com.infoshop.R;
import br.com.infoshop.activities.MainActivity;
import br.com.infoshop.activities.ProjectDetailActivity;
import br.com.infoshop.adapter.ProjectsAdapter;
import br.com.infoshop.model.Project;
import br.com.infoshop.utils.EndlessRecyclerViewScrollListener;
import br.com.infoshop.utils.ItemClickSupport;
import br.com.infoshop.utils.RecyclerItemTouchHelper;
import br.com.infoshop.viewmodel.AuthViewModel;
import br.com.infoshop.viewmodel.HomeViewModel;
import br.com.infoshop.viewmodel.PesquisarViewModel;
import br.com.infoshop.viewmodel.ProjectsViewModel;
import dagger.hilt.android.AndroidEntryPoint;

import static br.com.infoshop.utils.Constants.INTENT_OPEN_PROJECT_DETAIL;
import static br.com.infoshop.utils.Constants.MY_LOG_TAG;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, SwipeRefreshLayout.OnRefreshListener {

    private HomeViewModel homeViewModel;
    private LinearLayoutManager linearLayoutManager;
    private ProjectsAdapter adapterProjects;
    private RecyclerView recyclerProjects;
    private SwipeRefreshLayout swipeContainer;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    private ProjectsViewModel projectsViewModel;
    private TextView usernameView;
    @Inject
    protected AuthViewModel authViewModel;
    private ConstraintLayout backgroundNoProjects;
    private PesquisarViewModel pesquisarViewModel;
    //Query para pesquisar
    private String query;
    private BottomNavigationView navView;

    //Adiciona um frame ao recycler para indicar carregamento
    //@param bool frameVisibility: se o frame está visivel (true) ou não
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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        projectsViewModel = new ViewModelProvider(this).get(ProjectsViewModel.class);

        navView = getActivity().findViewById(R.id.nav_view);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        final TextView boasVindasTextView = root.findViewById(R.id.boas_vindas_text_view);
        usernameView = root.findViewById(R.id.home_usuario_name_text_view);

        homeViewModel.getBoasVindasMessageLiveData().observe(getViewLifecycleOwner(), boasVindasTextView::setText);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getContext();
        if (context != null) {
            setHasOptionsMenu(true);
            usernameView.setText(authViewModel.getLoggedUserLiveData().getValue().getUsername());
            projectsViewModel = new ViewModelProvider(this).get(ProjectsViewModel.class);
            pesquisarViewModel = new ViewModelProvider(this).get(PesquisarViewModel.class);

            //Configura LM do Recycler
            linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            adapterProjects = new ProjectsAdapter(new ArrayList<>(), getContext());
            adapterProjects.setHasStableIds(true);

            recyclerProjects = view.findViewById(R.id.recycler_projects_home);
            recyclerProjects.setLayoutManager(linearLayoutManager);
            recyclerProjects.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
            recyclerProjects.setItemAnimator(new DefaultItemAnimator());
            recyclerProjects.setAdapter(adapterProjects);
            swipeContainer = view.findViewById(R.id.layout_swipe_refresh);
            swipeContainer.setOnRefreshListener(this);
            // Retain an instance so that you can call `resetState()` for fresh searches
            scrollListener = new EndlessRecyclerViewScrollListener(new LinearLayoutManager(getContext())) {

                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    //Workaround para usar swipe e endless
                    if (!swipeContainer.isRefreshing()) {
                        fetchProjects(query);
                    }

                }
            };
            //Endless RecyclerView
            recyclerProjects.addOnScrollListener(scrollListener);

            backgroundNoProjects = view.findViewById(R.id.layout_background_home_recycler);

            swipeContainer.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
            swipeContainer.post(() -> {
                if (swipeContainer != null) {
                    swipeContainer.setRefreshing(true);
                }
                fetchProjects(query);
            });
            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerProjects);
            ItemClickSupport.addTo(recyclerProjects).setOnItemClickListener((recyclerView, position, v) -> {
                onDetailProject(projectsViewModel.getAllProjectsLiveData().getValue().get(position));
            });
        }
    }

    private void onDetailProject(Project project) {
        Intent intent = new Intent(getContext(), ProjectDetailActivity.class);
        intent.putExtra("selectedProject", project);
        startActivityForResult(intent, INTENT_OPEN_PROJECT_DETAIL);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK && requestCode == INTENT_OPEN_PROJECT_DETAIL) {
//            Log.d(MY_LOG_TAG, "ACTIVITY RESULT VOLTOU OK");
//        }
//    }

    //resgata projetos
    //param query: "" -> todos
    //param query !"" -> filtrados por nome/descricao
    private void fetchProjects(String query) {
        if (query != null && !query.isEmpty()) {
            projectsViewModel.fetchProjectsByQuery(query, 10);
            projectsViewModel.getFilteredProjectsLiveData().observe(getViewLifecycleOwner(), projects -> {
                if (projects != null) {
                    if (projects.size() == 0) {
                        if (recyclerProjects.getVisibility() == View.VISIBLE)
                            recyclerProjects.setVisibility(View.GONE);
                        if (backgroundNoProjects.getVisibility() != View.VISIBLE)
                            backgroundNoProjects.setVisibility(View.VISIBLE);

                        Toast.makeText(getContext(), "Não há projetos com esse Titulo ou Descrição!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "Falhou ao recuperar projetos filtrados!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            //swipeContainer.setRefreshing(true);
            projectsViewModel.fetchProjects(10);
            projectsViewModel.getAllProjectsLiveData().observe(getViewLifecycleOwner(), projects -> {
                if (projects != null) {
                    if (projects.size() == 0) {
                        if (recyclerProjects.getVisibility() == View.VISIBLE)
                            recyclerProjects.setVisibility(View.GONE);
                        if (backgroundNoProjects.getVisibility() != View.VISIBLE)
                            backgroundNoProjects.setVisibility(View.VISIBLE);

                        Toast.makeText(getContext(), "Sem projetos!", Toast.LENGTH_SHORT).show();
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
                }
            });
        }
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

    @Override
    public void onRefresh() {
        if (!swipeContainer.isRefreshing()) {
            swipeContainer.setRefreshing(true);
        }
//        projectsViewModel.clearProjects();
        fetchProjects("");
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_option_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        searchView.setQueryHint(getString(R.string.titulo_ou_descricao));
        item.setActionView(searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                item.collapseActionView();
                fetchProjects(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //de fato não utilizado (para consultas a cada letra inserida)
                if (!newText.equals("")) {
                    pesquisarViewModel.setNewQuery(newText);
                }
                return false;
            }
        });
        if (query != null && !query.equals("")) {
            searchView.setQuery(query, true);
            query = null;
        }
    }
}