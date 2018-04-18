package com.loalon.pfg.facepal;

import android.app.Application;
import android.content.Context;

/**
 * Permite acceder de forma "global" al context de la aplicacion
 *
 * Created by Alonso on 05/04/2018.
 * @author Alonso Serrano
 * @version 180405
 *
 */
public class GlobalClass extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
