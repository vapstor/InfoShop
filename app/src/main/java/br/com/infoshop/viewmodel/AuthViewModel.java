package br.com.infoshop.viewmodel;


import android.app.Application;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;
import javax.inject.Singleton;

import br.com.infoshop.model.User;
import br.com.infoshop.repository.FirebaseRepository;

@Singleton
public class AuthViewModel extends AndroidViewModel {
    private FirebaseRepository authRepository;
    public LiveData<User> createdUserLiveData;

    @ViewModelInject
    @Inject
    public AuthViewModel(Application application) {
        super(application);
        this.authRepository = new FirebaseRepository();
        this.loggedUserLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<User> loggedUserLiveData;

    public MutableLiveData<User> getLoggedUserLiveData() {
        return loggedUserLiveData;
    }

    MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<User> getCreatedUserLiveData() {
        return createdUserLiveData;
    }

    public void createUser(User user) {
        setLoading(true);
        createdUserLiveData = authRepository.firebaseSignUp(user);
    }

    public void login(String username, String password) {
        setLoading(true);
        loggedUserLiveData = authRepository.firebaseLogin(username, password);
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void logout() {
        authRepository.auth.signOut();
        loggedUserLiveData.setValue(null);
    }
}