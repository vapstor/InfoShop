package br.com.infoshop.ui.projects;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import br.com.infoshop.model.Categorie;

public class ProjectsViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Categorie>> mCategories;
    private MutableLiveData<ArrayList<Integer>> mFavorites;

    public ProjectsViewModel() {
        mCategories = new MutableLiveData<>();
        mFavorites = new MutableLiveData<>();
        setCategories(new ArrayList<>());
        setFavorites(new ArrayList<>());
    }

    public MutableLiveData<ArrayList<Categorie>> getCategoriesLiveData() {
        return mCategories;
    }

    public void setCategories(ArrayList<Categorie> categories) {
        getCategoriesLiveData().setValue(categories);
    }

    public MutableLiveData<ArrayList<Integer>> getFavoritesLiveData() {
        return mFavorites;
    }
    public void setFavorites(ArrayList<Integer> favorites) {
        getFavoritesLiveData().setValue(favorites);
    }
    public void addFavorite(int index) {
        ArrayList<Integer> a = getFavoritesLiveData().getValue();
        a.add(index);
        getFavoritesLiveData().setValue(a);
    }
}