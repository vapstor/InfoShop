package br.com.infoshop.auth;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;

import br.com.infoshop.model.User;
import br.com.infoshop.repository.FirebaseRepository;

public class AuthViewModel extends AndroidViewModel {
    private FirebaseRepository authRepository;
    LiveData<User> createdUserLiveData;
    MutableLiveData<FirebaseUser> loggedUserLiveData;

    public MutableLiveData<FirebaseUser> getLoggedUserLiveData() {
        return loggedUserLiveData;
    }

    MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<User> getCreatedUserLiveData() {
        return createdUserLiveData;
    }

    public AuthViewModel(Application application) {
        super(application);
        this.authRepository = new FirebaseRepository();
        this.loggedUserLiveData = new MutableLiveData<>();
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