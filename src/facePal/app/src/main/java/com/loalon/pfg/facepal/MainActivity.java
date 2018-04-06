package com.loalon.pfg.facepal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private final int PICK_IMAGE = 1;
    private static final int MAX_FACE = 10;
    //private ProgressDialog detectionProgressDialog;
    private boolean faceLoaded = false;
    private Bitmap bitmap2;
    private String consoleText = "neutral";
    private String groupName = "conocidos";
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //final String textTemp = prefs.getString("sKey_text", "nada");

        /* BOTON BUSCAR */
        Button buttonBrowse = findViewById(R.id.button_browse);
        buttonBrowse.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent gallIntent = new Intent(Intent.ACTION_GET_CONTENT);
                gallIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(gallIntent, "Escoge imagen"), PICK_IMAGE);
            }
        });

        /* BOTON RECONOCER */
        Button buttonRecon = findViewById(R.id.button_recon);
        buttonRecon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            if (faceLoaded) {
                TextView textView = (TextView) findViewById(R.id.text_console);
                String personaID = Util.identiFace("conocidos", bitmap2);
                consoleText = Util.getName("conocidos", personaID);
                textView.setText(consoleText);
                Snackbar.make(view, consoleText, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                final Snackbar snackBar= Snackbar.make(view, "No hay una cara cargada", Snackbar.LENGTH_INDEFINITE);
                snackBar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackBar.dismiss();
                    }
                });
                snackBar.show();
            }
            }
        });

        /* BOTON ENTRENAR */
        Button buttonTrain = findViewById(R.id.button_train);
        buttonTrain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (faceLoaded) {
                    Snackbar.make(view, "Cargando ventana entranamiento", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Intent intent = new Intent(context, TrainActivity.class);
                    startActivity(intent);
                } else {
                    final Snackbar snackBar= Snackbar.make(view, "No hay una cara cargada", Snackbar.LENGTH_INDEFINITE);
                    snackBar.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackBar.dismiss();
                        }
                    });
                    snackBar.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* GESTION TRAS SELECCION DE IMAGEN */
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //int rotation = Util.getCameraPhotoOrientation(this, uri,
                  //      uri.getPath());
                //Bitmap bitmapTemp = Util.rotateImage(bitmap,rotation);
                //Bitmap bitmapTemp= Util.rotateImageIfRequired(bitmap,context,uri);

                bitmap2 = Util.detectFace(bitmap);
                ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                if (bitmap2 == null) {
                    //imageView.setImageResource(R.drawable.nondetected);
                    imageView.setImageBitmap(bitmap);

                    faceLoaded = false;
                } else {
                    imageView.setImageBitmap(bitmap2);
                    faceLoaded = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}