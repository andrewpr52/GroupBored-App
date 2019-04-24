package com.terminalreach.groupbored;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static android.content.Context.MODE_PRIVATE;

public class UpdateBioActivity extends AsyncTask<String, String, String> {
    private Context context;
    private String bioText;

    UpdateBioActivity(Context context, String bioText) {
        this.context = context;
        this.bioText = bioText;
    }

    public void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... arg0) {
        try {

            String hostname = "arodsg.com";
            String userID = arg0[0];
            String bio = arg0[1];

            String link = "http://" + hostname + "/android_connect/update_bio.php";
            URL url = new URL(link);


            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("userid","UTF-8")+"="+URLEncoder.encode(userID,"UTF-8")+"&"
                    +URLEncoder.encode("bio","UTF-8")+"="+URLEncoder.encode(bio,"UTF-8");

            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();


            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            StringBuilder result = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine())!= null) {
                result.append(line);
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            return result.toString();
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {

        try {
            JSONObject object = new JSONObject(result);
            String success = object.getString("success");
            String message = object.getString("message");


            if (success.equals("1")) {
                SharedPreferences sp = context.getSharedPreferences("Login", MODE_PRIVATE);
                Editor editor = sp.edit();
                editor.putString("bio", bioText);
                editor.apply();
            }

            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
        }
        catch (JSONException e) {
            e.printStackTrace();

        }

        //activity.finish();
    }

}
