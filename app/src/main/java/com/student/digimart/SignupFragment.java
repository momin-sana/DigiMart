package com.student.digimart;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class SignupFragment extends Fragment {
    private TextView haveAcc;
    private Button signupBtn;
    private TextInputEditText createUsername, email, createPassword, confirmPassword, phoneNo;
    private TextInputLayout emailTextInputLayout, usernameTextInputLayout, passwordTextInputLayout, confirmPasswordTextInputLayout, phoneNoInputLayout;


    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        haveAcc = view.findViewById(R.id.have_acc_tv);
        haveAcc.setOnClickListener(v -> showSignIn());

        createUsername = view.findViewById(R.id.create_username_input_signup);
        email = view.findViewById(R.id.create_email_input_signup);
        createPassword = view.findViewById(R.id.create_password_input_signup);
        confirmPassword = view.findViewById(R.id.confirm_password_input_signup);
        phoneNo = view.findViewById(R.id.enter_phone_no_input_signup);

        emailTextInputLayout = view.findViewById(R.id.createEmailTextInputLayout);
        usernameTextInputLayout = view.findViewById(R.id.createUsernameTextInputLayout);
        passwordTextInputLayout = view.findViewById(R.id.createPasswordTextInputLayout);
        confirmPasswordTextInputLayout = view.findViewById(R.id.confirmPasswordTextInputLayout);
        phoneNoInputLayout = view.findViewById(R.id.enterPhoneNoInputLayout);


        createUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidUsername(String.valueOf(createUsername))){
                    email.setEnabled(true);
                    emailTextInputLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.bright_violet, requireActivity().getTheme())));
                    usernameTextInputLayout.setError(null);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > usernameTextInputLayout.getCounterMaxLength())
                    usernameTextInputLayout.setError(getString(R.string.error_msg_invalid_username));
                else
                    usernameTextInputLayout.setError(null);
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String usernameText = Objects.requireNonNull(createUsername.getText()).toString().trim();
                if (TextUtils.isEmpty(usernameText)){
                    usernameTextInputLayout.setError(getString(R.string.required_field));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                emailTextInputLayout.setError(null);

            }
        });

        phoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String usernameText = Objects.requireNonNull(createUsername.getText()).toString().trim();
                String emailText = Objects.requireNonNull(email.getText()).toString().trim();
                if (TextUtils.isEmpty(usernameText)){
                    usernameTextInputLayout.setError(getString(R.string.required_field));
                }if (TextUtils.isEmpty(emailText)){
                    emailTextInputLayout.setError(getString(R.string.required_field));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                phoneNoInputLayout.setError(null);
            }
        });

        createPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String usernameText = Objects.requireNonNull(createUsername.getText()).toString().trim();
                String emailText = Objects.requireNonNull(email.getText()).toString().trim();
                String phoneNoText = Objects.requireNonNull(phoneNo.getText()).toString().trim();

                if (TextUtils.isEmpty(usernameText)){
                    usernameTextInputLayout.setError(getString(R.string.required_field));
                }if (TextUtils.isEmpty(emailText)){
                    emailTextInputLayout.setError(getString(R.string.required_field));
                }if (TextUtils.isEmpty(phoneNoText)){
                    phoneNoInputLayout.setError(getString(R.string.required_field));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordTextInputLayout.setError(null);
            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String usernameText = Objects.requireNonNull(createUsername.getText()).toString().trim();
                String emailText = Objects.requireNonNull(email.getText()).toString().trim();
                String phoneNoText = Objects.requireNonNull(phoneNo.getText()).toString().trim();
                String passwordText = Objects.requireNonNull(createPassword.getText()).toString().trim();

                if (TextUtils.isEmpty(usernameText)){
                    usernameTextInputLayout.setError(getString(R.string.required_field));
                }if (TextUtils.isEmpty(emailText)){
                    emailTextInputLayout.setError(getString(R.string.required_field));
                }if (TextUtils.isEmpty(phoneNoText)){
                    phoneNoInputLayout.setError(getString(R.string.required_field));
                }if (TextUtils.isEmpty(passwordText)){
                    passwordTextInputLayout.setError(getString(R.string.required_field));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                confirmPasswordTextInputLayout.setError(null);
            }
        });


        signupBtn = view.findViewById(R.id.signup_btn);
        signupBtn.setOnClickListener(v -> {
          validateFields();
        });

        return view;
    }

    private void showSignIn(){
        haveAcc.setTextColor(requireActivity().getColor(R.color.dark_dirty_violet));
        haveAcc.setTypeface(Typeface.DEFAULT_BOLD);

        new Handler().postDelayed(() -> {
            SigninFragment newFragment = new SigninFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        },500);
    }

    private void validateFields() {
        String usernameInput = createUsername.getText().toString().trim();
        String emailInput = email.getText().toString().trim();
        String phoneNoInput = phoneNo.getText().toString().trim();
        String passwordInput = createPassword.getText().toString().trim();
        String confirmInput = confirmPassword.getText().toString().trim();


        if (!TextUtils.isEmpty(usernameInput) || !TextUtils.isEmpty(emailInput) || !TextUtils.isEmpty(phoneNoInput) || !TextUtils.isEmpty(passwordInput) || !TextUtils.isEmpty(confirmInput)){
            if (isValidUsername(usernameInput)){
                if (Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
                    if (isValidPhoneNumber(phoneNoInput)){
                        if (isValidPassword(passwordInput)){
                            if (confirmInput.equals(passwordInput)){
//                                TODO loading bar
//                                when all input validation are correct, check if user already exist or not. if not exist create new account.
                                validateAccount(usernameInput, emailInput, phoneNoInput, confirmInput);
                            }else{
                                confirmPasswordTextInputLayout.setError(getString(R.string.error_msg_invalid_password_matching));
                            }
                        }else{
                            passwordTextInputLayout.setError(getString(R.string.error_msg_invalid_password_created));
                        }
                    }else {
                        phoneNoInputLayout.setError(getString(R.string.error_msg_invalid_phoneNo));
                    }
                }else{
                    emailTextInputLayout.setError(getString(R.string.error_msg_invalid_email));
                }
            }else {
                usernameTextInputLayout.setError(getString(R.string.error_msg_invalid_username));
            }
        }else {

            usernameTextInputLayout.setError(getString(R.string.required_field));
            emailTextInputLayout.setError(getString(R.string.required_field));
            phoneNoInputLayout.setError(getString(R.string.required_field));
            passwordTextInputLayout.setError(getString(R.string.required_field));
            confirmPasswordTextInputLayout.setError(getString(R.string.required_field));

            // change the color
            signupBtn.setBackgroundColor(requireActivity().getColor(R.color.orange));
            new Handler().postDelayed(() -> signupBtn.setBackgroundColor(requireActivity().getColor(R.color.dark_dirty_violet)), 1000);
        }

    }

    private void validateAccount(String usernameVA, String emailVA, String phoneNumberVA, String cPasswordVA) {
//        Check if user exists or not
        // Sanitize the email to create a valid database key
        String sanitizedEmail = emailVA.replace('.', '_').replace('#', '_').replace('$', '_').replace('[', '_').replace(']', '_');

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        // Check if the username exists
        databaseReference.child("Users").orderByChild("username").equalTo(usernameVA)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot usernameSnapshot) {
                        if (usernameSnapshot.exists()) {
                            // Username is already in use
                            userAlreadyExistsDialog();
                            Toast.makeText(getContext(), R.string.username_already_used, Toast.LENGTH_SHORT).show();
                            usernameTextInputLayout.setError(getString(R.string.username_already_used));
                        } else {
                            // Username is not in use, check email
                            databaseReference.child("Users").child(sanitizedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot emailSnapshot) {
                                    if (emailSnapshot.exists()) {
                                        // Email is already in use
                                        userAlreadyExistsDialog();
                                    } else {
                                        // Neither username nor email is in use, proceed with account creation
                                        HashMap<String, Object> userDataMap = new HashMap<>();
                                        userDataMap.put("username", usernameVA);
                                        userDataMap.put("email", emailVA);
                                        userDataMap.put("phone", phoneNumberVA);
                                        userDataMap.put("password", cPasswordVA);

                                        databaseReference.child("Users").child(sanitizedEmail).updateChildren(userDataMap)
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getContext(), R.string.account_successfully_created, Toast.LENGTH_SHORT).show();
                                                        showSignIn();
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError emailError) {
                                    // Handle database error here if needed
                                    Toast.makeText(getContext(), R.string.database_error_or_network_error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError usernameError) {
                        // Handle database error here if needed
                        Toast.makeText(getContext(), R.string.database_error_or_network_error, Toast.LENGTH_SHORT).show();
                    }
                });

        signupBtn.setBackgroundColor(requireActivity().getColor(R.color.dark_purple));

    }

    private boolean isValidUsername(String username) {
        String usernameREX ="^[a-zA-Z0-9]{10,15}$";
        return username.matches(usernameREX);
    }
    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneNoREX ="^\\+92[1-9][0-9]{9}$";
        return phoneNumber.matches(phoneNoREX);
    }
    private boolean isValidPassword(String password) {
        String passwordREX ="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
           return password.matches(passwordREX);

    }

    private void userAlreadyExistsDialog() {
        ImageView cancel, icon;
        View alertCustomDialog = LayoutInflater.from(requireActivity()).inflate(R.layout.dialogbox_userexists, null);
        Drawable drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.account_alert2);
        Button siginBtn = alertCustomDialog.findViewById(R.id.sign_in_btn);
        siginBtn.setText(R.string.signin);
        icon = alertCustomDialog.findViewById(R.id.dialog_icon);
        icon.setImageDrawable(drawable);
        TextView alertTV = alertCustomDialog.findViewById(R.id.alert_textview);
        alertTV.setText(R.string.user_with_this_email_already_exists);
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
            createUsername.setText("");
            email.setText("");
            phoneNo.setText("");
            createPassword.setText("");
            confirmPassword.setText("");
        });

        siginBtn.setOnClickListener(v -> {
            showSignIn();
            alertDialog.dismiss();
        });
    }
}