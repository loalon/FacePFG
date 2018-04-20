package com.loalon.pfg.facepal;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Tarea asincrona de recortar cara
 *
 * Created by Alonso on 09/04/2018.
 * @author Alonso Serrano
 * @version 180415
 *
 */

public class AsyncFaceCroper extends AsyncTask<Context, Integer, String> {

    public interface AsyncResponse {
        void processFinish(Boolean output, Bitmap outBM);
    }

    public AsyncResponse delegate = null;

    Bitmap bitmap;
    Bitmap newBitmap;
    Context context;
    ImageView imageView;
    Boolean faceLoaded=false;



    public AsyncFaceCroper(Context context, Bitmap bitmap, ImageView imageView, AsyncResponse delegate) {
        super();
        this.bitmap=bitmap;
        this.context=context;
        this.imageView= (ImageView) imageView;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(context, "Ejecutando detección facial. Espere por favor", Toast.LENGTH_LONG).show();
    }

    @Override
    protected String doInBackground(Context... params) {
        newBitmap = Util.detectFace(bitmap);
        if (!(newBitmap==null)) {
            faceLoaded=true;
            return "OK";
        } else {
            faceLoaded=false;
            newBitmap=bitmap;
            return "NO_FACE";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if(faceLoaded){
            imageView.setImageBitmap(newBitmap);
        } else {
            imageView.setImageBitmap(bitmap);
            Toast.makeText(context, "Cara no detectada, compruebe en la imagen cargada" +
                            " si existe una sola cara y que la orientación sea correcta."
                    , Toast.LENGTH_LONG).show();
        }
        delegate.processFinish(faceLoaded, newBitmap);
    }
}
