package br.com.infoshop.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Calendar;

import br.com.infoshop.model.Project;

import static br.com.infoshop.utils.Util.MY_LOG_TAG;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> boasVindasMessageLiveData;
    private MutableLiveData<ArrayList<Project>> projectsLiveData;

    public HomeViewModel() {
        boasVindasMessageLiveData = new MutableLiveData<>();
        projectsLiveData = new MutableLiveData<>();
        setBoasVindasMessageLiveData(welcomeMessage(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
        setProjectsLiveData(new ArrayList<>());
    }

    private String welcomeMessage(int hour) {
        if (hour >= 6 && hour <= 12) {
            return "Bom dia, ";
        } else if (hour > 12 && hour <= 18) {
            return "Boa tarde, ";
        } else {
            return "Boa noite, ";
        }
    }

    public LiveData<String> getBoasVindasMessageLiveData() {
        return boasVindasMessageLiveData;
    }

    protected void setBoasVindasMessageLiveData(String message) {
        boasVindasMessageLiveData.setValue(message);
    }

    public LiveData<ArrayList<Project>> getProjectsLiveData() {
        return projectsLiveData;
    }

    public void setProjectsLiveData(ArrayList<Project> projects) {
        projectsLiveData.setValue(projects);
    }

    public void removeProject(Project project) {
        ArrayList<Project> projects = projectsLiveData.getValue();
        if (projects != null && projects.size() > 0) {
            projects.remove(project);
            setProjectsLiveData(projects);
        } else {
            Log.e(MY_LOG_TAG, "ERRO: Projeto não está na lista!");
        }
    }
}