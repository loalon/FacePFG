package com.loalon.pfg.facepal;

/**
 * Created by OAA on 30/03/2018.
 */

import android.os.AsyncTask;
import android.os.StrictMode;

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
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

public class identiFace extends AsyncTask<Void, Void, String> {

        //private final ImageView img;
        private String groupName;

        identiFace(String groupName) {
            this.groupName = groupName;
        }

        // this function is called before the api call is made
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //resultText.setText("Getting results...");
            System.out.println("Getting results...");
        }

        // this function is called when the api call is made
        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpclient = HttpClients.createDefault();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                StringBuilder stringBuilder = new StringBuilder(Util.getBaseURL()).append("persongroups/");
                        //.append(groupName).append("/train");
                System.out.println(stringBuilder.toString());

                URIBuilder builder = new URIBuilder(stringBuilder.toString());
                //builder.setParameter()
                URI uri = builder.build();
                //HttpPost request = new HttpPost(uri);
                HttpGet request = new HttpGet(uri);
                //request.setHeader("Content-Type", "application/octet-stream");
                request.setHeader("Content-Type", "application/json");
                // enter you subscription key here
                request.setHeader("Ocp-Apim-Subscription-Key", "3d1818c14a1a41faa2283c84a09cc2ea");

                // Request body.The parameter of setEntity converts the image to base64
                //request.setEntity(new ByteArrayEntity(Util.toBase64(img)));
                //request.se
                // getting a response and assigning it to the string res
                HttpResponse response = httpclient.execute(request);
                HttpEntity entity = response.getEntity();
                String res = EntityUtils.toString(entity);
                System.out.println("resultado ");
                System.out.println(res);
                return res;

            }
            catch (Exception e){
                return "null";
            }

        }

        // this function is called when we get a result from the API call
        @Override
        protected void onPostExecute(String result) {
            JSONArray jsonArray = null;
            try {
                // convert the string to JSONArray
                jsonArray = new JSONArray(result);
                String emotions = "";
                // get the scores object from the results
                for(int i = 0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    JSONObject scores = jsonObject.getJSONObject("scores");
                    double max = 0;
                    String emotion = "";
                    for (int j = 0; j < scores.names().length(); j++) {
                        if (scores.getDouble(scores.names().getString(j)) > max) {
                            max = scores.getDouble(scores.names().getString(j));
                            emotion = scores.names().getString(j);
                        }
                    }
                    emotions += emotion + "\n";
                }
                //resultText.setText(emotions);

            } catch (JSONException e) {
                //resultText.setText("No emotion detected. Try again later");
            }
        }

}
