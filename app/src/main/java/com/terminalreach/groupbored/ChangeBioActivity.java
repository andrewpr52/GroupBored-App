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


public class ChangeBioActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bio);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Update Info");
        }

        final EditText bioEditText = findViewById(R.id.bioEditText);
        final Button submitBtn = findViewById(R.id.submitBioButton);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences sp = getBaseContext().getSharedPreferences("Login", MODE_PRIVATE);

                String userID = sp.getString("uid", null);
                String bioText = bioEditText.getText().toString();

                new UpdateBioActivity(getBaseContext(), bioText).execute(userID, bioText);

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
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
