package com.loalon.pfg.facepal;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Crea objetos Snackbar con un duración indeterminada
 * hasta que el usuario pulsa el boton OK
 * El proposito de los Minisnacks es informar y que el usuario no realice otras acciones
 * hasta comprender el mensaje que se le devuelve
 *
 * Created by Alonso on 09/04/2018.
 * @author Alonso Serrano
 * @version 180409
 *
 */

public class MiniSnack {

    /**
     *
     * @param view View donde se desea el mensaje
     * @param text Texto que aparecera en el mensaje
     */
    public MiniSnack(View view, String text) {

        final Snackbar snackBar = Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });
        snackBar.show();
    }
}
