package br.com.infoshop.viewmodel;

import android.util.Log;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import br.com.infoshop.model.Categorie;
import br.com.infoshop.model.Project;
import br.com.infoshop.repository.FirebaseRepository;

import static br.com.infoshop.utils.Constants.MY_LOG_TAG;

@Singleton
public class ProjectsViewModel extends ViewModel {

    private FirebaseRepository repository;
    private MutableLiveData<ArrayList<Categorie>> mCategories = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Project>> mProjects = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Project>> mFilteredProjects = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Project>> mFavorites = new MutableLiveData<>();
    private MutableLiveData<Boolean> isFavorite = new MutableLiveData<>();

    @ViewModelInject
    @Inject
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

    public void fetchProjectsByQuery(String query, int pageLimit) {
        ArrayList<Project> filteredProjects;
        int idLastItemFetched;

        if (getFilteredProjects().getValue() != null) {
            filteredProjects = getFilteredProjects().getValue();
            idLastItemFetched = filteredProjects.get(filteredProjects.size() - 1).getId();
        } else {
            filteredProjects = new ArrayList<>();
            idLastItemFetched = 0;
        }

        //Primeiro filtra por titulo
        this.repository.fetchProjectsByTitle(query, pageLimit, idLastItemFetched).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot obj = task.getResult();
                        for (QueryDocumentSnapshot document : obj) {
                            Project projeto = document.toObject(Project.class);
                            filteredProjects.add(projeto);
                            Log.d(MY_LOG_TAG, document.getId() + " => " + document.getData());
                        }
                        setFilteredProjects(filteredProjects);
                    } else {
                        setFilteredProjects(null);
                        Log.w(MY_LOG_TAG, "Error getting documents.", task.getException());
                    }
                });
        //depois filtra por descricao
        this.repository.fetchProjectsByDesc(query, pageLimit, idLastItemFetched)
                .addOnCompleteListener(otherTask -> {
                    if (otherTask.isSuccessful()) {
                        for (QueryDocumentSnapshot document : otherTask.getResult()) {
                            Project projeto = document.toObject(Project.class);
                            filteredProjects.add(projeto);
                            Log.d(MY_LOG_TAG, document.getId() + " => " + document.getData());
                        }
                        setFilteredProjects(filteredProjects);
                    } else {
                        setFilteredProjects(null);
                        Log.w(MY_LOG_TAG, "Error getting documents.", otherTask.getException());
                    }
                });
    }

    public void setAllProjects(ArrayList<Project> projects) {
        mProjects.setValue(projects);
    }

    public void setFilteredProjects(ArrayList<Project> projects) {
        mFilteredProjects.setValue(projects);
    }

    public MutableLiveData<ArrayList<Project>> getAllProjects() {
        return mProjects;
    }

    public MutableLiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }
    public void setIsfavorite(boolean val) {
        isFavorite.setValue(val);
    }

    public MutableLiveData<ArrayList<Project>> getFilteredProjects() {
        return mFilteredProjects;
    }

    public MutableLiveData<ArrayList<Project>> getFavoritesLiveData() {
        return mFavorites;
    }

    public void setFavorites(ArrayList<Project> favorites) {
        mFavorites.setValue(favorites);
    }

    public void addFavorite(Project project) throws IllegalStateException {
        ArrayList<Project> favorites = getFavoritesLiveData().getValue();
        if (favorites != null) {
            boolean contain = false;
            for (int i = 0; i < favorites.size(); i++) {
                if (favorites.get(i).getId() == project.getId()) {
                    contain = true;
                }
            }
            if (!contain) {
                this.repository.addToFavorites(project).addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        throw new IllegalStateException("Falhou ao inserir", task.getException());
                    }
                });
                favorites.add(project);
                setFavorites(favorites);
            } else {
                Log.e(MY_LOG_TAG, "Lista de projetos favoritos já contém o projeto especificado");
                throw new IllegalStateException("Este projeto já favoritado!");
            }
        } else {
            throw new IllegalStateException("Lista de favoritos nula!.");
        }
    }

    public void removeProject(Project project) {
        ArrayList<Project> projetos = getAllProjects().getValue();
        if (projetos != null) {
            this.repository.removeFromAllProjects(project).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    throw new IllegalStateException("Falhou ao excluir");
                } else {
                    for (int i = 0; i < projetos.size(); i++) {
                        Project p = projetos.get(i);
                        if (p.getId() == project.getId()) {
                            projetos.remove(p);
                            setAllProjects(projetos);
                        }
                    }
                }
            });
        } else {
            Log.e(MY_LOG_TAG, "Lista de Projetos Nula!");
            throw new IllegalStateException("Lista de Projetos Nula!");
        }
    }

    public void removeFavorite(Project project) throws IllegalStateException {
        ArrayList<Project> favorites = getFavoritesLiveData().getValue();
        if (favorites != null) {
            for (int i = 0; i < favorites.size(); i++) {
                if (favorites.get(i).getId() == project.getId()) {
                    final int finalI = i;
                    this.repository.removeFromFavorites(project).addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            throw new IllegalStateException("Falhou ao excluir");
                        } else {
                            favorites.remove(favorites.get(finalI));
                            setFavorites(favorites);
                        }
                    });
                    break;
                }
            }
        } else {
            Log.e(MY_LOG_TAG, "Lista de projetos favoritos nula OU não contém o projeto especificado");
        }
    }

    public void fetchFavorites() {
        ArrayList<Project> favorites = new ArrayList<>();
        this.repository.fetchFavorites().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Project projeto = document.toObject(Project.class);
                            favorites.add(projeto);
                            Log.d(MY_LOG_TAG, document.getId() + " => " + document.getData());
                        }
                        setFavorites(favorites);
                    } else {
                        throw new IllegalStateException("Error getting documents.", task.getException());
                    }
                });
    }


}