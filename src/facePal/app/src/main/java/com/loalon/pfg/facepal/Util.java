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
import android.os.StrictMode;
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
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import com.microsoft.projectoxford.face.*;
import com.microsoft.projectoxford.face.contract.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by OAA on 29/03/2018.
 */

public class Util {

    private static String BASE_URL = "https://northeurope.api.cognitive.microsoft.com/face/v1.0/";
    private static String BASE_KEY = "6ff97ccedba642f78dc07a821122fc4d";
    private static String BASE_KEY_ALT ="3d1818c14a1a41faa2283c84a09cc2ea";

    public static String getBaseURL(){
        return BASE_URL;
    }
    public static String getBaseKey(){
        return BASE_KEY;
    }

    // convert image to base 64 so that we can send the image to Emotion API
    public static byte[] toBase64(Bitmap bm) {
    //public static byte[] toBase64(ImageView imgPreview) {
        //Bitmap bm = ((BitmapDrawable) imgPreview.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        return baos.toByteArray();
    }


    //private FaceServiceClient faceServiceClient =
            //new FaceServiceRestClient(BASE_URL, BASE_KEY);
    //HttpClient httpclient = new DefaultHttpClient();
    private static final int MAX_FACE = 10;

    //Person[] hola = faceServiceClient.list

    public static String getName(String groupName, String personID ) {
        HttpClient httpclient = HttpClients.createDefault();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try { //conexion
            StringBuilder stringBuilder = new StringBuilder(Util.getBaseURL()).append("persongroups/")
                    .append(groupName).append("/persons");
            //System.out.println(stringBuilder.toString());

            URIBuilder builder = new URIBuilder(stringBuilder.toString());
            URI uri = builder.build();
            HttpGet request = new HttpGet(uri);
            request.setHeader("Ocp-Apim-Subscription-Key", BASE_KEY);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity);
            //System.out.println("resultado ");
            //System.out.println(res);
            JSONArray jsonArray = new JSONArray(res);
            try { //parseo de JSON
                for(int i = 0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    //System.out.println(jsonObject.toString());
                    String id = jsonObject.getString("personId");
                    String name = jsonObject.getString("name");
                    //System.out.println("persona " +id + " " +name);
                    //System.out.println("id " +id);
                    //System.out.println("personID " +personID);
                    if (id.equals(personID)) {
                        //System.out.println("id " +id);
                        //System.out.println("personID " +personID);
                        return name; //persona identificada, devuelve nombre
                    }
                }
            } catch (JSONException e) {
                return "Error en JSON recibido";
            }

            return "Sujeto desconocido";
        } catch (Exception e){
            return "Conexion fallida, vuelva a intentarlo";
        }

    }
    public static String identiFace (String groupName, Bitmap bitmap){
        //ProgressDialog mProgress;
        //mProgress = new ProgressDialog(context);
        //mProgress.setMessage("Downloading nPlease wait...");
       // mProgress.show();

        HttpClient httpclient = HttpClients.createDefault();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            StringBuilder stringBuilder = new StringBuilder(Util.getBaseURL()).append("detect/");
            //.append(groupName).append("/train");
            System.out.println(stringBuilder.toString());

            URIBuilder builder = new URIBuilder(stringBuilder.toString());
            builder.setParameter("returnFaceId", "true");
            //builder.setParameter()
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            //HttpGet request = new HttpGet(uri);
            request.setHeader("Content-Type", "application/octet-stream");
            //request.setHeader("Content-Type", "application/json");
            // enter you subscription key here
            request.setHeader("Ocp-Apim-Subscription-Key", "3d1818c14a1a41faa2283c84a09cc2ea");

            request.setEntity(new ByteArrayEntity(Util.toBase64(bitmap)));
            // Request body.The parameter of setEntity converts the image to base64
            //request.setEntity(new ByteArrayEntity(Util.toBase64(img)));
            //request.se
            // getting a response and assigning it to the string res
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity);
            System.out.println("resultado ");
            System.out.println(res);
            JSONArray jsonArray = null;
            String faceid;
            try {
                jsonArray = new JSONArray(res);

                System.out.println(jsonArray.get(0).toString());
                JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());
                faceid = jsonObject.getString("faceId");
                System.out.println("salida " + faceid);

            } catch (JSONException e) {
                faceid = "null";
                return "null";
            }
            StringBuilder stringBuilder2 = new StringBuilder(Util.getBaseURL()).append("identify/");
            System.out.println(stringBuilder2.toString());
            URIBuilder builder2 = new URIBuilder(stringBuilder2.toString());
            URI uri2 = builder2.build();
            HttpPost request2 = new HttpPost(uri2);

            request2.setHeader("Content-Type", "application/json");
            request2.setHeader("Ocp-Apim-Subscription-Key", "3d1818c14a1a41faa2283c84a09cc2ea");
            StringBuilder stringBuilder3 = new StringBuilder("{ \"personGroupId\": \"").append(groupName).append("\",");

            //stringBuilder3.append("\"faceIds\":[\"").append(faceid).append("\"]}");
            stringBuilder3.append("}");
            System.out.println("json "+stringBuilder3);

            JSONObject bodyJson = new JSONObject();
            bodyJson.put("personGroupId", groupName);
            //bodyJson.put("largePersonGroupId", "");
            JSONArray jsonfaceArray = new JSONArray();
            jsonfaceArray.put(faceid);
            JSONObject array = new JSONObject();
            bodyJson.put("faceIds", jsonfaceArray);
            //bodyJson.put("maxNumOfCandidatesReturned", 1);
            //bodyJson.put("confidenceThreshold", 0.6);
            System.out.println("json "+bodyJson.toString());

            StringEntity reqEntity = new StringEntity(bodyJson.toString());
            request2.setEntity(reqEntity);
            HttpResponse response2 = httpclient.execute(request2);
            HttpEntity entity2 = response2.getEntity();
            String res2 = EntityUtils.toString(entity2);
            System.out.println("resultado final " + res2);
            //textView.setText("hola");
            //mProgress.dismiss();

            try {
                // convert the string to JSONArray
                jsonArray = new JSONArray(res2);


                JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());
                System.out.println(jsonArray.get(0).toString());
                //JSONObject jsonObject333 = new JSONObject(jsonArray.get(i).toString());
                //System.out.println(jsonObject.toString());
                String caraid = jsonObject.getString("faceId");
                System.out.println("caraid " + caraid);
                String candid = jsonObject.getString("candidates");
                System.out.println("candid " + candid);
                JSONArray candArray = new JSONArray(candid);
                if (candArray.length()==0) {
                    System.out.println("NO_CANDIDATE");
                    return "NO_CANDIDATE";
                }
                //JSONObject theObject = new JSONObject(candArray.get(0).toString());
                System.out.println("candidate array " + candArray);
                JSONObject jsonObject2 = new JSONObject(candArray.get(0).toString());
                String personid = jsonObject2.getString("personId");

                //textView.setText(personid);
                System.out.println("SALIDA " + personid);
                return personid;
                //String emotions = "";
                // get the scores object from the results

                //resultText.setText(emotions);

            } catch (JSONException e) {
                System.out.println("excepcion jason");
            }
            return res2;
        }
        catch (Exception e){
            return "null";
        }
    }

    public static String trainFace(String groupName, Bitmap bitmap){
        //comparativa
        //String id = identiFace(groupName, bitmap);

        //si existe la cara, devovler nombre y sugerir añadir a la coleccion
            //conseguir id de esa persona
            //aádir cara
            //entrenar el sistema
        //si no se reconoce la cara
            //dialogo de insertar nombre
                //si el nombre esta dado alta
                    //persona ya dada del alta, inserte nombre nuevo
                //si no esta dado de alta
                    //dar de alta a la persona
                    //agregar cara
                    //entrenar el sistema

        return "null";
    }

    public static String addFace(String groupName, String personName, Bitmap bitmap){

        return "null";
    }

    public static String getPersonID(String groupName, String name){
        HttpClient httpclient = HttpClients.createDefault();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try { //conexion
            StringBuilder stringBuilder = new StringBuilder(Util.getBaseURL()).append("persongroups/")
                    .append(groupName).append("/persons");
            URIBuilder builder = new URIBuilder(stringBuilder.toString());
            URI uri = builder.build();
            HttpGet request = new HttpGet(uri);
            request.setHeader("Ocp-Apim-Subscription-Key", BASE_KEY);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity);
            JSONArray jsonArray = new JSONArray(res);
            try { //parseo de JSON
                for(int i = 0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());

                    String nameInSystem = jsonObject.getString("name");
                    String id = jsonObject.getString("personId");
                    if (name.equals(nameInSystem)) {
                        //System.out.println("id " +id);
                        //System.out.println("personID " +personID);
                        return id; //persona identificada, devuelve nombre
                    }
                }
            } catch (JSONException e) {
                return "Error en JSON recibido";
            }

            return "NOT_ID";
        } catch (Exception e){
            return "Conexion fallida, vuelva a intentarlo";
        }
    }

    public static String addPerson(String groupName, String name) {
        //comprobar
        if ("NOT_ID".equals(getPersonID(groupName,name))) {
            return "ERROR";
        }

        HttpClient httpclient = HttpClients.createDefault();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            StringBuilder stringBuilder = new StringBuilder(Util.getBaseURL()).append("persongroups/")
                    .append(groupName).append("/persons");

            URIBuilder builder = new URIBuilder(stringBuilder.toString());
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);

            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", BASE_KEY);
            StringBuilder stringBuilder3 = new StringBuilder("{ \"personGroupId\": \"").append(groupName).append("\",");





            StringBuilder stringBuilder2 = new StringBuilder(Util.getBaseURL()).append("identify/");
            System.out.println(stringBuilder2.toString());
            URIBuilder builder2 = new URIBuilder(stringBuilder2.toString());
            URI uri2 = builder2.build();
            HttpPut request2 = new HttpPut(uri);

            JSONObject bodyJson = new JSONObject();
            bodyJson.put("name", groupName);
            System.out.println("addPerson json " + bodyJson.toString());

            StringEntity reqEntity = new StringEntity(bodyJson.toString());
            request2.setEntity(reqEntity);
            HttpResponse response2 = httpclient.execute(request2);
            HttpEntity entity2 = response2.getEntity();
            String res2 = EntityUtils.toString(entity2);
            System.out.println("resultado final " + res2);
        } catch (Exception e){
            return "null";
        }

        return "null";
    }
}
