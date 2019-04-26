package com.terminalreach.groupbored;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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

        final EditText commentEditText = findViewById(R.id.commentEditText);
        final Button submitBtn = findViewById(R.id.submitCommentButton);

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
                            //handle push notification for the receiving user.

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
}
