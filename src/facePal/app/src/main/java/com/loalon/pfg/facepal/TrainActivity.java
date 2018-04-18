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
/**
 * Crea la actividad de entrenamiento de FacePal
 *
 * Created by Alonso on 02/04/2018.
 * @author Alonso Serrano
 * @version 180418
 *
 */
public class TrainActivity extends AppCompatActivity {

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GlobalClass.context);
    final String groupName = prefs.getString("groupName_text", "nada");
    boolean personExists = false;
    final Context context = GlobalClass.context;
    String faceName;
    Boolean faceIdentified=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);
        Intent intent = getIntent();

        /* Cargar la imagen del recorte en el imageView */
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File imgFile=new File(directory,"tempFace.jpg");
        final Bitmap trainBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        ImageView myImage = (ImageView) findViewById(R.id.imageview_train);
        myImage.setImageBitmap(trainBitmap);

        /* Cargar el nombre de la persona en el textView*/
        final EditText textView = (EditText) findViewById(R.id.editText);
        String faceId=Util.identiFace(trainBitmap);
        if (faceId.equals("NO_FACE_IMAGE")){ //si no contiene cara
            new MiniSnack(findViewById(android.R.id.content),
                    "Azure no reconoce la cara de la imagen, compruebe la resolucion.");
        } else if (faceId.equals("NO_CANDIDATE")){ //si no se reconoce permite añadir
            textView.setText("Escriba un nombre");
            faceIdentified=true;
        } else { //persona reconocida, permite añadir cara
            faceName = Util.getName(faceId);
            textView.setText(faceName);
            textView.setEnabled(false);
            textView.setFocusable(false);
            personExists=true;
            faceIdentified=true;
        }
        /*BOTON AÑADIR CARA*/
        Button buttonAddFace = findViewById(R.id.button_addFace);
        buttonAddFace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!personExists) {
                    new MiniSnack(view, "No puede añadirse porque la persona no esta en el sistema");
                } else {
                    new AsyncAddFace(getBaseContext(), view, groupName, faceName, trainBitmap)
                            .execute();

                }
            }
        });

        /*BOTON AÑADIR PERSONA*/
        Button buttonAddPerson = findViewById(R.id.button_addPerson);
        buttonAddPerson.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(personExists) {
                    new MiniSnack(view, "La persona existe, no puede volver a crearse");
                } else {
                    if (faceIdentified) {
                        new AsyncAddPerson(getBaseContext(), view, groupName,
                                textView.getText().toString(), trainBitmap).execute();
                    } else {
                        new MiniSnack(findViewById(android.R.id.content),
                                "Azure no reconoce la cara de la imagen, compruebe la resolucion.");
                    }
                }
            }
        });
    }
}
