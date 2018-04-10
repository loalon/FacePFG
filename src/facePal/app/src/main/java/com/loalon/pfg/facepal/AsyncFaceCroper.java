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
 * Created by OAA on 09/04/2018.
 */

public class AsyncFaceCroper extends AsyncTask<Context, Integer, String> {

    public interface AsyncResponse {
        void processFinish(Boolean output, Bitmap outBM);
    }
    public AsyncResponse delegate = null;

    Bitmap bitmap;
    Bitmap newBitmap;
    Context context;
    View view;
    Snackbar snackbar;
    ImageView imageView;
    ProgressBar progressBar;
    Boolean faceLoaded=false;

    //ProgressDialog dialog;

    AlertDialog alertDialog;

    public AsyncFaceCroper(Context context, Bitmap bitmap, ImageView imageView, AsyncResponse delegate) {
        super();
        this.bitmap=bitmap;
        this.context=context;
        this.progressBar=progressBar;

        this.imageView= (ImageView) imageView;
        this.delegate = delegate;
        //this.dialog=dialog;
        //dialog= new ProgressDialog(context);

        //alertDialog = new AlertDialog(this.context);
    }

    @Override
    protected void onPreExecute() {
        System.out.println("PRE");
        //new MiniSnack(view, "Prejecucion: ");
        Toast.makeText(context, "Ejecutando reconocimiento ahora", Toast.LENGTH_LONG).show();
        //super.onPreExecute();
        //dialog.show();
        //snackbar.make(view, "cargando cara", Snackbar.LENGTH_LONG)
        //      .setAction("Action", null).show();
        //super.onPreExecute();
    }

    @Override
    protected String doInBackground(Context... params) {
        System.out.println("DURANTE");
        newBitmap = Util.detectFace(bitmap);
        faceLoaded=true;
        //String personaID = Util.identiFace("conocidos", bitmap);
        //String texto = Util.getName("conocidos", personaID);
        return "PRONTO";
        //return "WHAAAA";
    }

    @Override
    protected void onPostExecute(String result) {
        //new MiniSnack(view, "Identificación: " + result);
        Toast.makeText(context, "Ejecutando deteccion ahora", Toast.LENGTH_SHORT).show();
        imageView.setImageBitmap(newBitmap);
        System.out.println("POS");
        delegate.processFinish(faceLoaded, newBitmap);
        //if (this.dialog.isShowing()) {
       //     this.dialog.dismiss();
      //  }
      //  diag.
        //snackbar.dismiss();
        //super.onPostExecute(result);
        //Log.i("makemachine", "onPostExecute(): " + result);
        //_percentField.setText(result);
        //_percentField.setTextColor(0xFF69adea);
        //_cancelButton.setVisibility(View.INVISIBLE);
    }
}
