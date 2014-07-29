package com.tikalk.tikalhub.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.tikalk.tikalhub.R;

public class ConfirmDialog extends DialogFragment {

    private Runnable positiveClickListener;

    public static void show(Activity activity, String message, Runnable positiveClickListener, String tag) {

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setPositiveClickListener(positiveClickListener);
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        dialog.setArguments(bundle);

        dialog.show(activity.getFragmentManager(), tag);

    }

    public void setPositiveClickListener(Runnable runnable) {

        positiveClickListener = runnable;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString("message");

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if(positiveClickListener != null)
                            positiveClickListener.run();

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();

    }
}
