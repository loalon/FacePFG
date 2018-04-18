package com.loalon.pfg.facepal;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

/**
 * Tarea asincrona de añadir cara
 *
 * Created by Alonso on 09/04/2018.
 * @author Alonso Serrano
 * @version 180415
 *
 */

public class AsyncAddFace extends AsyncTask<Context, Integer, String> {

    Bitmap bitmap;
    Context context;
    View view;
    String groupName;
    String faceName;

    /**
     *
     * @param context Contexto de la aplicacion
     * @param view view don de apareceran los mensajes
     * @param groupName nombre del grupo
     * @param faceName nombre de la persona a la que se agregara la cara
     * @param bitmap imagen de la cara a añadir
     */
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
        String result = Util.addFace(groupName, faceName, bitmap);
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
