package com.student.digimart;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class SigninFragment extends Fragment {

    TextView createAccTV;

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