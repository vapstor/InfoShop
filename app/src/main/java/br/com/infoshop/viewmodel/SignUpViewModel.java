package br.com.infoshop.viewmodel;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import br.com.infoshop.R;
import br.com.infoshop.ui.login_signup.signup.SignUpFormState;

public class SignUpViewModel extends ViewModel {
    private MutableLiveData<SignUpFormState> signUpFormStateLiveData;
    private MutableLiveData<Boolean> isDataValidLiveData;

    public SignUpViewModel() {
        signUpFormStateLiveData = new MutableLiveData<>();
        signUpFormStateLiveData.setValue(null);
        isDataValidLiveData = new MutableLiveData<>();
        isDataValidLiveData.setValue(false);
    }


    public LiveData<SignUpFormState> getSignupFormState() {
        return signUpFormStateLiveData;
    }

    //Atualiza estado do formulario e salva no modelo
    public void signUpDataChanged(String name, String username, String email, String pass, String address, String phone) {
        SignUpFormState signUpFormState;
        if (!isNameValid(name)) {
            signUpFormState = updateSignupFormStateWithUserInfo(new SignUpFormState(R.string.invalid_name, null, null, null, null, null), name, username, email, pass, address, phone);
            signUpFormStateLiveData.setValue(signUpFormState);
        } else if (!isUserNameValid(username)) {
            signUpFormStateLiveData.setValue(
                    updateSignupFormStateWithUserInfo(
                            new SignUpFormState(null, R.string.invalid_email, null, null, null, null),
                            name, username, email, pass, address, phone));
        } else if (!isEmailValid(email)) {
            signUpFormStateLiveData.setValue(
                    updateSignupFormStateWithUserInfo(
                            new SignUpFormState(null, null, R.string.invalid_email, null, null, null),
                            name, username, email, pass, address, phone));
        } else if (!isPasswordValid(pass)) {
            signUpFormStateLiveData.setValue(
                    updateSignupFormStateWithUserInfo(
                            new SignUpFormState(null, null, null, R.string.invalid_password, null, null),
                            name, username, email, pass, address, phone));
        } else if (!isAddressValid(address)) {
            signUpFormStateLiveData.setValue(
                    updateSignupFormStateWithUserInfo(
                            new SignUpFormState(null, null, null, null, R.string.address_invalid, null),
                            name, username, email, pass, address, phone));
        } else if (!isPhoneValid(phone)) {
            signUpFormStateLiveData.setValue(
                    updateSignupFormStateWithUserInfo(
                            new SignUpFormState(null, null, null, null, null, R.string.phone_invalid),
                            name, username, email, pass, address, phone));
        } else {
            signUpFormStateLiveData.setValue(updateSignupFormStateWithUserInfo(new SignUpFormState(true), name, username, email, pass, address, phone));
        }
    }

    public boolean isPhoneValid(String phone) {
        return phone != null && phone.length() > 0;
    }

    public boolean isAddressValid(String address) {
        return address != null && address.length() > 0;
    }

    public SignUpFormState updateSignupFormStateWithUserInfo(SignUpFormState signUpFormState, String name, String username, String email, String pass, String address, String phone) {
        signUpFormState.setName(name);
        signUpFormState.setUsername(username);
        signUpFormState.setEmail(email);
        signUpFormState.setPassword(pass);
        signUpFormState.setAddress(address);
        signUpFormState.setPhone(phone);
        return signUpFormState;
    }

    private boolean isUserNameValid(String username) {
        return username != null && username.length() > 0;
    }

    private boolean isNameValid(String name) {
        return name != null && name.length() > 0;
    }

    // A placeholder email validation check
    public boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        if (email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return !email.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    public boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}