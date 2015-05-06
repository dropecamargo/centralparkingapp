package com.koiti.centralparking.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by @dropecamargo.
 */
public class ParkingProgressDialog extends ProgressDialog {

    public ParkingProgressDialog(Context context) {
        super(context);

        setCancelable(false);
    }
}
