package com.loalon.pfg.facepal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.Camera;
//import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
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
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final int PICK_IMAGE = 1;
    private static final int MAX_FACE = 10;
    private ProgressDialog detectionProgressDialog;
    private boolean faceLoaded = false;



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

                //Snackbar.make(view, textTemp, Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();

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
                    Snackbar.make(view, textTemp, Snackbar.LENGTH_LONG)
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
                    Snackbar.make(view, textTemp, Snackbar.LENGTH_LONG)
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
                Bitmap bitmap2=detectFace(bitmap);
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
        //resetData();
        Bitmap newBitmap;
        FaceDetector faceDetector = new
                FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false) .build();

        //if(!faceDetector.isOperational()){
          //  new AlertDialog.Builder(v.getContext()).setMessage("Could not set up the face detector!").show();
           // return;
        //}
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);
        //android.media.FaceDetector fdet_ = new android.media.FaceDetector(bitmap.getWidth(), bitmap.getHeight(), MAX_FACE);
        System.out.println("w " + bitmap.getWidth());
        System.out.println("h " + bitmap.getHeight());
        System.out.println("max " + MAX_FACE);
        int x; //pos x
        int y; //pos y
        int w; //
        int h;
        //android.media.FaceDetector.Face[] fullResults = new android.media.FaceDetector.Face[MAX_FACE];
        //int numFaces = fdet_.findFaces(bitmap, fullResults);
        System.out.println("numero de caras " + faces.size());

        //return bitmap;

        //if(fullResults.length != 1) {
        if(faces.size() != 1) {
            faceDetector.release();
            return null;
        } else {
            //coger el primer elemento
            //FaceDetector.Face face=fullResults[0];
            //PointF mid = new PointF();
            //face.getMidPoint(mid);
            Face face = faces.valueAt(0);
            //float eyesDis = face.eyesDistance();
            //float confidence = face.confidence();
            //float pose = face.pose(android.media.FaceDetector.Face.EULER_Y);
            //x=(int)(mid.x - eyesDis * 1.20f);
            //y=(int) (mid.y - eyesDis * 0.55f);
            x= (int) face.getPosition().x;
            int preY=(int) face.getPosition().y;
                if (preY>0){
                    y= preY;
                }else {
                    y= -1* preY; //0,0 desde esquina superior izq, y es negativo
                }

            w= (int) face.getWidth();
            h= (int) face.getHeight();
            System.out.println("x " + x);
            System.out.println("y " + y);
            System.out.println("w " + w);
            System.out.println("h " + h);
            /*
            Rect rect = new Rect( //x1,y1,x2,y2
                    (int) (mid.x - eyesDis * 1.20f),
                    (int) (mid.y - eyesDis * 0.55f),
                    (int) (mid.x + eyesDis * 1.20f),
                    (int) (mid.y + eyesDis * 1.85f));
            w=rect.width();
            h=rect.height();
            */
            //if (rect.height() * rect.width() > 100 * 100) {
                //nada
            //}
            newBitmap=Bitmap.createBitmap(bitmap, x, y, w, h);
            faceDetector.release();

            return newBitmap;

        }

    }
}
