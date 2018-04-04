package com.loalon.pfg.facepal;

/**
 * Created by OAA on 30/03/2018.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

public class FaceIdentificator extends AsyncTask<Void, Void, String> {

    //private final ImageView img;
    private Context mContext;
    private String groupName;
    private Bitmap bitmap;
    private String consoleText;

    ProgressDialog mProgress;

    //private TaskCompleted mCallback;

    FaceIdentificator(Context context, String groupName, Bitmap bitmap, String consoleText) {
        this.bitmap = bitmap;
        this.groupName = groupName;
        this.consoleText=consoleText;
        this.mContext = context;
        //this.mCallback = (TaskCompleted) context;
    }

    // this function is called before the api call is made
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgress = new ProgressDialog(mContext);
        mProgress.setMessage("Downloading nPlease wait...");
        mProgress.show();
        //resultText.setText("Getting results...");
        //showDialog("Downloaded " + result + " bytes");
        System.out.println("Getting results...");
    }

    // this function is called when the api call is made
    @Override
    protected String doInBackground(Void... params) {
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
            return res2;
        }
        catch (Exception e){
            return "null";
        }

    }

    // this function is called when we get a result from the API call
    @Override
    protected void onPostExecute(String result) {
        System.out.println("post execute");
        System.out.println(result);
        JSONArray jsonArray = null;
        try {
            // convert the string to JSONArray
            jsonArray = new JSONArray(result);


            JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());
            System.out.println(jsonArray.get(0).toString());
            JSONArray candArray = new JSONArray(jsonObject.getJSONArray("candidates"));
            System.out.println(candArray.get(0).toString());
            JSONObject jsonObject2 = new JSONObject(candArray.get(0).toString());
            String personid = jsonObject2.getString("personId");
            consoleText="hola";
            //textView.setText(personid);
            System.out.println("salida " + personid);
            //String emotions = "";
            // get the scores object from the results

            //resultText.setText(emotions);

        } catch (JSONException e) {
            //resultText.setText("No emotion detected. Try again later");
        }
    }

}
