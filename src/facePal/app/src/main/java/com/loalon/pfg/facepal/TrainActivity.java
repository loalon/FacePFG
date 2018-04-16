package com.loalon.pfg.facepal;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class TrainActivity extends AppCompatActivity {

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GlobalClass.context);
    final String groupName = prefs.getString("groupName_text", "nada");
    boolean personExists = false;
    final Context context = GlobalClass.context;
    String faceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        Intent intent = getIntent();

        /* Cargar la imagen del recorte en el imageView */
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        ///data/user/0/com.loalon.pfg.facepal/app_imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File imgFile=new File(directory,"tempFace.jpg");
        final Bitmap trainBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        ImageView myImage = (ImageView) findViewById(R.id.imageview_train);
        myImage.setImageBitmap(trainBitmap);

        /* Cargar el nombre de la persona en el textView*/
        final EditText textView = (EditText) findViewById(R.id.editText);
        String faceId=Util.identiFace(trainBitmap);
        if (faceId.equals("NO_CANDIDATE")){
            textView.setText("Escriba un nombre");

        } else {
            faceName = Util.getName(faceId);
            textView.setText(faceName);
            textView.setEnabled(false);
            textView.setFocusable(false);
            personExists=true;
        }
        /*BOTON AÑADIR CARA*/
        Button buttonAddFace = findViewById(R.id.button_addFace);
        buttonAddFace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!personExists) {
                    //Snackbar.make(view, "No puede añadirse porque la persona no esta en el sistema", Snackbar.LENGTH_LONG)
                   //         .setAction("Action", null).show();
                    new MiniSnack(view, "No puede añadirse porque la persona no esta en el sistema");
                } else {
                    new AsyncAddFace(getBaseContext(), view, groupName, faceName, trainBitmap).execute();
/*
                    Snackbar.make(view, "Añadiendo cara", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    String result=Util.addFace(groupName, faceName, trainBitmap);
                    if (result.equals("ERROR")){
                        Snackbar.make(view, "Error añadiendo cara, intentelo más tarde", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(view, "Cara añadida con exito", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    */
                }
            }
        });

        Button buttonAddPerson = findViewById(R.id.button_addPerson);
        buttonAddPerson.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(personExists) {
                    new MiniSnack(view, "La persona existe, no puede volver a crearse");
                    //Snackbar.make(view, "Persona existe, no puede volver a crearse", Snackbar.LENGTH_LONG)
                            //.setAction("Action", null).show();
                } else {
                    new AsyncAddPerson(getBaseContext(), view, groupName, textView.getText().toString(), trainBitmap).execute();
                    //Snackbar.make(view, "Añadiendo persona", Snackbar.LENGTH_LONG)
                      //      .setAction("Action", null).show();

                    // comprobar la existencia de ese nombre
                    //basicamente si devuelve un ID es que ya existe
                    // mensaje que ya existe
/*
                    String theName = Util.getPersonID(groupName, textView.getText().toString());
                    //si devuelve NO_ID es que no existe
                    if (!theName.equals("NO_ID")) {
                        Snackbar.make(view, "Persona ya en el sistema. Compruebe nombre", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        //si no cuadro de dialogo y añadir
                        String addedPerson=Util.addPerson(groupName, textView.getText().toString());
                        System.out.println("en actividadTrain " + addedPerson);
                        if (!addedPerson.equals("ERROR")) {
                            String result=Util.addFace(groupName, textView.getText().toString(), trainBitmap);
                            if (result.equals("ERROR")){
                                Snackbar.make(view, "Error añadiendo cara, intentelo más tarde", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            } else {
                                Snackbar.make(view, "Cara añadida con exito", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }

                    }
                    */
                }
            }
        });

    }
}
