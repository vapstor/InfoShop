package br.com.infoshop.ui.login_signup.signup;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class SignUpFormState {

    @Nullable
    private Integer nameError, usernameError, emailError, passwordError, addressError, phoneError;
    private String name, username, email, password, address, phone;
    private boolean isDataValid;


    SignUpFormState(@Nullable Integer nameError, @Nullable Integer usernameError, @Nullable Integer emailError, @Nullable Integer passwordError, @Nullable Integer addressError, @Nullable Integer phoneError) {
        this.nameError = nameError;
        this.usernameError = usernameError;
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.addressError = addressError;
        this.phoneError = phoneError;
        this.isDataValid = false;
    }

    SignUpFormState(boolean isDataValid) {
        this.nameError = null;
        this.usernameError = null;
        this.emailError = null;
        this.passwordError = null;
        this.addressError = null;
        this.phoneError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getNameError() {
        return nameError;
    }

    @Nullable
    Integer getEmailError() {
        return emailError;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    Integer getAddressError() {
        return addressError;
    }

    @Nullable
    public Integer getPhoneError() {
        return phoneError;
    }

    boolean isDataValid() {
        return isDataValid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}
