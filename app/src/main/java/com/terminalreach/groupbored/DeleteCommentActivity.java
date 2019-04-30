package com.terminalreach.groupbored;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class DeleteCommentActivity extends AsyncTask<Integer, String, String> {
    private Context context;

    DeleteCommentActivity(Context context) {
        this.context = context;
    }

    public void onPreExecute() {
    }

    @Override
    protected String doInBackground(Integer... arg0) {
        try {

            String hostname = "arodsg.com";
            String commentID = Integer.toString(arg0[0]);

            String link = "http://" + hostname + "/android_connect/delete_comment.php";
            URL url = new URL(link);

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("commentid","UTF-8")+"="+URLEncoder.encode(commentID,"UTF-8");

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
    protected void onPostExecute(String result) {}

}
