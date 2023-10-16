package com.student.digimart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.paperdb.Paper;

public class Home extends AppCompatActivity {
private Button logoutBtn;
private FirebaseAuth firebaseAuth;
 private GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        logoutBtn = findViewById(R.id.logoutBtn);

        // Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize firebase user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        googleSignInClient = GoogleSignIn.getClient(Home.this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);

        logoutBtn.setOnClickListener(v -> {
            Paper.book().destroy();
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                // Check condition
                if (task.isSuccessful()) {
                    // When task is successful sign out from firebase
                    firebaseAuth.signOut();
                    Intent intent = new Intent(Home.this, MainActivity.class); // Change MainActivity to the appropriate activity where your fragment is hosted
                    intent.putExtra("logoutFlag", true); // Use a key-value pair to indicate logout
                    startActivity(intent);
                    finish();
                    // Display Toast
                    Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
                    // Finish activity
                    finish();
                }
            });
        });
    }
}