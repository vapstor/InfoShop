package br.com.infoshop.ui.login_signup.signup;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import com.google.android.material.textfield.TextInputLayout;

import javax.inject.Inject;

import br.com.infoshop.R;
import br.com.infoshop.activities.MainActivity;
import br.com.infoshop.model.User;
import br.com.infoshop.viewmodel.AuthViewModel;
import br.com.infoshop.viewmodel.SignUpViewModel;
import dagger.hilt.android.AndroidEntryPoint;

import static br.com.infoshop.utils.Constants.MY_LOG_TAG;

@AndroidEntryPoint
public class SignUpFragment extends Fragment {
    @Inject
    protected AuthViewModel authViewModel;
    private SignUpViewModel signupViewModel;
    private NavController navController;
    private ProgressBar signupProgressBar;
    private Button signupButton;
    private TextView linkLogin;
    private EditText passEditText, emailEditText, usernameEditText, nameEditText, addressEditText, phoneEditText;
    private TextInputLayout inputNameLayout, inputUsernameLayout, inputEmailLayout, inputPassLayout, inputAddressLayout, inputPhoneLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        navController.navigateUp();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        signupViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            //Para animação suave do teclado (setar no xml "animateLayoutChanges=true")
            ViewGroup rootView = getActivity().findViewById(R.id.root_signup_scrollview);
            LayoutTransition layoutTransition = rootView.getLayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

            nameEditText = getActivity().findViewById(R.id.input_signup_name);
            usernameEditText = getActivity().findViewById(R.id.input_signup_username);
            emailEditText = getActivity().findViewById(R.id.input_signup_email);
            passEditText = getActivity().findViewById(R.id.input_signup_password);
            addressEditText = getActivity().findViewById(R.id.input_signup_address);
            phoneEditText = getActivity().findViewById(R.id.input_signup_phone);

            linkLogin = getActivity().findViewById(R.id.link_login);
            linkLogin.setOnClickListener(v -> navController.navigateUp());

            //Atualiza estado do modelo a cada letra
            TextWatcher afterTextChangedListener = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // ignore
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // ignore
                }

                @Override
                public void afterTextChanged(Editable s) {
                    signupViewModel.signUpDataChanged(
                            nameEditText.getText().toString(),
                            usernameEditText.getText().toString(),
                            emailEditText.getText().toString(),
                            passEditText.getText().toString(),
                            addressEditText.getText().toString(),
                            phoneEditText.getText().toString()
                    );
                }
            };

            //previous values ?
            setValues(signupViewModel.getSignupFormState().getValue());

            nameEditText.addTextChangedListener(afterTextChangedListener);
            usernameEditText.addTextChangedListener(afterTextChangedListener);
            emailEditText.addTextChangedListener(afterTextChangedListener);
            passEditText.addTextChangedListener(afterTextChangedListener);
            addressEditText.addTextChangedListener(afterTextChangedListener);
            phoneEditText.addTextChangedListener(afterTextChangedListener);

            inputNameLayout = getActivity().findViewById(R.id.layout_input_name);
            inputUsernameLayout = getActivity().findViewById(R.id.layout_input_username);
            inputEmailLayout = getActivity().findViewById(R.id.layout_input_email);
            inputPassLayout = getActivity().findViewById(R.id.layout_input_password);
            inputAddressLayout = getActivity().findViewById(R.id.layout_input_address);
            inputPhoneLayout = getActivity().findViewById(R.id.layout_input_phone);

            //Recebe mudanças no modelo
            signupViewModel.getSignupFormState().observe(getViewLifecycleOwner(), signupFormState -> {
                if (signupFormState == null) {
                    signupButton.setEnabled(false);
                    signupButton.setAlpha((float) 0.70);
                    return;
                }
                //Seta erros na tela
                setErrors(signupFormState);
            });

            authViewModel.getIsLoading().observe(getViewLifecycleOwner(), this::toggleProgressBarVisibility);

            signupButton = getActivity().findViewById(R.id.btn_signup);
            signupProgressBar = getActivity().findViewById(R.id.signup_progress_bar);
            linkLogin = getActivity().findViewById(R.id.link_login);

            //Efetuar cadastro
            signupButton.setOnClickListener(v -> createNewUser());
        }
    }

    private void setErrors(SignUpFormState signupFormState) {
        if (!signupFormState.isDataValid()) {
            signupButton.setEnabled(false);
            signupButton.setAlpha((float) 0.70);
        } else {
            signupButton.setEnabled(true);
            signupButton.setAlpha(1);
        }

        if (signupFormState.getNameError() != null) {
            inputNameLayout.setErrorEnabled(true);
            inputNameLayout.setError(getString(signupFormState.getNameError()));
        } else {
            inputNameLayout.setErrorEnabled(false);
        }

        if (signupFormState.getUsernameError() != null) {
            inputUsernameLayout.setErrorEnabled(true);
            inputUsernameLayout.setError(getString(signupFormState.getUsernameError()));
        } else {
            inputUsernameLayout.setErrorEnabled(false);
        }

        if (signupFormState.getEmailError() != null) {
            inputEmailLayout.setErrorEnabled(true);
            inputEmailLayout.setError(getString(signupFormState.getEmailError()));
        } else {
            inputEmailLayout.setErrorEnabled(false);
        }

        if (signupFormState.getPasswordError() != null) {
            inputPassLayout.setErrorEnabled(true);
            inputPassLayout.setError(getString(signupFormState.getPasswordError()));
        } else {
            inputPassLayout.setErrorEnabled(false);
        }

        if (signupFormState.getAddressError() != null) {
            inputAddressLayout.setErrorEnabled(true);
            inputAddressLayout.setError(getString(signupFormState.getAddressError()));
        } else {
            inputAddressLayout.setErrorEnabled(false);
        }
        if (signupFormState.getPhoneError() != null) {
            inputPhoneLayout.setErrorEnabled(true);
            inputPhoneLayout.setError(getString(signupFormState.getPhoneError()));
        } else {
            inputPhoneLayout.setErrorEnabled(false);
        }
    }

    //verifica se estado anterior é diferente de nulo para preencher campos no input com valores anteriores
    private void setValues(SignUpFormState previousState) {
        if (previousState != null) {
            nameEditText.setText(previousState.getName());
            usernameEditText.setText(previousState.getUsername());
            emailEditText.setText(previousState.getEmail());
            passEditText.setText(previousState.getPassword());
            addressEditText.setText(previousState.getAddress());
            phoneEditText.setText(previousState.getPhone());
        }
    }

    private void toggleProgressBarVisibility(boolean loginProgressBarVisibility) {
        if (getActivity() != null) {
            final ViewGroup root = getActivity().findViewById(R.id.root_signup_scrollview);
            if (root != null) {
                TransitionManager.beginDelayedTransition(root, new Fade());
                if (loginProgressBarVisibility) {
                    signupProgressBar.setVisibility(View.VISIBLE);
                    signupButton.setVisibility(View.INVISIBLE);
                } else {
                    signupButton.setVisibility(View.VISIBLE);
                    signupProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void createNewUser() {
        SignUpFormState signUpFormState = signupViewModel.getSignupFormState().getValue();
        try {
            assert signUpFormState != null;
            User newUser = new User(
                    signUpFormState.getName(),
                    signUpFormState.getUsername(),
                    signUpFormState.getEmail(),
                    signUpFormState.getPassword(),
                    signUpFormState.getAddress(),
                    signUpFormState.getPhone());
            authViewModel.createUser(newUser);
            authViewModel.getLoggedUserLiveData().observe(getViewLifecycleOwner(), user -> {
                authViewModel.setLoading(false);
                if (user == null)
                    return;
                if (user.isCreated().equals("true")) {
                    Toast.makeText(getContext(), "Usuário cadastrado", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else if (!user.isNew().equals("true")) {
                    Toast.makeText(getContext(), "Endereço de e-mail já cadastrado!", Toast.LENGTH_LONG).show();
                }
            });
        } catch (NullPointerException e) {
            Log.e(MY_LOG_TAG, e.getLocalizedMessage());
            Toast.makeText(getContext(), "Ocorreu um erro!", Toast.LENGTH_LONG).show();
        }
    }

}