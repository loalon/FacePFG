package com.loalon.pfg.facepal;

import android.content.BroadcastReceiver;

import android.content.Context;

import android.content.Intent;

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
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.app.Activity;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.microsoft.projectoxford.face.*;
import com.microsoft.projectoxford.face.contract.*;

/**
 * Created by OAA on 29/03/2018.
 */

public class Util {

    private static String BASE_URL = "https://northeurope.api.cognitive.microsoft.com/face/v1.0/";
    private static String BASE_KEY = "6ff97ccedba642f78dc07a821122fc4d";

    public static String getBaseURL(){
        return BASE_URL;
    }
    public static String getBaseKey(){
        return BASE_URL;
    }

    // convert image to base 64 so that we can send the image to Emotion API
    public static byte[] toBase64(ImageView imgPreview) {
        Bitmap bm = ((BitmapDrawable) imgPreview.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        return baos.toByteArray();
    }


    private FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(BASE_URL, BASE_KEY);
    //HttpClient httpclient = new DefaultHttpClient();
    private static final int MAX_FACE = 10;

    //Person[] hola = faceServiceClient.list
    public static String identiFace (String filename){


        return "";
    }


    void cropFace(){

    }
    String detectFace() {
        return "patata";

    }


}
