package com.student.digimart;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.airbnb.lottie.LottieAnimationView;

public class LoadingDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View alertCustomDialog = LayoutInflater.from(requireActivity()).inflate(R.layout.dialogbox_userexists, null);

        LottieAnimationView animationView = alertCustomDialog.findViewById(R.id.loading);
        animationView.playAnimation();
        animationView.setVisibility(View.VISIBLE);

        Button siginBtn = alertCustomDialog.findViewById(R.id.sign_in_btn);
        siginBtn.setVisibility(View.GONE);

        ImageView cancel = alertCustomDialog.findViewById(R.id.cancel_button);
        cancel.setVisibility(View.GONE);

        TextView alertTV = alertCustomDialog.findViewById(R.id.alert_textview);
        alertTV.setText(R.string.please_wait_data_is_being_loaded);


        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(alertCustomDialog);

        final AlertDialog alertDialog = builder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        return alertDialog;
    }
}
