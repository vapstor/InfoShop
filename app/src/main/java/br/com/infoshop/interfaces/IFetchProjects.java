package br.com.infoshop.interfaces;

import java.util.ArrayList;

import br.com.infoshop.model.Project;

public interface IFetchProjects {
    public void onGetDataDone(ArrayList<Project> projectsList);
}
