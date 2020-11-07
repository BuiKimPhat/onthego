package com.leobkdn.onthego.ui.go;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.leobkdn.onthego.R;

public class ExistTripAddDialog extends DialogFragment {
    private String token;
    private TripResult tripResult;

    public ExistTripAddDialog(String token, TripResult tripResult) {
        this.token = token;
        this.tripResult = tripResult;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View contentView = inflater.inflate(R.layout.dialog_exist_trip, null);
        builder.setView(contentView)
                // Add action buttons
                .setPositiveButton("Thêm mới", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().findViewById(R.id.tripLoading).setVisibility(View.VISIBLE);
                        EditText tripId = contentView.findViewById(R.id.existTripId_edit);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                tripResult.addUserTrip(token, Integer.parseInt(tripId.getText().toString()));
                            }
                        }).start();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ExistTripAddDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
