package com.loalon.pfg.facepal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    private final int PICK_IMAGE = 1;
    private static final int MAX_FACE = 10;
    //private ProgressDialog detectionProgressDialog;
    private boolean faceLoaded = false;
    private Bitmap bitmap2;
    private String consoleText = "neutral";
    private String groupName = "conocidos";
    final Context context = this;
    //private ProgressBar progressBar;
    ProgressBar progressBar;

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
            Toast.makeText(getBaseContext(), "Ejecutando reconocimiento", Toast.LENGTH_LONG).show();
            if (faceLoaded) {

                //TextView textView = (TextView) findViewById(R.id.text_console);
                //String personaID = Util.identiFace("conocidos", bitmap2);

                /*
                String personaID ="NO_CANDIDATE";
                Snackbar.make(view, "Cargando cara. Espere por favor", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                ProgressDialog dialog = new ProgressDialog(MainActivity.this);



                progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar

                dialog.setCancelable(true);
                dialog.setMessage("Please wait...");
                //dialog.setIndeterminate(true);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
*/
                //String personaID ="NADA";
                String texto="nada";

               // try {
                    System.out.println("llamada asincrona");
                   // String personaID=
                    new AsyncFaceDetector(getBaseContext(), view, bitmap2).execute();
                    //texto = Util.getName("conocidos", personaID);
                    System.out.println("fin llamada asincrona");
            //    } catch (InterruptedException e) {
            //        e.printStackTrace();
             //   } catch (ExecutionException e) {
             //       e.printStackTrace();
             //   }


                //textView.setText(consoleText);
                //new MiniSnack(view, "Sujeto: " + texto);
                //Snackbar.make(view, "Sujeto: " + consoleText, Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();
            } else {
                //Toast.makeText(getBaseContext(), "else reconocimiento", Toast.LENGTH_SHORT).show();
                new MiniSnack(view, "No hay una cara cargada");
                /*
                final Snackbar snackBar= Snackbar.make(view, "No hay una cara cargada", Snackbar.LENGTH_INDEFINITE);
                snackBar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackBar.dismiss();
                    }
                });
                snackBar.show();*/

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
                    /*
                    final Snackbar snackBar= Snackbar.make(view, "No hay una cara cargada", Snackbar.LENGTH_INDEFINITE);
                    snackBar.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackBar.dismiss();
                        }
                    });

                    snackBar.show();
                    */
                }
            }
        });
    }
    //@Override

    /*
    public void onBtnClicked(View v)
    {
        if(v.getId() == R.id.button_recon)
        {
            Toast.makeText(this, "Ejecutando reconocimiento", Toast.LENGTH_SHORT).show();
        }
    }
*/
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
                System.out.println("Empieza la activity result");
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //int rotation = Util.getCameraPhotoOrientation(this, uri,
                  //      uri.getPath());
                //Bitmap bitmapTemp = Util.rotateImage(bitmap,rotation);
                //Bitmap bitmapTemp= Util.rotateImageIfRequired(bitmap,context,uri);
                //progressBar.setVisibility(View.VISIBLE);
                ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                new AsyncFaceCroper(getBaseContext(), bitmap, imageView,
                        new AsyncFaceCroper.AsyncResponse(){

                            @Override
                            public void processFinish(Boolean output, Bitmap outBM){
                                faceLoaded=output;
                                bitmap2=outBM;
                                //Here you will receive the result fired from async class
                                //of onPostExecute(result) method.
                            }
                        }
                        ).execute();

                //bitmap2 = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                /*
                bitmap2 = Util.detectFace(bitmap);
                //progressBar.setVisibility(View.GONE);
                System.out.println("fin de detectFAce");

                if (bitmap2 == null) {
                    //imageView.setImageResource(R.drawable.nondetected);
                    imageView.setImageBitmap(bitmap);
                    new AlertDialog.Builder(context).setMessage("Cara no detectada, compruebe orientación")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
                    faceLoaded = false;
                } else {
                    System.out.println("Empieza setImage");

                    imageView.setImageBitmap(bitmap2);
                    System.out.println("Acabe setImage");
                    faceLoaded = true;
                }
                */
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}