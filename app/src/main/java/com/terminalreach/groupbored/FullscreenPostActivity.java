package com.terminalreach.groupbored;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.picasso.transformations.CropSquareTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class FullscreenPostActivity extends AppCompatActivity {
    private static final int NEW_COMMENT_REQUEST = 10;

    List<CommentRow> listCommentRow = new ArrayList<>();
    int postStart, postEnd;
    boolean loadMoreElements = true;
    boolean firstCreation = true;

    String postID;

    ListViewCommentAdapter listViewCommentAdapter;
    LinearLayout holder;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_post);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Comments");
        }

        postID = getIntent().getStringExtra("postID");
        final String username = getIntent().getStringExtra("username");
        final String timestamp = getIntent().getStringExtra("timestamp");
        final String group = getIntent().getStringExtra("group");
        final String contents = getIntent().getStringExtra("contents");
        final String profilePictureURL = getIntent().getStringExtra("profilePictureURL");

        ImageView profileIv = findViewById(R.id.fullscreen_profile_image);
        TextView usernameTv = findViewById(R.id.fullscreen_post_username);
        TextView groupTv = findViewById(R.id.fullscreen_post_group);
        TextView postContentsTv = findViewById(R.id.fullscreen_post_contents);

        if (profilePictureURL.isEmpty()) {
            Picasso.get()
                    .load(R.drawable.profile)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .resize(400,400)
                    .transform(new CropSquareTransformation())
                    .transform(new RoundedCornersTransformation(50,0))
                    .into(profileIv);
        }
        else {
            Picasso.get()
                    .load(profilePictureURL)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .resize(400,400)
                    .transform(new CropSquareTransformation())
                    .transform(new RoundedCornersTransformation(50,0))
                    .into(profileIv);
        }

        usernameTv.setText(username);
        String groupTvText = String.format(getResources().getString(R.string.posted), group, timestamp);
        groupTv.setText(groupTvText);
        postContentsTv.setText(contents);

        // Floating Action Button on the "Feed" tab
        // Creates on onClick method to control the button click
        FloatingActionButton fab = findViewById(R.id.commentFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FullscreenPostActivity.this, NewCommentActivity.class);
                intent.putExtra("postID", postID);
                intent.putExtra("username", username);
                intent.putExtra("timestamp", timestamp);
                intent.putExtra("group", group);
                intent.putExtra("contents", contents);
                intent.putExtra("profilePictureURL", profilePictureURL);
                startActivityForResult(intent, NEW_COMMENT_REQUEST);
            }
        });

        swipeRefresh = findViewById(R.id.swipeRefreshPost);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimary);
        swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.colorAccent);
        swipeRefresh.setRefreshing(true);

        swipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                firstCreation = true;
                                loadMoreElements = true;
                                createListView();
                            }
                        }).start();
                    }
                }
        );


        new Thread(new Runnable() {
            @Override
            public void run() {
                createListView();
            }
        }).start();

        // Creates a LinearLayout to store the posts in.
        //  This allows the profile header info and the post list to be displayed and scrollable together.
        LinearLayout holder = findViewById(R.id.fullscreen_comments_list_view);
        ListViewCommentAdapter listViewAdapter = new ListViewCommentAdapter(getBaseContext(), this, listCommentRow);
        holder.removeAllViews();
        for(int i = 0; i < listCommentRow.size(); i++) {
            holder.addView(listViewAdapter.getView(i,null, holder));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == NEW_COMMENT_REQUEST) {
            if (resultCode == RESULT_OK) {


                createListView();
            }
        }
    }

    private void createListView() {
        listCommentRow.clear();
        listCommentRow = createCommentList(postID);

        holder = findViewById(R.id.fullscreen_comments_list_view);
        listViewCommentAdapter = new ListViewCommentAdapter(getBaseContext(), FullscreenPostActivity.this, listCommentRow);

        FullscreenPostActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView noCommentsTv = findViewById(R.id.noCommentsTextView);
                if (listCommentRow.size() != 0) {
                    noCommentsTv.setVisibility(View.GONE);
                }
                else {
                    noCommentsTv.setVisibility(View.VISIBLE);
                }

                if (holder != null) {
                    holder.removeAllViews();
                    for (int i = 0; i < listCommentRow.size(); i++) {
                        holder.addView(listViewCommentAdapter.getView(i, null, holder));
                    }
                }
                if (swipeRefresh != null) {
                    swipeRefresh.setRefreshing(false);
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


    private List<CommentRow> createCommentList(String parentPostID) {
        String hostname = "arodsg.com";
        String link = "http://" + hostname + "/android_connect/get_post_comments.php?postid=" + parentPostID;

        GetPostActivity postActivity = new GetPostActivity(link);

        try {
            String result = postActivity.execute().get();

            try {
                JSONObject object = new JSONObject(result);
                JSONArray commentArray = object.getJSONArray("comments").getJSONArray(0);

                if (firstCreation) {
                    postEnd = commentArray.length();
                    postStart = postEnd - 25;
                    firstCreation = false;
                }

                if (postStart < 0) {
                    postStart = 0;
                    loadMoreElements = false;
                }

                for (int i = commentArray.length() - 1; i >= postStart; i--) {
                    JSONObject comment = commentArray.getJSONObject(i);
                    final int commentID = comment.getInt("ID");
                    String timestamp = comment.getString("Timestamp");
                    String username = comment.getString("Username");
                    String contents = comment.getString("Contents");
                    String profilePictureURL;
                    if (comment.has("ProfilePictureURL")) {
                        profilePictureURL = comment.getString("ProfilePictureURL");
                    }
                    else {
                        profilePictureURL = "";
                    }

                    listCommentRow.add(new CommentRow(
                            commentID,
                            profilePictureURL,
                            username,
                            timestamp,
                            contents)
                    );
                }
                postEnd = postStart;
                postStart = postEnd - 25;
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

        return listCommentRow;
    }
}
