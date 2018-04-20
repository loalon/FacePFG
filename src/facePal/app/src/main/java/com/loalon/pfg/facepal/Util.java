package com.loalon.pfg.facepal;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

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

import static com.loalon.pfg.facepal.GlobalClass.context;


/**
 * Clase que contiene metodos estaticos independientes del GUI
 * Sirve como la clase "engine" para poder portarla a otras aplicaciones
 *
 * Created by Alonso on 29/03/2018.
 * @author Alonso Serrano
 * @version 180413
 *
 */
public class Util {

    /**
     * Convierte un bitmap a byte array base64
     * Requisito de Azure
     * @param bitmap imagen a enviar
     * @return array de bytes en base64
     */
    public static byte[] toBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * Devuelve URL base
     * @return URL base
     */
    public static String getBaseURL() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String serverName = prefs.getString("sServer_text", "northeurope");

        StringBuilder stringBuilder = new StringBuilder("https://")
                .append(serverName)
                .append(".api.cognitive.microsoft.com/face/v1.0/");
        return stringBuilder.toString();
    }

    /**
     * Devuelve clave de suscripcion
     * @return clave de suscripcion
     */
    public static String getKey() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String key = prefs.getString("sKey_text", "xxxxxx");
        return key;
    }

    /**
     * Devuelve nombre de grupo de personas
     * @return nombre de grupo de personas
     */
    public static String getGroupName() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String groupName = prefs.getString("groupName_text", "conocidos");
        return groupName;
    }

    /**
     * Deteccion de caras
     * @param bitmap imagen para detectar si existe una cara
     * @return imagen recortada solo con un rostro o null si existen 0 o mas de 1 cara
     */
    public static Bitmap detectFace(Bitmap bitmap) {
        Bitmap newBitmap;
        FaceDetector faceDetector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false) //acelera el reconocimiento
                .setMode(FaceDetector.FAST_MODE)
                .setClassificationType(FaceDetector.NO_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.NO_LANDMARKS)
                .build();

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);

        int x; //pos x
        int y; //pos y
        int w; //ancho
        int h; //alto

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

            newBitmap=Bitmap.createBitmap(bitmap, x, y, w, h);

            // Almacena la imagen temporalmente para una carga más rapida en el imageView
            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File mypath = new File(directory,"tempFace.jpg");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(mypath);
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

            faceDetector.release(); //libera recursos, sino no se puede llamar de nuevo
            return newBitmap;
        }
    }

    /**
     * Recupera el nombre real de una persona mediante su ID
     * @param personID id de la persona
     * @return nombre de la persona
     */
    public static String getName(String personID) {
        HttpClient httpclient = HttpClients.createDefault();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try { //conexion
            StringBuilder stringBuilder = new StringBuilder(Util.getBaseURL())
                    .append("persongroups/")
                    .append(getGroupName()).append("/persons");

            URIBuilder builder = new URIBuilder(stringBuilder.toString());
            URI uri = builder.build();
            HttpGet request = new HttpGet(uri);
            request.setHeader("Ocp-Apim-Subscription-Key", getKey());
            //Respuesta
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity);
            String checkRes = catchJSONerror(res);

            if (!checkRes.equals("NO_ERROR")) {
                System.out.println("salida checkRes " + checkRes);
                return checkRes;
            }
            JSONArray jsonArray = new JSONArray(res);
            try { //parseo de JSON
                for(int i = 0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    String id = jsonObject.getString("personId");
                    String name = jsonObject.getString("name");

                    if (id.equals(personID)) {
                        return "Identificacion: " + name; //persona identificada, devuelve nombre
                    }
                }
            } catch (JSONException e) {
                return "Error en JSON recibido";
            }

            return "Sujeto desconocido";
        } catch (Exception e){
            return "Conexion fallida, vuelva a intentarlo más tarde";
        }
    }

    /**
     * Identifica una persona mediante una cara recortada
     * @param bitmap imagen de cara recortada
     * @return identificador del mejor candidato
     */
    public static String identiFace (Bitmap bitmap){
        HttpClient httpclient = HttpClients.createDefault();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            /* SECCION DETECCION */
            StringBuilder stringBuilder = new StringBuilder(Util.getBaseURL()).append("detect/");
            System.out.println(stringBuilder.toString());

            URIBuilder builder = new URIBuilder(stringBuilder.toString());
            builder.setParameter("returnFaceId", "true");
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", getKey());
            request.setEntity(new ByteArrayEntity(Util.toBase64(bitmap))); //imagen en base64

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity);
            String checkRes = catchJSONerror(res);
            if (!checkRes.equals("NO_ERROR")) {
                return checkRes;
            }
            if (res.equals("[]")) {
                return "NO_FACE_IMAGE";
            }

            JSONArray jsonArray = null;
            String faceid;
            try { //obtencio de JSON
                jsonArray = new JSONArray(res);
                JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());
                faceid = jsonObject.getString("faceId");
            } catch (JSONException e) {
                faceid = "null";
                return "null";
            }

            /* SECCION IDENTIFICACION */
            StringBuilder stringBuilder2 = new StringBuilder(Util.getBaseURL()).append("identify/");

            URIBuilder builder2 = new URIBuilder(stringBuilder2.toString());
            URI uri2 = builder2.build();
            HttpPost request2 = new HttpPost(uri2);

            request2.setHeader("Content-Type", "application/json");
            request2.setHeader("Ocp-Apim-Subscription-Key", getKey());
            StringBuilder stringBuilder3 = new StringBuilder("{ \"personGroupId\": \"").append(getGroupName()).append("\",");
            stringBuilder3.append("}");

            JSONObject bodyJson = new JSONObject();
            bodyJson.put("personGroupId", getGroupName());

            JSONArray jsonfaceArray = new JSONArray();
            jsonfaceArray.put(faceid);
            JSONObject array = new JSONObject();
            bodyJson.put("faceIds", jsonfaceArray);

            StringEntity reqEntity = new StringEntity(bodyJson.toString());
            request2.setEntity(reqEntity);
            HttpResponse response2 = httpclient.execute(request2);
            HttpEntity entity2 = response2.getEntity();
            String res2 = EntityUtils.toString(entity2);
            String checkRes2 = catchJSONerror(res2);
            if (!checkRes2.equals("NO_ERROR")) {
                return checkRes2;
            }

            try {
                jsonArray = new JSONArray(res2);
                JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());
                String caraid = jsonObject.getString("faceId");
                String candid = jsonObject.getString("candidates");
                JSONArray candArray = new JSONArray(candid);
                if (candArray.length()==0) {
                    return "NO_CANDIDATE";
                }
                JSONObject jsonObject2 = new JSONObject(candArray.get(0).toString());
                String personid = jsonObject2.getString("personId");
                return personid;
            } catch (JSONException e) {
                System.out.println("excepcion json");
            }
            return res2;
        }
        catch (Exception e){
            return "null";
        }
    }

    /**
     * Indica a Azure que debe entrenar el grupo
     * @param groupName nombre del grupo de personas a entrenar
     * @return resultado de la operacion, null si ha tenido exito
     */
    public static String trainGroup(String groupName){
        HttpClient httpclient = HttpClients.createDefault();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            StringBuilder stringBuilder = new StringBuilder(Util.getBaseURL()).append("persongroups/")
                    .append(groupName).append("/train");

            URIBuilder builder = new URIBuilder(stringBuilder.toString());
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", getKey());

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity);
            String checkRes = catchJSONerror(res);
            if (!checkRes.equals("NO_ERROR")) {
                return checkRes;
            }
        } catch (Exception e){
            return "ERROR";
        }
        return "null";
    }

    /**
     * Indica a Azure que debe entrenar el grupo
     * @param groupName nombre del grupo de personas a entrenar
     * @param bitmap imagen para añadir
     * @param personName nombre de la persona a añadir
     * @return resultado de la operacion
     */
    public static String addFace(String groupName, String personName, Bitmap bitmap){
        HttpClient httpclient = HttpClients.createDefault();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String personID = getPersonID(groupName,personName);
        try {
            StringBuilder stringBuilder = new StringBuilder(Util.getBaseURL()).append("persongroups/")
                    .append(groupName).append("/persons/").append(personID)
                    .append("/persistedFaces");

            URIBuilder builder = new URIBuilder(stringBuilder.toString());
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", getKey());
            request.setEntity(new ByteArrayEntity(Util.toBase64(bitmap)));

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            String res2 = EntityUtils.toString(entity);
            String res3 = trainGroup(groupName);
            return res2;

        } catch (Exception e){
            return "ERROR";
        }
    }
    /**
     * Recupera el ID de una persona
     * @param groupName nombre del grupo de personas
     * @param name nombre de la persona
     * @return Id de la persona
     */
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
            request.setHeader("Ocp-Apim-Subscription-Key", getKey());

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity);
            String checkRes = catchJSONerror(res);
            if (!checkRes.equals("NO_ERROR")) {
                System.out.println("salida checkRes " + checkRes);
                return checkRes;
            }
            JSONArray jsonArray = new JSONArray(res);
            try { //parseo de JSON
                for(int i = 0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());

                    String nameInSystem = jsonObject.getString("name");
                    String id = jsonObject.getString("personId");
                    if (name.equals(nameInSystem)) {
                        return id; //persona identificada, devuelve nombre
                    }
                }
            } catch (JSONException e) {
                return "Error en JSON recibido";
            }
            return "NO_ID";
        } catch (Exception e){
            return "Conexion fallida, vuelva a intentarlo";
        }
    }
    /**
     * Añade una persona al sistema
     * @param groupName nombre del grupo de personas a entrenar
     * @param name nombre de la persona a añadir
     * @return resultado de la operacion, null si ha tenido exito
     */
    public static String addPerson(String groupName, String name) {
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
            request.setHeader("Ocp-Apim-Subscription-Key", getKey());
            JSONObject bodyJson = new JSONObject();
                try {
                    bodyJson.put("name", name);
                } catch (JSONException e) {
                    return "Error en JSON recibido";
                }
            StringEntity reqEntity = new StringEntity(bodyJson.toString());
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity);
            String checkRes = catchJSONerror(res);
            if (!checkRes.equals("NO_ERROR")) {
                return checkRes;
            }
            String personFromJSON;

            try { //parse de JSON
                JSONObject jsonObject = new JSONObject(res);
                personFromJSON = jsonObject.getString("personId");
                return personFromJSON;
            } catch (JSONException e) {
                return "ERROR";
            }
        } catch (Exception e){
            return "null";
        }

    }

    /**
     * Verifica el contenido del JSON recibido desde Azure, si contiene error lo interpreta
     * y devuelve un mensaje claro sobre el error
     *
     * @param jsonString JSON recibido
     * @return mensaje de error o si no hay error el contenido del archivo JSON
     */

    public static String catchJSONerror(String jsonString) {
        System.out.println("JSON recibido en errorCheck " + jsonString);
        String result = "NO_ERROR";

        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            if(!jsonObject.has("error")) { //si no tiene error, es correcto
                return result;
            }
            JSONObject jsonError = new JSONObject(jsonObject.getString("error"));

            switch (jsonError.getString("code")){
                case "BadArgument":
                    return "ERROR: Error en imagen. Verifique tamaño y formato";
                case "Unspecified":
                    return "ERROR: Error en la clave de suscripción. Verifique que es correcta";
                case "QuotaExceeded":
                    return "ERROR: Se ha superado el numero de consultas para esta suscripción.";
                case "PersonGroupNotFound":
                    return "ERROR: Grupo no encontrado, verifique nombre de grupo";
                case "OperationTimeOut":
                    return "ERROR: Tiempo de espera excedido, intentelo más adelante";
                case "PersonGroupTrainingNotFinished":
                    return "ERROR: Grupo en proceso de entrenamiento. Intentelo mas adelante.";
                default:
                    return "Error desconocido";
            }
        } catch (JSONException e) { //Si no tiene "error" se asume JSON correcto
            return result;
        }
    }
}
