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
        Bitmap trainBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        ImageView myImage = (ImageView) findViewById(R.id.imageview_train);
        myImage.setImageBitmap(trainBitmap);

        /* Cargar el nombre de la persona en el textView*/
        final EditText textView = (EditText) findViewById(R.id.editText);
        String faceId=Util.identiFace(groupName, trainBitmap);
        if (faceId.equals("NO_CANDIDATE")){
            textView.setText("Escriba un nombre");

        } else {
            String faceName = Util.getName(groupName, faceId);
            textView.setText(faceName);
            textView.setEnabled(false);
            textView.setFocusable(false);
            personExists=true;
        }

        Button buttonAddFace = findViewById(R.id.button_addFace);
        buttonAddFace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!personExists) {
                    Snackbar.make(view, "No puede añadirse porque la persona no esta en el sistema", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "Añadiendo cara", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        Button buttonAddPerson = findViewById(R.id.button_addPerson);
        buttonAddPerson.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(personExists) {
                    Snackbar.make(view, "Persona existe, no puede volver a crearse", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "Añadiendo persona", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TrainActivity.this, R.style.dialogTheme);
                    alertDialogBuilder.setMessage("Va a añadirse "+ textView.getText() + " al sistema. ¿Esta seguro?");
                    // set prompts.xml to alertdialog builder
                    //alertDialogBuilder.setView(promptsView);
                    alertDialogBuilder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //Toast.makeText(context,"pulsaste yes",Toast.LENGTH_LONG).show();
                            //finish();
                        }
                    });
                    alertDialogBuilder.setNegativeButton("NO",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }
            }
        });

    }
}
