package com.terminalreach.groupbored;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

public class ChangeVoteActivity extends AsyncTask<String, String, String> {

    ChangeVoteActivity() {
    }

    public void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... arg0) {
        try {
            String hostname = "arodsg.com";
            String userID = arg0[0];
            String postId = arg0[1];
            String action = arg0[2];
            String link = "http://" + hostname + "/android_connect/change_post_vote.php?uid=" + userID + "&pid=" + postId + "&action=" + action;

            URI uri = new URI(link);
            link = uri.toASCIIString();
            URI uri2 = new URI(link);


            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();

            request.setURI(uri2);
            HttpResponse response = client.execute(request);
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                sb.append(line);
            }

            in.close();
            return sb.toString();
        }
        catch(Exception e) {
            return "Exception: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
    }

}
