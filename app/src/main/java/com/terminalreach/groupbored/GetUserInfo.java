package com.terminalreach.groupbored;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

public class GetUserInfo extends AsyncTask<String, String, String> {
    public void onPreExecute() {}

    @Override
    protected String doInBackground(String... arg0) {
        try {
            String uname = arg0[0];

            String hostname = "arodsg.com";
            String link = "http://" + hostname + "/android_connect/get_user_info.php?username=" + uname;

            URI uri = new URI(link);

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();

            request.setURI(uri);
            HttpResponse response = client.execute(request);
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                sb.append(line);
            }

            in.close();
            return sb.toString();
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {}
}