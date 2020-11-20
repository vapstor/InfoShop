package br.com.infoshop.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import br.com.infoshop.R;
import br.com.infoshop.activities.ProjectDetailActivity;
import br.com.infoshop.adapter.FavsProjectsAdapter;
import br.com.infoshop.interfaces.IUnfavClick;
import br.com.infoshop.model.Project;
import br.com.infoshop.model.User;
import br.com.infoshop.utils.ItemClickSupport;
import br.com.infoshop.utils.RecyclerItemTouchHelper;
import br.com.infoshop.viewmodel.AuthViewModel;
import br.com.infoshop.viewmodel.ProjectsViewModel;
import dagger.hilt.android.AndroidEntryPoint;

import static br.com.infoshop.utils.Constants.MY_LOG_TAG;

@AndroidEntryPoint
public class ProfileFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, SwipeRefreshLayout.OnRefreshListener, IUnfavClick {
    private TextView nameTv, usernameTv, emailTv, telTv, enderecoTv, totalFavsPrice;
    private RecyclerView recyclerFavs;
    @Inject
    public AuthViewModel authViewModel;
    @Inject
    public ProjectsViewModel projectsViewModel;
    private SwipeRefreshLayout swipeContainer;
    private FavsProjectsAdapter adapterFavsProjects;
    private ConstraintLayout backgroundNoProjects;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(root);
        observeUserInfos();
        observeFavoritesList();
        observeFavoritesValue();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configRecycler();
        configProfile();
        swipeContainer.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        swipeContainer.post(this::onRefresh);


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerFavs);
        ItemClickSupport.addTo(recyclerFavs).setOnItemClickListener((recyclerView, position, v) -> {
            onDetailProject(projectsViewModel.getFavoritesLiveData().getValue().get(position));
        });
    }

    private void onDetailProject(Project project) {
        Intent intent = new Intent(getContext(), ProjectDetailActivity.class);
        intent.putExtra("selectedProject", project);
//        startActivityForResult(intent, INTENT_OPEN_PROJECT_DETAIL);
        startActivity(intent);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK && requestCode == INTENT_OPEN_PROJECT_DETAIL) {
//            Log.d(MY_LOG_TAG, "ACTIVITY RESULT VOLTOU OK");
//        }
//    }

    private void configProfile() {
        User user = authViewModel.getLoggedUserLiveData().getValue();
        if (user != null) {
            nameTv.setText(user.getName());
            usernameTv.setText("Usuário: @" + user.getUsername());
            emailTv.setText("E-mail: " + user.getEmail());
            telTv.setText("Telefone: " + user.getPhone());
            enderecoTv.setText("Endereço: " + user.getAddress());
        }
    }

    private void configRecycler() {
        if (getContext() != null) {
            recyclerFavs.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerFavs.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            recyclerFavs.setItemAnimator(new DefaultItemAnimator());

            adapterFavsProjects = new FavsProjectsAdapter(new ArrayList<>(), getContext(), this);
            adapterFavsProjects.setHasStableIds(true);

            recyclerFavs.setAdapter(adapterFavsProjects);
        } else {
            Log.e(MY_LOG_TAG, "Contexto Nulo");
        }
    }

    private void observeFavoritesValue() {
        projectsViewModel.getTotalFavPriceLiveData().observe(getViewLifecycleOwner(), valor -> {
            totalFavsPrice.setText(valor);
        });
    }

    private void observeFavoritesList() {
        projectsViewModel.getFavoritesLiveData().observe(getViewLifecycleOwner(), projects -> {
            if (projects == null || projects.size() == 0) {
                if (recyclerFavs.getVisibility() == View.VISIBLE)
                    recyclerFavs.setVisibility(View.GONE);
                if (backgroundNoProjects.getVisibility() != View.VISIBLE)
                    backgroundNoProjects.setVisibility(View.VISIBLE);
            } else {
                adapterFavsProjects.updateProjectsList(projects);
                if (recyclerFavs.getVisibility() != View.VISIBLE)
                    recyclerFavs.setVisibility(View.VISIBLE);
                if (backgroundNoProjects.getVisibility() == View.VISIBLE)
                    backgroundNoProjects.setVisibility(View.GONE);
            }
            // setRefreshing(false) to signal refresh has finished
            if (swipeContainer.isRefreshing()) {
                swipeContainer.setRefreshing(false);
            }
            //atualiza preço com base na ultima atualização ouvida
            projectsViewModel.calculateFavoritesTotalPrice();
        });
    }

    //Observador para caso queira implementar real-time do Firebase ou um adicional ex.: editarPerfil()
    //Implementado mas não utilizado pois atualiza a partir de cada entrada no fragment (configProfile())
    private void observeUserInfos() {
        authViewModel.getLoggedUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                nameTv.setText(user.getName());
                usernameTv.setText("Usuário: @" + user.getUsername());
                emailTv.setText("E-mail: " + user.getEmail());
                telTv.setText("Telefone: " + user.getPhone());
                enderecoTv.setText("Endereço: " + user.getAddress());
            } else {
                Toast.makeText(getContext(), "Ocorreu um erro ao recuperar usuário logado!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews(@NotNull View root) {
        nameTv = root.findViewById(R.id.profile_name_tv);
        usernameTv = root.findViewById(R.id.profile_username_tv);
        emailTv = root.findViewById(R.id.profile_email_tv);
        telTv = root.findViewById(R.id.profile_tel_tv);
        enderecoTv = root.findViewById(R.id.profile_endereco_tv);
        recyclerFavs = root.findViewById(R.id.favs_recycler);
        totalFavsPrice = root.findViewById(R.id.fav_total_price);
        swipeContainer = root.findViewById(R.id.layout_swipe_refresh);
        backgroundNoProjects = root.findViewById(R.id.layout_background_home_recycler);
        swipeContainer.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        if (!swipeContainer.isRefreshing()) {
            swipeContainer.setRefreshing(true);
        }
        //busca favoritos
        projectsViewModel.fetchFavorites();
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        Log.d(MY_LOG_TAG, "viewHolder.getAdapterPosition() : " + viewHolder.getAdapterPosition());
        Log.d(MY_LOG_TAG, "POSITION: " + position);
        new AlertDialog.Builder(getContext(), R.style.AlertDialog_Rounded)
                .setTitle(R.string.excluir)
                .setMessage(R.string.voce_tem_certeza_que_deseja_desfavoritar_este_projeto)
                .setNegativeButton(R.string.cancelar, (dialog, which) -> {
//                    adapterProjects.notifyItemChanged(position);
                    adapterFavsProjects.notifyItemRemoved(position);
                    adapterFavsProjects.notifyItemInserted(position);
                })
                .setPositiveButton(R.string.excluir, (dialog, which) -> {
                    try {
                        adapterFavsProjects.notifyItemRemoved(position);
                        Project project = Objects.requireNonNull(projectsViewModel.getFavoritesLiveData().getValue()).get(position);
                        projectsViewModel.removeFavorite(project);
                    } catch (NullPointerException e) {
                        adapterFavsProjects.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
                        Log.e(MY_LOG_TAG, "Lista de projetos nula!");
                    }
                }).setOnDismissListener(dialog -> adapterFavsProjects.notifyItemChanged(position)).show();
    }

    @Override
    public void onUnfav(int position) {
        swipeContainer.setRefreshing(true);
        Project project = Objects.requireNonNull(projectsViewModel.getFavoritesLiveData().getValue()).get(position);
        projectsViewModel.removeFavorite(project);
    }
}