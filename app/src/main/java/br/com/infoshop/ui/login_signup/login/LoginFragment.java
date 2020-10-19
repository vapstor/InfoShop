package br.com.infoshop.ui.login_signup.login;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import com.google.android.material.textfield.TextInputLayout;

import br.com.infoshop.R;
import br.com.infoshop.activities.MainActivity;
import br.com.infoshop.auth.AuthViewModel;

public class LoginFragment extends Fragment {
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private ProgressBar loginProgressBar;
    private LoginViewModel loginViewModel;
    private TextView linkSignup;
    private NavController navController;
    private AuthViewModel authViewModel;
    private TextInputLayout usernameLayout, passwordLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loginViewModel = new ViewModelProvider(requireParentFragment()).get(LoginViewModel.class);
        authViewModel = new ViewModelProvider(requireParentFragment()).get(AuthViewModel.class);
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar supportActionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar.hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ActionBar supportActionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar.show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            //Para animação suave do teclado (setar no xml "animateLayoutChanges=true")
            ViewGroup rootView = getActivity().findViewById(R.id.root_login_scrollview);
            LayoutTransition layoutTransition = rootView.getLayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

            passwordEditText = getActivity().findViewById(R.id.input_password);
            usernameEditText = getActivity().findViewById(R.id.input_email);

            usernameLayout = getActivity().findViewById(R.id.login_username_layout);
            passwordLayout = getActivity().findViewById(R.id.login_password_layout);

            loginButton = getActivity().findViewById(R.id.btn_login);
            loginProgressBar = getActivity().findViewById(R.id.login_progress_bar);
            linkSignup = getActivity().findViewById(R.id.link_signup);

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
                    loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
            };
            usernameEditText.addTextChangedListener(afterTextChangedListener);
            passwordEditText.addTextChangedListener(afterTextChangedListener);
            passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    authViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                }
                return false;
            });

            loginButton.setOnClickListener(v -> {
                authViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                authViewModel.getLoggedUserLiveData().observe(getViewLifecycleOwner(), user -> {
                    authViewModel.setLoading(false);
                    if (user != null) {
                        Toast.makeText(getContext(), "Bem vindo, " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    } else {
                        Toast.makeText(getContext(), "Falha ao logar!", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            linkSignup.setOnClickListener(v -> navController.navigate(R.id.action_loginFragment_to_signUp));

            loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), loginFormState -> {
                if (loginFormState == null) {
                    loginButton.setEnabled(false);
                    loginButton.setAlpha((float) 0.70);
                    return;
                }
                if (!loginFormState.isDataValid()) {
                    loginButton.setEnabled(false);
                    loginButton.setAlpha((float) 0.70);
                } else {
                    loginButton.setEnabled(true);
                    loginButton.setAlpha(1);
                }

                if (loginFormState.getUsernameError() != null) {
                    usernameLayout.setErrorEnabled(true);
                    usernameLayout.setError(getString(loginFormState.getUsernameError()));
                } else {
                    usernameLayout.setErrorEnabled(false);
                }

                if (loginFormState.getPasswordError() != null) {
                    passwordLayout.setErrorEnabled(true);
                    passwordLayout.setError(getString(loginFormState.getPasswordError()));
                } else {
                    passwordLayout.setErrorEnabled(false);
                }
            });
            authViewModel.getIsLoading().observe(getViewLifecycleOwner(), this::toggleProgressBarVisibility);
        }
    }

    private void toggleProgressBarVisibility(boolean loginProgressBarVisibility) {
        if (getActivity() != null) {
            final ViewGroup root = getActivity().findViewById(R.id.root_login_scrollview);
            if (root != null) {
                TransitionManager.beginDelayedTransition(root, new Fade());
                if (loginProgressBarVisibility) {
                    loginProgressBar.setVisibility(View.VISIBLE);
                    loginButton.setVisibility(View.INVISIBLE);
                } else {
                    loginButton.setVisibility(View.VISIBLE);
                    loginProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

}