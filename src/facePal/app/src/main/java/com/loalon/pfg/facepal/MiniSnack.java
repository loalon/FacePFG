package com.loalon.pfg.facepal;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by OAA on 09/04/2018.
 */

public class MiniSnack {

    public MiniSnack(View view, String text) {

        final Snackbar snackBar= Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });
        snackBar.show();
    }
}
