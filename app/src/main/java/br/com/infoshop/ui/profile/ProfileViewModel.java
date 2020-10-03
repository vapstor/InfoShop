package br.com.infoshop.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<String> boasVindasMessage;


    public ProfileViewModel() {
        boasVindasMessage = new MutableLiveData<>();
        setBoasVindasMessage(selectRightMessage(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
    }

    private String selectRightMessage(int hour) {
        if(hour >=6 && hour <= 12) {
            return "Bom dia";
        } else if(hour > 12 && hour <= 18) {
            return "Boa tarde";
        } else {
            return "Boa noite";
        }
    }

    public LiveData<String> getBoasVindasMessage() {
        return boasVindasMessage;
    }

    protected void setBoasVindasMessage(String message) {
        boasVindasMessage.setValue(message);
    }
}