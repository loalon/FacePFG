package com.loalon.pfg.facepal;

import android.app.Application;
import android.content.Context;

/**
 * Created by OAA on 05/04/2018.
 * Solo para acceder de forma "global" al context de la aplicacion
 */

public class GlobalClass extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
