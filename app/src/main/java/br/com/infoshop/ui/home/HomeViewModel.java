package br.com.infoshop.ui.home;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;

import br.com.infoshop.repository.FirebaseRepository;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<String> boasVindasMessageLiveData = new MutableLiveData<>();

    @ViewModelInject
    public HomeViewModel() {
        setBoasVindasMessageLiveData(welcomeMessage(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
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

    protected void setBoasVindasMessageLiveData(String message) {
        boasVindasMessageLiveData.setValue(message);
    }

    public LiveData<String> getBoasVindasMessageLiveData() {
        return boasVindasMessageLiveData;
    }


}