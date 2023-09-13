package com.student.digimart;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class SigninFragment extends Fragment {

    TextView createAccTV;

    public SigninFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        // TODO GET DATA FROM FIREBASE AND CHECK IF USER EXISTS OR NOT... follow video...
        Button btnSignin = view.findViewById(R.id.signin_btn);
        createAccTV= view.findViewById(R.id.create_account_tv);
        createAccTV.setOnClickListener(v -> showRegistration());

        return view;
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
}