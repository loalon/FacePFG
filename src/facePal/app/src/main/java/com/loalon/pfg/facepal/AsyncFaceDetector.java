package com.loalon.pfg.facepal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by OAA on 09/04/2018.
 */

public class AsyncFaceDetector extends AsyncTask<Context, Integer, String> {

    Bitmap bitmap;
    Context context;
    View view;

    public AsyncFaceDetector(Context context, View view, Bitmap bitmap) {
        super();
        this.bitmap=bitmap;
        this.context=context;
        this.view=view;
    }

    @Override
    protected void onPreExecute() {
        //System.out.println("PRE");
        //new MiniSnack(view, "Prejecucion: ");
        Toast.makeText(context, "Ejecutando detección. Espere por favor", Toast.LENGTH_LONG).show();
    }

    @Override
    protected String doInBackground(Context... params) {
        //System.out.println("DURANTE");
        //final String groupName = prefs.getString("groupName_text", "nada");
        String personaID = Util.identiFace(bitmap);
        if (personaID.startsWith("ERROR:")){
            return personaID;
        }
        String name = Util.getName(personaID);
        return name;
        //return "WHAAAA";
    }

    @Override
    protected void onPostExecute(String result) {
        new MiniSnack(view, result);
        //System.out.println("POS");
    }
}
