package br.com.infoshop.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
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

import br.com.infoshop.R;
import br.com.infoshop.adapter.HomeProjectsAdapter;
import br.com.infoshop.interfaces.IFetchProjects;
import br.com.infoshop.model.Project;
import br.com.infoshop.ui.projects.ProjectsViewModel;
import br.com.infoshop.utils.ItemClickSupport;
import br.com.infoshop.utils.RecyclerItemTouchHelper;

import static br.com.infoshop.utils.Constants.MY_LOG_TAG;
import static java.lang.Thread.sleep;

public class HomeFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, IFetchProjects, SwipeRefreshLayout.OnRefreshListener {

    private HomeViewModel homeViewModel;
    private CountDownTimer countdown;
    private NavController navController;
    ArrayList<Project> projetos = new ArrayList<>();
    private Handler handler;
    private LinearLayoutManager linearLayoutManager;
    private HomeProjectsAdapter adapterProjects;
    private RecyclerView recyclerProjects;
    private SwipeRefreshLayout swipeContainer;
    private BottomNavigationView navView;
    private Bundle mBundleRecyclerViewState;
    private Parcelable mListState;
    private String KEY_RECYCLER_STATE = "1";
    private ProjectsViewModel projectsViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        //Handler para manipular UI
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                updateUI(msg);
            }
        };
        //Countdown timeout ao buscar projetos
        countdown = new CountDownTimer(5000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (getActivity() != null) {
                    if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.navigation_home) {
                        Toast.makeText(getContext(), "Por favor, aguarde...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        projectsViewModel = new ViewModelProvider(requireParentFragment()).get(ProjectsViewModel.class);
    }

    private void updateUI(Message msg) {
        if (msg.what == 0) {
            if ("tentando_conectar".equals(msg.obj)) {
                toggleFrameLoadingVisibility(true);
            }
        } else if (msg.what == 1) {
            switch ((String) msg.obj) {
                case "onPreExecute":
                    toggleFrameLoadingVisibility(true);
                    break;
                case "erro_sql":
                    Toast.makeText(getContext(), "Erro de SQL", Toast.LENGTH_SHORT).show();
                    toggleFrameLoadingVisibility(false);
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                case "onPostExecute":
                    toggleFrameLoadingVisibility(false);
//                    Toast.makeText(getActivity(), "Produtos recuperados com sucesso!", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            Toast.makeText(getContext(), "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
        }
    }

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

    //Simular requisição à API
    private void instanceFakeProjects() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    if (projetos.size() != 0) {
                        projetos.clear();
                    }
                    projetos.add(new Project(1, "Projeto 01", getResources().getString(R.string.lorem_ipsum_1p), 8.0, ""));
                    projetos.add(new Project(2, "Projeto 02", getResources().getString(R.string.lorem_ipsum_1p), 13.0, ""));
                    projetos.add(new Project(3, "Projeto 03", getResources().getString(R.string.lorem_ipsum_1p), 11.0, ""));
                    projetos.add(new Project(4, "Projeto 04", getResources().getString(R.string.lorem_ipsum_1p), 9.0, ""));
                    projetos.add(new Project(5, "Projeto 05", getResources().getString(R.string.lorem_ipsum_1p), 100.0, ""));
                    projetos.add(new Project(6, "Projeto 06", getResources().getString(R.string.lorem_ipsum_1p), 1110.0, ""));
                    projetos.add(new Project(7, "Projeto 07", getResources().getString(R.string.lorem_ipsum_1p), 23.5, ""));

                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    HomeFragment.this.onGetDataDone(null);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                HomeFragment.this.onGetDataDone(projetos);
            }
        }.execute();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //Configura LM do Recycler
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        adapterProjects = new HomeProjectsAdapter(new ArrayList<>(), getContext());
        adapterProjects.setHasStableIds(true);

        navView = getActivity().findViewById(R.id.nav_view);

        homeViewModel = new ViewModelProvider(requireParentFragment()).get(HomeViewModel.class);
        final TextView boasVindasTextView = root.findViewById(R.id.boas_vindas_text_view);

        homeViewModel.getBoasVindasMessageLiveData().observe(getViewLifecycleOwner(), boasVindasTextView::setText);
        homeViewModel.getProjectsLiveData().observe(getViewLifecycleOwner(), projectsList -> {
            //Confirme que o frame não bloqueia a UI
            toggleFrameLoadingVisibility(false);

            if (projectsList.size() == 0) {
                fetchProjects();
            } else {
                adapterProjects.clear();
                adapterProjects.addAll(projectsList);

                if (recyclerProjects.getVisibility() != View.VISIBLE) {
                    recyclerProjects.setVisibility(View.VISIBLE);
                }

                // setRefreshing(false) to signal refresh has finished
                if (swipeContainer.isRefreshing()) {
                    swipeContainer.setRefreshing(false);
                }

            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getContext();
        if (context != null) {
            recyclerProjects = view.findViewById(R.id.recycler_projects_home);

            recyclerProjects.setLayoutManager(linearLayoutManager);
            recyclerProjects.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
            recyclerProjects.setItemAnimator(new DefaultItemAnimator());
            recyclerProjects.setAdapter(adapterProjects);

            swipeContainer = view.findViewById(R.id.layout_swipe_refresh);
            swipeContainer.setOnRefreshListener(this);
            swipeContainer.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
            /**
             * Showing Swipe Refresh animation on activity create
             * As animation won't start on onCreate, post runnable is used
             */
            swipeContainer.post(() -> {
                if (swipeContainer != null) {
                    swipeContainer.setRefreshing(true);
                }
                fetchProjects();
            });
            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerProjects);
            ItemClickSupport.addTo(recyclerProjects).setOnItemClickListener((recyclerView, position, v) -> {
                Toast.makeText(context, "Você clicou na posição " + position, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void fetchProjects() {
        //resgata produtos assíncronamente
//        swipeContainer.setRefreshing(true);
        instanceFakeProjects();
        //Inicia counter para timeout
        countdown.start();
    }

    @Override
    public void onGetDataDone(ArrayList<Project> projectsList) {
        if (getActivity() != null) {
            //Cancela countdown pois os dados chegaram
            if (countdown != null) {
                countdown.cancel();
            }
            homeViewModel.setProjectsLiveData(projectsList);
        } else {
            Log.e(MY_LOG_TAG, "Activity NULL!");
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
                    projetos = homeViewModel.getProjectsLiveData().getValue();
                    if (projetos != null) {
                        homeViewModel.removeProject(projetos.get(position));
                        adapterProjects.notifyItemRemoved(position);
                    }
                }).setOnDismissListener(dialog -> adapterProjects.notifyItemChanged(position)).show();
    }

    @Override
    public void onRefresh() {
        if (!swipeContainer.isRefreshing()) {
            swipeContainer.setRefreshing(true);
        }
        fetchProjects();
    }
}