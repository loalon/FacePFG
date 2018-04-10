package com.loalon.pfg.facepal;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final int PICK_IMAGE = 1;
    private static final int MAX_FACE = 10;
    private boolean faceLoaded = false;
    private Bitmap bitmap2;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                    new AsyncFaceDetector(getBaseContext(), view, bitmap2).execute();
                } else {
                    new MiniSnack(view, "No hay una cara cargada");
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
                    new MiniSnack(view, "No hay una cara cargada");
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
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(context, AboutActivity.class);
            startActivity(intent);
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
                ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                new AsyncFaceCroper(getBaseContext(), bitmap, imageView, new AsyncFaceCroper.AsyncResponse(){
                    @Override
                    public void processFinish(Boolean output, Bitmap outBM){
                        faceLoaded=output;
                        bitmap2=outBM;
                    }
                }).execute();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}