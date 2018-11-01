package com.proposeme.seven.mpsg.view;

import android.app.AlertDialog;
import android.app.Dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.proposeme.seven.mpsg.R;

public class OpenToneFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.choose_tone, null);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.open_tone_dialog)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel,null)
                .create();
    }
}
