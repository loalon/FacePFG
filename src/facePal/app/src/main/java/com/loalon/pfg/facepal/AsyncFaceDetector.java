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

    //public interface AsyncResponse {
    //    void processFinish(Boolean output);
   // }

  //  public AsyncResponse delegate = null;

    Bitmap bitmap;
    Context context;
    View view;
    //Boolean faceIdentified;

    public AsyncFaceDetector(Context context, View view, Bitmap bitmap) {
        super();
        this.bitmap=bitmap;
        this.context=context;
        this.view=view;
        //Boolean faceIdentified=false;
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
        if (personaID.startsWith("NO_FACE_IMAGE")){
            return personaID;
        }
        String name = Util.getName(personaID);
        return name;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.startsWith("NO_FACE_IMAGE")){
            new MiniSnack(view, "Azure no reconoce la cara de la imagen," +
                    " compruebe la resolucion.");
        } else {
            //faceIdentified = true;
            new MiniSnack(view, result);
        }
        //delegate.processFinish(faceIdentified);
    }
}
