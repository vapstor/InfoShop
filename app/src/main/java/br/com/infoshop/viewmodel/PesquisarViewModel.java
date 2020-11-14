package br.com.infoshop.viewmodel;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import br.com.infoshop.model.Project;

public class PesquisarViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<Project>> allSearchedProjectsLiveData = new MutableLiveData<>();
    private MutableLiveData<String> queryLiveData = new MutableLiveData<>();

    @ViewModelInject
    public PesquisarViewModel() {
        setNewQuery("");
        updateProductsList(new ArrayList<>());
    }

    public MutableLiveData<String> getQueryLiveData() {
        return queryLiveData;
    }

    public MutableLiveData<ArrayList<Project>> getAllSearchedProjectsLiveData() {
        return allSearchedProjectsLiveData;
    }

    public void setNewQuery(String query) {
        queryLiveData.setValue(query);
    }

    public void updateProductsList(ArrayList<Project> newList) {
        allSearchedProjectsLiveData.setValue(newList);
    }
}