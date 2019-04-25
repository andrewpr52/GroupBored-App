package com.terminalreach.groupbored;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Login");
        }

        Button loginButton = findViewById(R.id.submitLoginButton);
        final EditText unameEditText = findViewById(R.id.usernameEditText);
        final EditText pwordEditText = findViewById(R.id.passwordEditText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uname = unameEditText.getText().toString();
                String pword = pwordEditText.getText().toString();

                checkUserLogin(uname, pword);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkUserLogin(final String username, final String password) {
        class UserLogin extends AsyncTask<String, String, String> {
            public void onPreExecute() {
            }

            @Override
            protected String doInBackground(String... arg0) {
                try {
                    String hostname = "arodsg.com";
                    String uname = arg0[0];
                    String pword = arg0[1];
                    String link = "http://" + hostname + "/android_connect/check_login.php";
                    URL url = new URL(link);

                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(uname,"UTF-8")+"&"
                            +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(pword,"UTF-8");

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
                }
                catch (Exception e) {
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
                        JSONArray userArray = object.getJSONArray("user");
                        JSONObject user = userArray.getJSONObject(0);

                        int uid = user.getInt("ID");
                        String username = user.getString("Username");
                        String password = user.getString("Password");
                        String bio = user.getString("UserBio");
                        String joinDate = user.getString("JoinDate");

                        Set<String> following = new HashSet<>();

                        String hostname = "arodsg.com";

                        GetPostActivity postActivity = new GetPostActivity("http://" + hostname + "/android_connect/get_followed_users.php?parentid=" + uid);
                        try {
                            String result2 = postActivity.execute().get();

                            try {
                                JSONObject object2 = new JSONObject(result2);
                                int success2 = object2.getInt("success");

                                if (success2 == 1) {
                                    JSONArray followedUsersArray = object2.getJSONArray("followed_users").getJSONArray(0);
                                    int numFollowing = followedUsersArray.length();

                                    for (int i = numFollowing - 1; i >= 0; i--) {
                                        JSONObject followedUser = followedUsersArray.getJSONObject(i);
                                        String followedID = followedUser.getString("ID");
                                        following.add(followedID);
                                    }
                                }
                            }
                            catch (JSONException e) {
                                e.printStackTrace();

                                String toastMessage = "Error retrieving data from server.";
                                displayToastMessage(toastMessage);
                            }
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();

                            String toastMessage = "Error retrieving posts, please try again.";
                            displayToastMessage(toastMessage);
                        }
                        catch (ExecutionException e) {
                            e.printStackTrace();

                            String toastMessage = "Error retrieving posts, please try again.";
                            displayToastMessage(toastMessage);
                        }

                        SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                        SharedPreferences.Editor Ed = sp.edit();
                        Ed.putString("uid", Integer.toString(uid));
                        Ed.putString("uname", username);
                        Ed.putString("pword", password);
                        Ed.putString("bio", bio);
                        Ed.putString("joindate", joinDate);
                        Ed.putStringSet("following", following);
                        Ed.apply();

                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    displayToastMessage(message);
                }
                catch (JSONException e) {
                    e.printStackTrace();

                    String toastMessage = "Error contacting server. Try again later.";
                    displayToastMessage(toastMessage);
                }
            }
        }
        UserLogin userLogin = new UserLogin();
        userLogin.execute(username, password);
    }

    private void displayToastMessage(String message) {
        Toast toast = Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT);
        TextView v = toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }
}
