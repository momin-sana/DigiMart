package com.student.digimart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.student.digimart.Models.Users;
import com.student.digimart.Prevalent.Prevalent;

import java.util.Objects;

import io.paperdb.Paper;


public class SigninFragment extends Fragment {

    TextView createAccTV, forgotPassword, googleSignin;
    Button btnSignIn;
    private TextInputEditText email;
    private TextInputEditText password ;
    private TextInputLayout emailTIL, passwordTIL;
    CheckBox rememberMe;
    private final String userDb = "Users";
    private GoogleSignInClient googleSignInClient;
    private GoogleAuthHandler googleAuthHandler;


    public SigninFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ImageView cancel, icon;
                View alertCustomDialog = LayoutInflater.from(requireActivity()).inflate(R.layout.dialogbox_userexists, null);
                Drawable drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.warning);
                icon = alertCustomDialog.findViewById(R.id.dialog_icon);
                icon.setImageDrawable(drawable);
                Button okBtn = alertCustomDialog.findViewById(R.id.sign_in_btn);
                okBtn.setText(R.string.yes);
                TextView alertTV = alertCustomDialog.findViewById(R.id.alert_textview);
                alertTV.setText(R.string.sure_to_exit);
                cancel = alertCustomDialog.findViewById(R.id.cancel_button);


                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setView(alertCustomDialog);
                AlertDialog dialog = builder.create();
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
                dialog.show();

                okBtn.setOnClickListener(v -> {
                    dialog.dismiss();
                    requireActivity().finishAffinity();
                });
                cancel.setOnClickListener(v -> dialog.dismiss());
            }
        });
        googleAuthHandler = new GoogleAuthHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        googleSignin = view.findViewById(R.id.sign_in_with_google_signin);
        btnSignIn = view.findViewById(R.id.signin_btn);
        createAccTV= view.findViewById(R.id.create_account_tv);
        email = view.findViewById(R.id.enter_email_user_signin);
        password = view.findViewById(R.id.enter_password_input_signin);
        emailTIL = view.findViewById(R.id.enterEmailTextInputLayoutSignin);
        passwordTIL = view.findViewById(R.id.enterPasswordInputLayoutSignin);
        rememberMe = view.findViewById(R.id.rememberMeCheckBox);
        Paper.init(requireActivity());

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                emailTIL.setError(null);
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String emailText = Objects.requireNonNull(email.getText()).toString().trim();
                if (TextUtils.isEmpty(emailText)){
                    emailTIL.setError(getString(R.string.required_field));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordTIL.setError(null);
            }
        });

        googleSignin.setOnClickListener(v -> {
            Log.d("SignIn with Google", "SignIn with Google BTN clicked ");
            Intent googleSignInApiSignInIntent = googleSignInClient.getSignInIntent();
            intentActivityResultLauncher.launch(googleSignInApiSignInIntent);

        });

        createAccTV.setOnClickListener(v -> showRegistration());

        btnSignIn.setOnClickListener(v -> validSignIn());
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("47539634118-5ef3e5eqs3bdpgq9a5naao554a02tujg.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions);

        // Set up the ActivityResultLauncher for handling Google Sign-In result
//        intentActivityResultLauncher.launch(new Intent(requireActivity(), Home.class));
    }
    @Override
    public void onResume() {
        super.onResume();
        // Clear email and password fields when the fragment is resumed
        email.setText("");
        password.setText("");
        emailTIL.setError(null);
        passwordTIL.setError(null);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Dismiss loading dialog if it's showing when the fragment is destroyed
        dismissLoadingDialog();
    }
    private void validSignIn() { //check inputs matches data, if yes login, if no show error

        String emailInput = Objects.requireNonNull(email.getText()).toString().trim();
        String passwordInput = Objects.requireNonNull(password.getText()).toString().trim();

        if (TextUtils.isEmpty(emailInput)) {
            errorAnimationShake();
            emailTIL.setError(getString(R.string.required_field));
        } if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            errorAnimationShake();
            emailTIL.setError(getString(R.string.error_msg_invalid_email));
            return;
        }if (TextUtils.isEmpty(passwordInput)){
            errorAnimationShake();
            passwordTIL.setError(getString(R.string.required_field));
        } else {
            checkDataBase(emailInput, passwordInput);
        }
    }

    private void checkDataBase(@NonNull String email, String password) {
        String sanitizedEmail = email.replace('.', '_').replace('#', '_').replace('$', '_').replace('[', '_').replace(']', '_');
        loadingDialog();

        if (rememberMe.isChecked()){
            Paper.book().write(Prevalent.UserEmailKey, email);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dismissLoadingDialog();
                if (snapshot.child(userDb).child(sanitizedEmail).exists()){
                    Users usersData = snapshot.child(userDb).child(sanitizedEmail).getValue(Users.class);
                    assert usersData != null;
                    if (usersData.getEmail().equals(email)){
                        if (usersData.getPassword().equals(password)){
                            dismissLoadingDialog();
                            Intent intent = new Intent(getActivity(), Home.class);
                            startActivity(intent);
                            }else {
                            dismissLoadingDialog();
                            errorAnimationShake();
                            passwordTIL.setError(getString(R.string.error_msg_invalid_password_matching));
                        }
                    }
                } else{
                    dismissLoadingDialog();
                    errorAnimationShake();
                    userDoesntExistsDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dismissLoadingDialog();
                // Handle database error here if needed
                Toast.makeText(getContext(), R.string.database_error_or_network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRegistration(){
        createAccTV.setTextColor(requireActivity().getColor(R.color.dark_dirty_violet));
        createAccTV.setTypeface(Typeface.DEFAULT_BOLD);

        new Handler().postDelayed(() -> {
            SignupFragment newFragment = new SignupFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        },500);
    }
    @SuppressLint("SetTextI18n")
    private void userDoesntExistsDialog() {
        errorAnimationShake();
        String emailText = Objects.requireNonNull(email.getText()).toString().trim();
        ImageView cancel, icon;
        View alertCustomDialog = LayoutInflater.from(requireContext()).inflate(R.layout.dialogbox_userexists, null);
        Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.account_alert);
        Button siginBtn = alertCustomDialog.findViewById(R.id.sign_in_btn);
        siginBtn.setText(R.string.signup);
        icon = alertCustomDialog.findViewById(R.id.dialog_icon);
        icon.setImageDrawable(drawable);
        TextView alertTV = alertCustomDialog.findViewById(R.id.alert_textview);
        alertTV.setText("Account with " + emailText + " does not exist. \n Please Create an Account");
        cancel = alertCustomDialog.findViewById(R.id.cancel_button);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(alertCustomDialog);
        final AlertDialog alertDialog = builder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
        dismissLoadingDialog();

        cancel.setOnClickListener(v -> {
            alertDialog.dismiss();
            email.setText("");
            password.setText("");
            emailTIL.setError("");
            passwordTIL.setError("");
        });

        siginBtn.setOnClickListener(v -> {
            showRegistration();
            alertDialog.dismiss();
            email.setText("");
            password.setText("");
            emailTIL.setError("");
            passwordTIL.setError("");
        });
    }

    private void errorAnimationShake(){
        btnSignIn.setBackgroundColor(requireActivity().getColor(R.color.orange));
        new Handler().postDelayed(() -> btnSignIn.setBackgroundColor(requireActivity().getColor(R.color.dark_dirty_violet)), 1000);
        if (getView() != null) {
            @SuppressLint("ResourceType") Animation shakeAnimation = AnimationUtils.loadAnimation(requireContext(), R.animator.fragment_shake);
            getView().startAnimation(shakeAnimation);
        }
    }
    private void loadingDialog() {
        LoadingDialogFragment loadingDialogFragment = new LoadingDialogFragment();
        loadingDialogFragment.show(getChildFragmentManager(), "loading_dialog");
    }
    private void dismissLoadingDialog() {
        LoadingDialogFragment loadingDialog = (LoadingDialogFragment) getChildFragmentManager().findFragmentByTag("loading_dialog");
        if (loadingDialog != null && loadingDialog.getDialog() != null && loadingDialog.getDialog().isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private final ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK){
                    loadingDialog();
                    Intent data = result.getData();
                    Log.d("ActivityResultLauncher", "result.getResultCode(): " + result.getResultCode() + " \n data :" + data);
                    googleAuthHandler.signInWithGoogle(requireActivity(), data, new GoogleAuthHandler.OnGoogleSignInResultListener() {
                        @Override
                        public void onGoogleSignInSuccess(Users user) {
                            // Handle successful Google Sign-In
                            dismissLoadingDialog();
                            Prevalent.setCurrentOnlineUser(user);
                            Intent intent = new Intent(getActivity(), Home.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onGoogleSignInFailure(Exception e) {
                            // Handle Google Sign-In failure
                            Log.d("TAG", "Google Sign-In failed: " + e.getMessage());
                            Toast.makeText(getActivity(), "Error signing in with Google", Toast.LENGTH_SHORT).show();
                            dismissLoadingDialog();
                        }
                    });
                }
    });


}