package com.loalon.pfg.facepal;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Alonso on 09/04/2018.
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
        Toast.makeText(context, "Ejecutando reconocimiento facial. Espere por favor", Toast.LENGTH_LONG).show();
    }

    @Override
    protected String doInBackground(Context... params) {
        String personaID = Util.identiFace(bitmap);
        if (personaID.startsWith("ERROR:")){
            return personaID;
        }
        String name = Util.getName(personaID);
        return name;
    }

    @Override
    protected void onPostExecute(String result) {
        new MiniSnack(view, result);
    }
}
