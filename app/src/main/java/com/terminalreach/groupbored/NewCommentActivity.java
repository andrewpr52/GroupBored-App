package com.terminalreach.groupbored;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class NewCommentActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comment);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("New Comment");
        }

        final String postID = getIntent().getStringExtra("postID");
        final String postUsername = getIntent().getStringExtra("username");

        final EditText commentEditText = findViewById(R.id.commentEditText);
        final Button submitBtn = findViewById(R.id.submitCommentButton);

        // put inside submitBtn listen when complete
        /*Log.d("fullscreenPostUsername", username);
        getFCMTokens(username);*/

        submitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences sp = getBaseContext().getSharedPreferences("Login", MODE_PRIVATE);

                String username = sp.getString("uname", null);
                String commentText = commentEditText.getText().toString();

                CreateCommentActivity createCommentActivity = new CreateCommentActivity();

                try {
                    String result = createCommentActivity.execute(postID, username, commentText).get();

                    try {
                        JSONObject object = new JSONObject(result);
                        String success = object.getString("success");
                        String message = object.getString("message");

                        Toast toast = Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT);
                        toast.show();

                        if (success.equals("1")) {
                            // Handles push notification for the receiving user.
                            // The ifs ensure that users don't receive a notification when they comment on their own posts.
                            if (username != null && postUsername != null) {
                                if (!username.equals(postUsername)) {
                                    getFCMTokens(postUsername);
                                }
                            }

                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                catch (ExecutionException e) {
                    e.printStackTrace();
                }
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

    private void getFCMTokens(final String username) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users").child(username).child("tokens");

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String token = childSnapshot.getKey();
                    sendFCMPush(token);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendFCMPush(String token) {
        SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        String sender = sp.getString("uname", null);

        String postID = getIntent().getStringExtra("postID");
        String postUsername = getIntent().getStringExtra("username");
        String postTimestamp = getIntent().getStringExtra("timestamp");
        String postGroup = getIntent().getStringExtra("group");
        String postContents = getIntent().getStringExtra("contents");
        String postProfilePictureURL = getIntent().getStringExtra("profilePictureURL");

        final String SERVER_KEY = "AAAArEbTU6Y:APA91bGloRkxcX7QjCmeJVZIQOTNQ3HLFDfzRplbVK8dCZEYsmFSCo6RkWgkD1bVBRHaAeWdYPt9PVsaWKZFf2CnqsJ6iUM1Sh8S_BuKfZkuHz_sKRVpu3cKKsIt01ydqQgwrOs0uzMQ";
        String msg = sender + " commented on your post!";
        String title = "New Comment";
        Log.d("onDataChangeToken", token);
        JSONObject obj = null;
        JSONObject objData;
        JSONObject dataobjData;

        try {
            obj = new JSONObject();
            objData = new JSONObject();

            objData.put("body", msg);
            objData.put("title", title);
            objData.put("sound", "default");
            objData.put("icon", "icon_name"); //icon_name
            objData.put("tag", token);
            objData.put("priority", "high");

            dataobjData = new JSONObject();
            dataobjData.put("title", title);
            dataobjData.put("text", msg);
            dataobjData.put("post_id", postID);
            dataobjData.put("post_username", postUsername);
            dataobjData.put("post_timestamp", postTimestamp);
            dataobjData.put("post_group", postGroup);
            dataobjData.put("post_contents", postContents);
            dataobjData.put("post_profile_picture_url", postProfilePictureURL);

            obj.put("to", token);
            obj.put("priority", "high");

            obj.put("data", objData);
            obj.put("data", dataobjData);
            Log.e("return here>>", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", obj,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("True", response + "");
            }
            },
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("False", error + "");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "key=" + SERVER_KEY);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        int socketTimeout = 1000 * 60;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }
}