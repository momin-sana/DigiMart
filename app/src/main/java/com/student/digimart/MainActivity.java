package com.student.digimart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.student.digimart.Models.Users;
import com.student.digimart.Prevalent.Prevalent;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private final String userDb = "Users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this);

        SigninFragment newFragment = new SigninFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        String userEmailKey = Paper.book().read(Prevalent.UserEmailKey);
        String userPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if (userEmailKey != null && userPasswordKey != null){
            if (!TextUtils.isEmpty(userEmailKey) && !TextUtils.isEmpty(userPasswordKey)){
                allowAccess(userEmailKey, userPasswordKey);
                loadingDialog();
            }
        }

    }

    private void allowAccess(String email, String password) {
        String sanitizedEmail = email.replace('.', '_').replace('#', '_').replace('$', '_').replace('[', '_').replace(']', '_');
        // Check Firebase Authentication
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Firebase Authentication successful
                dismissLoadingDialog();
                Intent intent = new Intent(MainActivity.this, Home.class);
                intent.putExtra("userEmail", email);
                startActivity(intent);
                finish();
            } else {
                // Firebase Authentication failed, check Realtime Database
                checkRealtimeDatabase(sanitizedEmail, password);
            }
        });

    }

    private void checkRealtimeDatabase(String sanitizedEmail, String password) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dismissLoadingDialog();
                if (snapshot.child(userDb).child(sanitizedEmail).exists()){
                    Users usersData = snapshot.child(userDb).child(sanitizedEmail).getValue(Users.class);
                    assert usersData != null;
                    String userEmailFromDb = usersData.getEmail().toLowerCase();
                    Log.d("Debug", "user remembered: " + userEmailFromDb);
                    if (usersData.getEmail().equals(sanitizedEmail) && usersData.getPassword().equals(password)) {
                        dismissLoadingDialog();
                        Intent intent = new Intent(MainActivity.this, Home.class);
                        intent.putExtra("userEmail", userEmailFromDb);
                        startActivity(intent);
                        finish();
                    }else {
                        dismissLoadingDialog();
                    }
                } else{
                    dismissLoadingDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dismissLoadingDialog();
                // Handle database error here if needed
                Toast.makeText(MainActivity.this, R.string.database_error_or_network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadingDialog() {
        LoadingDialogFragment loadingDialogFragment = new LoadingDialogFragment();
        loadingDialogFragment.show(MainActivity.this.getSupportFragmentManager(), "loading_dialog");
    }
    private void dismissLoadingDialog() {
        LoadingDialogFragment loadingDialog = (LoadingDialogFragment) MainActivity.this.getSupportFragmentManager().findFragmentByTag("loading_dialog");
        if (loadingDialog != null && loadingDialog.getDialog() != null && loadingDialog.getDialog().isShowing()) {
            loadingDialog.dismiss();
        }
    }
}