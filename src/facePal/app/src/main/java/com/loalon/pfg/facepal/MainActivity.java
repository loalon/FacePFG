package com.loalon.pfg.facepal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.Camera;
//import android.media.FaceDetector;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private final int PICK_IMAGE = 1;
    private static final int MAX_FACE = 10;
    private ProgressDialog detectionProgressDialog;
    private boolean faceLoaded = false;
    private Bitmap bitmap2;
    private String consoleText = "neutral";
    private String groupName="conocidos";
    final Context context = this;

    /*
    builder.setMessage(R.string.dialog_message)
            .setTitle(R.string.dialog_title);
      alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    });
    */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String textTemp = prefs.getString("sKey_text", "nada");

        Button buttonBrowse = findViewById(R.id.button_browse);
        buttonBrowse.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            Intent gallIntent = new Intent(Intent.ACTION_GET_CONTENT);
            gallIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(gallIntent, "Escoge imagen"), PICK_IMAGE);
            if(faceLoaded) {
                Snackbar.make(view, "Imagen cargada", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                Snackbar.make(view, "Imagen no cargada", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            }
        });
        Button buttonRecon = findViewById(R.id.button_recon);
        buttonRecon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Intent gallIntent = new Intent(Intent.ACTION_GET_CONTENT);
                //gallIntent.setType("image/*");
                //startActivityForResult(Intent.createChooser(gallIntent, "Escoge imagen"), PICK_IMAGE);
                if(faceLoaded){
                    //StringBuilder stringBuilder = new StringBuilder(Util.getBaseURL()).append("recognize");

                    //Snackbar.make(view, stringBuilder.toString(), Snackbar.LENGTH_LONG)
                            //.setAction("Action", null).show();
                //identiFace idfac = new identiFace("conocidos");
                //idfac.execute();
                    TextView textView = (TextView) findViewById(R.id.text_console);
                    //FaceIdentificator faceide = new FaceIdentificator(MainActivity.this,"conocidos", bitmap2, consoleText);
                    //faceide.execute();
                    String personaID=Util.identiFace("conocidos", bitmap2);
                    consoleText = Util.getName("conocidos", personaID);
                    textView.setText(consoleText);
                    Snackbar.make(view, consoleText, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();


                } else {
                    Snackbar.make(view, "No hay una cara cargada", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        Button buttonTrain = findViewById(R.id.button_train);
        buttonTrain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(faceLoaded){
                    Intent intent = new Intent(context, TrainActivity.class);
                    //EditText editText = (EditText) findViewById(R.id.editText);
                    //String message = editText.getText().toString();
                    //intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);
                    /*
                    //String faceid = Util.identiFace(groupName, bitmap2);
                    //si faceid==NO_CANDIDATE
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.input_text, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.editTextDialogUserInput);
                    consoleText="ZZZ";
                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("SI",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            // get user input and set it to result
                                            // edit text
                                            consoleText=userInput.getText().toString();
                                        }
                                    })
                            .setNegativeButton("Cancelar",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.cancel();

                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                    //String personaID=Util.identiFace("conocidos", bitmap2);
                    */
                        Snackbar.make(view, consoleText, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "No hay una cara cargada", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        detectionProgressDialog = new ProgressDialog(this);
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
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //Bitmap blank =
                //pasarlo a detector de caras
                //Bitmap bitmap2=detectFace(bitmap);
                bitmap2=detectFace(bitmap);
                //if (bitmap2 == null){
                //cortar y devolver
                ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                if (bitmap2 == null) {
                    imageView.setImageResource(R.drawable.nondetected);
                    faceLoaded=false;
                } else {
                    imageView.setImageBitmap(bitmap2);
                    faceLoaded=true;
                }
                //ImageView imageView = (ImageView) findViewById(R.id.imageView1);


               // Snackbar.make(findViewById(R.id.myCoordinatorLayout), bitmap2, Snackbar.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap detectFace(Bitmap bitmap) {
        Bitmap newBitmap;
        FaceDetector faceDetector = new
                FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false) .build();

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);
        System.out.println("w " + bitmap.getWidth());
        System.out.println("h " + bitmap.getHeight());
        System.out.println("max " + MAX_FACE);
        int x; //pos x
        int y; //pos y
        int w; //
        int h;
        System.out.println("numero de caras " + faces.size());

        if(faces.size() != 1) {
            faceDetector.release();
            return null; //0, 2 o mas caras devuelven null
        } else {
            Face face = faces.valueAt(0); //solo una cara
            x= (int) face.getPosition().x;
            //la corrección de Y es necesaria, en ocasiones cambia el punto de origen
            int preY=(int) face.getPosition().y;
                if (preY>0){
                    y= preY;
                } else {
                    y= -1* preY;
                }

            w= (int) face.getWidth();
            h= (int) face.getHeight();
            System.out.println("x " + x);
            System.out.println("y " + y);
            System.out.println("w " + w);
            System.out.println("h " + h);
            newBitmap=Bitmap.createBitmap(bitmap, x, y, w, h);

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            // Create imageDir
            File mypath=new File(directory,"tempFace.jpg");

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(mypath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                newBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(directory.getAbsolutePath());
            /*
            FileOutputStream out = null;
            try {
                //out = new FileOutputStream("tempFace.png");
                out = openFileOutput(filename, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/

            faceDetector.release();
            return newBitmap;
        }
    }

    private String reconFace(Bitmap bitmap){
        String name;

        return "";
    }
}
