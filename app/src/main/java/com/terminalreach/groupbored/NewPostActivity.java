package com.terminalreach.groupbored;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class NewPostActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("New Post");
        }

        final Spinner dropdown = findViewById(R.id.groupSpinner);
        String[] items = new String[]{"Select a group", "General", "Games", "News/Politics", "Science", "Sports", "Technology"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        final EditText postEditText = findViewById(R.id.postEditText);
        final Button submitBtn = findViewById(R.id.submitPostButton);

        final Context context = this.getBaseContext();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences sp = context.getSharedPreferences("Login", MODE_PRIVATE);

                String selectedGroup = dropdown.getSelectedItem().toString();
                String username = sp.getString("uname", null);
                String postText = postEditText.getText().toString();

                if (selectedGroup.equals("Select a group")) {
                    Toast.makeText(context, "Please select a group to post in.", Toast.LENGTH_SHORT).show();
                }
                else {
                    CreatePostActivity createPostActivity = new CreatePostActivity();

                    try {
                        String result = createPostActivity.execute(selectedGroup, username, postText).get();

                        try {
                            JSONObject object = new JSONObject(result);
                            String success = object.getString("success");
                            String message = object.getString("message");


                            if (success.equals("1")) {
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                intent.putExtra("groupName", selectedGroup);
                                finish();
                            }

                            displayToastMessage(message);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();

                            String toastMessage = "Error contacting the server. Try again later.";
                            displayToastMessage(toastMessage);
                        }
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();

                        String toastMessage = "Error creating the post. Try again later.";
                        displayToastMessage(toastMessage);
                    }
                    catch (ExecutionException e) {
                        e.printStackTrace();

                        String toastMessage = "Error creating the post. Try again later.";
                        displayToastMessage(toastMessage);
                    }
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

    private void displayToastMessage(String message) {
        Toast toast = Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT);
        TextView v = toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }
}
