package br.com.infoshop.ui.projects;

import android.util.Log;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import br.com.infoshop.model.Categorie;
import br.com.infoshop.model.Project;
import br.com.infoshop.repository.FirebaseRepository;

import static br.com.infoshop.utils.Constants.MY_LOG_TAG;


public class ProjectsViewModel extends ViewModel {

    private FirebaseRepository repository;
    private MutableLiveData<ArrayList<Categorie>> mCategories = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Project>> mProjects = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Project>> mFavorites = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsFavorite = new MutableLiveData<>(false);

    @ViewModelInject
    public ProjectsViewModel(FirebaseRepository repository) {
        this.repository = repository;
    }

    public void fetchCategories() {
        ArrayList<Categorie> categories = new ArrayList<>();
        this.repository.fetchProjectsCategories()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Categorie categorie = new Categorie(
                                    Integer.parseInt(String.valueOf(document.get("id"))),
                                    String.valueOf(document.get("title")),
                                    String.valueOf(document.get("background_src")));
                            categories.add(categorie);
                            Log.d(MY_LOG_TAG, document.getId() + " => " + document.getData());
                        }
                        setCategories(categories);
                    } else {
                        setCategories(null);
                        Log.w(MY_LOG_TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    public void setCategories(ArrayList<Categorie> categories) {
        mCategories.setValue(categories);
    }

    public MutableLiveData<ArrayList<Categorie>> getCategories() {
        return mCategories;
    }

    public void fetchProjects(int pageLimit) {
        ArrayList<Project> projects;
        int idLastItemFetched;

        if (getAllProjects().getValue() != null) {
            projects = getAllProjects().getValue();
            idLastItemFetched = projects.get(projects.size() - 1).getId();
        } else {
            projects = new ArrayList<>();
            idLastItemFetched = 0;
        }

        this.repository.fetchProjects(pageLimit, idLastItemFetched).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Project projeto = document.toObject(Project.class);
//                            Project projeto = new Project(
//                                    Integer.parseInt(String.valueOf(document.get("id"))),
//                                    Integer.parseInt(String.valueOf(document.get("id_categoria"))),
//                                    String.valueOf(document.get("titulo")),
//                                    String.valueOf(document.get("descricao")),
//                                    Double.parseDouble(String.valueOf(document.get("preco"))),
//                                    String.valueOf(document.get("imagem")));
                            projects.add(projeto);
                            Log.d(MY_LOG_TAG, document.getId() + " => " + document.getData());
                        }
                        setAllProjects(projects);
                    } else {
                        setAllProjects(null);
                        Log.w(MY_LOG_TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    private void setAllProjects(ArrayList<Project> projects) {
        mProjects.setValue(projects);
    }

    public MutableLiveData<ArrayList<Project>> getAllProjects() {
        return mProjects;
    }

    public void setFavorites(ArrayList<Project> favorites) {
        getFavoritesLiveData().setValue(favorites);
    }

    public MutableLiveData<ArrayList<Project>> getFavoritesLiveData() {
        return mFavorites;
    }

    public void addFavorite(Project project) throws IllegalStateException {
        ArrayList<Project> favorites = getFavoritesLiveData().getValue();
        if (favorites != null) {
            if (!favorites.contains(project)) {
                addFavoriteToFirebase(project);
                favorites.add(project);
                getFavoritesLiveData().setValue(favorites);
            } else {
                Log.e(MY_LOG_TAG, "Lista de projetos favoritos já contém o projeto especificado");
            }
        } else {
            throw new IllegalStateException("Lista de favoritos nula!.");
        }
    }

    public void clearProjects() {
        if (getAllProjects().getValue() != null) {
            getAllProjects().getValue().clear();
        }
    }

    private void addFavoriteToFirebase(Project project) {
        this.repository.addToFavorites(project).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                throw new IllegalStateException("Falhou ao inserir", task.getException());
            }
        });
    }

    public void removeFavorite(Project project) throws IllegalStateException {
        ArrayList<Project> favorites = getFavoritesLiveData().getValue();
        if (favorites != null && favorites.contains(project)) {
            removeFromFirebase(project);
            favorites.remove(project);
            getFavoritesLiveData().setValue(favorites);
        } else {
            Log.e(MY_LOG_TAG, "Lista de projetos favoritos nula OU não contém o projeto especificado");
        }
    }

    private void removeFromFirebase(Project project) {
        this.repository.removeFromFavorites(project).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                throw new IllegalStateException("Falhou ao excluir");
            }
        });
    }

    public void fetchFavorites() {
        ArrayList<Project> favorites = new ArrayList<>();
        this.repository.fetchFavorites().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Project projeto = document.toObject(Project.class);
//                            Project projeto = new Project(
//                                    Integer.parseInt(String.valueOf(document.get("id"))),
//                                    Integer.parseInt(String.valueOf(document.get("id_categoria"))),
//                                    String.valueOf(document.get("titulo")),
//                                    String.valueOf(document.get("descricao")),
//                                    Double.parseDouble(String.valueOf(document.get("preco"))),
//                                    String.valueOf(document.get("imagem")));
                            favorites.add(projeto);
                            Log.d(MY_LOG_TAG, document.getId() + " => " + document.getData());
                        }
                        setFavorites(favorites);
                    } else {
                        throw new IllegalStateException("Error getting documents.", task.getException());
                    }
                });
    }

    public void checkIsFavorite(Project project) {
        ArrayList<Project> favorites = getFavoritesLiveData().getValue();
        if (favorites != null) {
            if (favorites.contains(project)) {
                getProjectIsFavoriteLiveData().setValue(true);
            } else {
                getProjectIsFavoriteLiveData().setValue(false);
            }
        } else {
            throw new IllegalStateException("Lista de favoritos nula!");
        }
    }

    public MutableLiveData<Boolean> getProjectIsFavoriteLiveData() {
        return mIsFavorite;
    }


    public void removeProject(Project project) {
        ArrayList<Project> projects = getAllProjects().getValue();
        if (projects != null && projects.size() > 0) {
            projects.remove(project);
            setAllProjects(projects);
        } else {
            Log.e(MY_LOG_TAG, "ERRO: Projeto não está na lista!");
        }
    }
}