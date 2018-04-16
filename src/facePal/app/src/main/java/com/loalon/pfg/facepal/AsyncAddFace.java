package com.loalon.pfg.facepal;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Alonso on 09/04/2018.
 */

public class AsyncAddFace extends AsyncTask<Context, Integer, String> {

    Bitmap bitmap;
    Context context;
    View view;
    String groupName;
    String faceName;

    public AsyncAddFace(Context context, View view, String groupName, String faceName, Bitmap bitmap) {
        super();
        this.bitmap=bitmap;
        this.context=context;
        this.view=view;
        this.groupName=groupName;
        this.faceName=faceName;
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(context, "Añadiendo cara. Espere por favor", Toast.LENGTH_LONG).show();
    }

    @Override
    protected String doInBackground(Context... params) {
        //String personaID = Util.identiFace(bitmap);
        String result=Util.addFace(groupName, faceName, bitmap);
        return result;
    }

    @Override
    protected void onPostExecute(String result) {

        if (result.startsWith("ERROR:")){
            new MiniSnack(view, result);
        } else {
            new MiniSnack(view, "Cara añadida con exito");
        }
    }
}
