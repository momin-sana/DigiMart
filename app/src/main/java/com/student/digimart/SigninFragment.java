package com.student.digimart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.student.digimart.Models.Users;

import java.util.List;
import java.util.Objects;


public class SigninFragment extends Fragment {

    TextView createAccTV, forgotPassword;
    Button btnSignIn;
    private TextInputEditText email;
    private TextInputEditText password ;
    private TextInputLayout emailTIL, passwordTIL;
    CheckBox rememberMe;
    private String userDb = "Users";


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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        btnSignIn = view.findViewById(R.id.signin_btn);
        createAccTV= view.findViewById(R.id.create_account_tv);
        email = view.findViewById(R.id.enter_email_user_signin);
        password = view.findViewById(R.id.enter_password_input_signin);
        emailTIL = view.findViewById(R.id.enterEmailTextInputLayoutSignin);
        passwordTIL = view.findViewById(R.id.enterPasswordInputLayoutSignin);

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


        createAccTV.setOnClickListener(v -> showRegistration());

        btnSignIn.setOnClickListener(v -> {
           validSignIn();
        });
        return view;
    }

//    abi yeha bas yeh check kiya hai k fields empty nai tou loginsucess() warna error
    private void validSignIn() { //check inputs matches data, if yes login, if no show error

        String emailInput = Objects.requireNonNull(email.getText()).toString().trim();
        String passwordInput = Objects.requireNonNull(password.getText()).toString().trim();

        if (TextUtils.isEmpty(emailInput)) {
            errorAnimationShake();
            emailTIL.setError(getString(R.string.required_field));
        }if (TextUtils.isEmpty(passwordInput)){
            errorAnimationShake();
            passwordTIL.setError(getString(R.string.required_field));
        } else {
            checkDataBase(emailInput, passwordInput);
        }
    }

    private void checkDataBase(String email, String password) {

        String sanitizedEmail = email.replace('.', '_').replace('#', '_').replace('$', '_').replace('[', '_').replace(']', '_');

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userDb).child(sanitizedEmail).exists()){
//                    TODO add progress bar with a message

                    Users usersData = snapshot.child(userDb).child(sanitizedEmail).getValue(Users.class);
                    assert usersData != null;
                    if (usersData.getEmail().equals(email)){
                        if (usersData.getPassword().equals(password)){
                            Intent intent = new Intent(getActivity(), Home.class);
                            startActivity(intent);
                            }else {
                            errorAnimationShake();
                            passwordTIL.setError(getString(R.string.error_msg_invalid_password_matching));
                        }
                    }
                }else{
                    errorAnimationShake();
                    userDoesntExistsDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        View alertCustomDialog = LayoutInflater.from(requireActivity()).inflate(R.layout.dialogbox_userexists, null);
        Drawable drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.account_alert2);
        Button siginBtn = alertCustomDialog.findViewById(R.id.sign_in_btn);
        siginBtn.setText(R.string.signup);
        icon = alertCustomDialog.findViewById(R.id.dialog_icon);
        icon.setImageDrawable(drawable);
        TextView alertTV = alertCustomDialog.findViewById(R.id.alert_textview);
        alertTV.setText("Account with " + emailText + " does not exist. \n Please Create an Account");
        cancel = alertCustomDialog.findViewById(R.id.cancel_button);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(alertCustomDialog);
        final AlertDialog alertDialog = builder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
        cancel.setOnClickListener(v -> {
            alertDialog.dismiss();
            email.setText("");
            password.setText("");
            emailTIL.setError("");
            passwordTIL.setError("");
        });

        siginBtn.setOnClickListener(v -> {
            email.setText("");
            password.setText("");
            emailTIL.setError("");
            passwordTIL.setError("");
            showRegistration();
            alertDialog.dismiss();
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
}