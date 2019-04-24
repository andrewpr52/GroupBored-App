package com.terminalreach.groupbored;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;

public class CreateCommentActivity extends AsyncTask<String, String, String> {

    CreateCommentActivity() {}

    public void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... arg0) {
        try {
            String hostname = "arodsg.com";
            String postID = arg0[0];
            String username = arg0[1];
            String commentText = arg0[2];

            String link = "http://" + hostname + "/android_connect/create_comment.php?postid=" + postID + "&username=" + username + "&contents=" + URLEncoder.encode(commentText, "UTF-8");


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
    protected void onPostExecute(String result) {}

}
