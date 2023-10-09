package com.student.digimart;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.student.digimart.Models.Users;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GoogleAuthHandler {
    private final FirebaseAuth firebaseAuth;
    private final Executor executor;

    public GoogleAuthHandler() {
        firebaseAuth = FirebaseAuth.getInstance();
        executor = Executors.newSingleThreadExecutor();
    }

    public void signInWithGoogle(Activity activity, Intent data, OnGoogleSignInResultListener listener){
        GoogleSignInAccount account = getSignInAccountFromIntent(data);
        if (account != null) {
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
//            Firebase API calls are executed on a background thread
            executor.execute(() -> {
                try {
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                Users googleUser = new Users(user.getEmail(), null, null, user.getDisplayName());
                                listener.onGoogleSignInSuccess(googleUser);
                            }else{
                                listener.onGoogleSignInFailure(new Exception("Firebase user is null"));
                            }
                        } else {
                            listener.onGoogleSignInFailure(task.getException());
                            Log.e("onGoogleSignInFailure", "google Auth Handle : Firebase authentication failed" );
                        }
                    });
                } catch (Exception e) {
                    listener.onGoogleSignInFailure(e);
                    Log.e("GoogleSignIn", "Exception: " + e.getMessage(), e);

                }
            });
        }else {
            listener.onGoogleSignInFailure(new Exception("Google Sign-In Account is null"));
        }
    }

    private GoogleSignInAccount getSignInAccountFromIntent(Intent data) {
        try {
            GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            assert signInResult != null;
            if (signInResult.isSuccess()) {
                return signInResult.getSignInAccount();
            } else {
                // Handle sign-in failure and throw ApiException
                Status status = signInResult.getStatus();
                throw new ApiException(status);
            }
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface OnGoogleSignInResultListener {
        void onGoogleSignInSuccess(Users user);

        void onGoogleSignInFailure(Exception e);
    }
}
