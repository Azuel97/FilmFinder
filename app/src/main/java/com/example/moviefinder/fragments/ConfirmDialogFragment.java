package com.example.moviefinder.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ConfirmDialogFragment extends DialogFragment {

    private String title, message;
    private long id;
    private ConfirmDialogFragmentListener listener;

    public ConfirmDialogFragment(String title, String message, long id) {
        this.title = title;
        this.message = message;
        this.id = id;
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof ConfirmDialogFragmentListener) {
            listener = (ConfirmDialogFragmentListener) activity;
        } else {
            listener = null;
        }
        super.onAttach(activity);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onPositivePressed(id);
            }
        });
        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getActivity(), "PREMUTO NO", Toast.LENGTH_SHORT).show();
                listener.onNegativePressed();

            }
        });
        return dialog.create();
    }
}
