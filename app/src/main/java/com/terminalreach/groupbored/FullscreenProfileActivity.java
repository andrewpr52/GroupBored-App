package com.terminalreach.groupbored;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class FullscreenProfileActivity extends AppCompatActivity {

    Set<String> followedUserIDs = new HashSet<>();
    boolean isClickedFollow;

    ImageView profileImageIv;
    TextView profileUsernameTv;
    TextView profileJoinDateTv;
    TextView profileFavoriteGroupTv;
    TextView profileNumPostsTv;
    TextView profileBioTv;

    String parentUserID;
    String profileUserID, profileUsername, profileJoinDate, profileFavoriteGroup, profileNumPosts, profileBio, profilePictureURL;
    String loggedInUsername;
    int postStart, postEnd;
    boolean loadMoreElements = true;
    boolean firstCreation = true;

    List<PostRow> listPostRow = new ArrayList<>();

    View view;
    LinearLayout holder;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tab_profile);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Profile");
        }

        profileImageIv = findViewById(R.id.imageViewProfilePage);
        profileUsernameTv = findViewById(R.id.textViewProfileUsername);
        profileJoinDateTv = findViewById(R.id.textViewProfileJoinDate);
        profileFavoriteGroupTv = findViewById(R.id.textViewProfileFavGroup);
        profileNumPostsTv = findViewById(R.id.textViewProfileNumPosts);
        profileBioTv = findViewById(R.id.textViewProfileBio);

        profileUsername = getIntent().getStringExtra("username");

        NestedScrollView nv = findViewById(R.id.profileScrollView);
        view = nv.getChildAt(nv.getChildCount() - 1);

        swipeRefresh = findViewById(R.id.swipeRefreshProfile);
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
                                getUserProfileInfo(profileUsername);
                                setProfileInfo(view, profileUsername, profileJoinDate, profileFavoriteGroup, profileNumPosts, profileBio, profilePictureURL);
                            }
                        }).start();
                        setLogoutFollowButton();
                    }
                }
        );

        nv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (loadMoreElements) {
                    int diff = view.getBottom() - (nestedScrollView.getHeight() + scrollY);

                    if (diff == 0) {

                        holder.invalidate();
                        swipeRefresh.setRefreshing(true);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                createListView(view);
                            }
                        }).start();
                    }
                }
            }
        });

        getUserProfileInfo(profileUsername);
        setProfileInfo(view, profileUsername, profileJoinDate, profileFavoriteGroup, profileNumPosts, profileBio, profilePictureURL);
        setLogoutFollowButton();
    }

    private void setLogoutFollowButton() {
        Button logoutButton = findViewById(R.id.logoutButton);
        final Button followButton = findViewById(R.id.followButton);

        logoutButton.setVisibility(View.GONE);
        followButton.setVisibility(View.GONE);
        final SharedPreferences sp = getBaseContext().getSharedPreferences("Login", MODE_PRIVATE);

        if (sp.contains("uname") && sp.contains("pword")) {
            parentUserID = sp.getString("uid", null);
            loggedInUsername = sp.getString("uname", null);
            followedUserIDs = sp.getStringSet("following", null);

            logoutButton.setVisibility(View.GONE);
            followButton.setVisibility(View.VISIBLE);

            final boolean isFollowing = followedUserIDs.contains(profileUserID);

            // checks if the logged-in user is currently following the user whose profile they're viewing
            if (isFollowing) {
                followButton.setText(getResources().getString(R.string.unfollow));
                isClickedFollow = true;
            }
            else {
                followButton.setText(getResources().getString(R.string.follow));
                followButton.setMinWidth(64);
                isClickedFollow = false;
            }

            // changes the follow/unfollow button and adds/removes the user from the Following db table
            followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     if (!isClickedFollow) {
                        followButton.setText(getResources().getString(R.string.unfollow));
                        isClickedFollow = true;
                        new FollowUserActivity().execute(parentUserID, profileUserID, "1");
                        followedUserIDs.add(profileUserID);
                        sp.edit().putStringSet("following", followedUserIDs).apply();
                    }
                    else {
                        followButton.setText(getResources().getString(R.string.follow));
                        followButton.setMinWidth(64);
                        isClickedFollow = false;
                        new FollowUserActivity().execute(parentUserID, profileUserID, "2");
                        followedUserIDs.remove(profileUserID);
                        sp.edit().putStringSet("following", followedUserIDs).apply();
                    }
                }
            });

            if (profileUsername.equals(loggedInUsername)) {


                logoutButton.setVisibility(View.VISIBLE);
                followButton.setVisibility(View.GONE);

                logoutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getBaseContext().getSharedPreferences("Login", 0).edit().clear().apply();

                        Intent intent = new Intent(FullscreenProfileActivity.this, MainActivity.class); // may not be correct
                        startActivity(intent);
                        FullscreenProfileActivity.this.finish();
                    }
                });
            }
        }
    }

    private void getUserProfileInfo(final String username) {
        GetUserInfo userLogin = new GetUserInfo();

        try {
            String result = userLogin.execute(username).get();
            try {
                JSONObject object = new JSONObject(result);

                JSONArray userArray = object.getJSONArray("user");


                JSONObject user = userArray.getJSONObject(0);


                profileUserID = user.getString("ID");
                profileUsername = user.getString("Username");
                profileBio = user.getString("UserBio");
                if (profileBio.equals("")) {
                    profileBio = "No bio";
                }
                profileJoinDate = user.getString("JoinDate");
                profileFavoriteGroup = user.getString("FavoriteGroup");
                profileNumPosts = user.getString("NumPosts");
                profilePictureURL = user.getString("ProfilePictureURL");
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

    private void setProfileInfo(final View v, String username, String joinDate, String favoriteGroup, String numPosts, String bio, String profilePictureURL) {
        String joinDateText = String.format(getResources().getString(R.string.join_date), joinDate);
        String favoriteGroupText = String.format(getResources().getString(R.string.favorite_group), favoriteGroup);
        String numPostsText = String.format(getResources().getString(R.string.num_posts), numPosts);

        setProfileText(username, joinDateText, favoriteGroupText, numPostsText, bio, profilePictureURL);

        // Loads the post listview after the other profile info is set.
        // The app switches to the profile page after the info is set, then the post listview is loaded.
        // This is more responsive and gives the user the visual feedback that the posts are loading, rather
        //  than waiting to switch to the profile after the posts are loaded.
        new Thread(new Runnable() {
            @Override
            public void run() {
                createListView(v);
            }
        }).start();
    }

    private void setProfileText(final String username, final String joinDateText, final String favoriteGroupText, final String numPostsText, final String bio, final String profilePictureURL) {
        FullscreenProfileActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                profileUsernameTv.setText(username);
                profileJoinDateTv.setText(joinDateText);
                profileFavoriteGroupTv.setText(favoriteGroupText);
                profileNumPostsTv.setText(numPostsText);
                profileBioTv.setText(bio);

                if (profilePictureURL.isEmpty()) {
                    Picasso.with(getBaseContext())
                            .load(R.drawable.profile)
                            .placeholder(R.drawable.profile)
                            .error(R.drawable.profile)
                            .resize(400,400)
                            .into(profileImageIv);
                }
                else {
                    Picasso.with(getBaseContext())
                            .load(profilePictureURL)
                            .placeholder(R.drawable.profile)
                            .error(R.drawable.profile)
                            .resize(400,400)
                            .into(profileImageIv);
                }
            }
        });
    }

    private void createListView(final View v) {
        listPostRow.clear();
        listPostRow = createPostList();
        holder = v.findViewById(R.id.profile_list_view);

        if (getBaseContext() != null) {
            final ListViewAdapter listViewAdapter = new ListViewAdapter(getBaseContext(), FullscreenProfileActivity.this, listPostRow);

            FullscreenProfileActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Sets the user's post score by adding all of their post's plus ratings and subtracting minus ratings
                    //int postScore = 0;

                    if (holder != null) {
                        holder.removeAllViews();
                        for (int i = 0; i < listPostRow.size(); i++) {
                            holder.addView(listViewAdapter.getView(i, null, holder));
                            //postScore += listPostRow.get(i).getPosRatingCount();
                            //postScore -= listPostRow.get(i).getNegRatingCount();
                        }
                    }
                    if (swipeRefresh != null) {
                        swipeRefresh.setRefreshing(false);
                    }

                    //TextView textViewPostScore = v.findViewById(R.id.textViewProfilePostScore);
                    //String postScoreText = postScore + " post score";
                    //textViewPostScore.setText(postScoreText);
                }
            });
        }
    }

    private List<PostRow> createPostList() {
        String hostname = "arodsg.com";
        String uname = profileUsername;

        GetPostActivity postActivity = new GetPostActivity("http://" + hostname + "/android_connect/get_user_posts.php?username=" + uname);

        try {
            String result = postActivity.execute().get();


            try {
                JSONObject object = new JSONObject(result);
                JSONArray postArray = object.getJSONArray("post").getJSONArray(0);

                if (firstCreation) {
                    postEnd = postArray.length();
                    postStart = postEnd - 25;
                    firstCreation = false;
                }

                if (postStart < 0) {
                    postStart = 0;
                    loadMoreElements = false;
                }

                for (int i = postArray.length() - 1; i >= postStart; i--) {
                    JSONObject post = postArray.getJSONObject(i);
                    int postId = post.getInt("ID");
                    String timestamp = post.getString("Timestamp");
                    String group = post.getString("GroupName");
                    String username = post.getString("Username");
                    String contents = post.getString("Contents");
                    int upvotes = post.getInt("Upvotes");
                    int downvotes = post.getInt("Downvotes");
                    String profilePictureURL;
                    if (post.has("ProfilePictureURL")) {
                        profilePictureURL = post.getString("ProfilePictureURL");
                    }
                    else {
                        profilePictureURL = "";
                    }

                    listPostRow.add(new PostRow(
                            postId,
                            profilePictureURL,
                            username,
                            timestamp,
                            group,
                            contents,
                            null,
                            upvotes,
                            downvotes)
                    );
                }
                postEnd = postStart;
                postStart = postEnd - 25;
            }
            catch (JSONException e) {
                e.printStackTrace();

                String toastMessage = "Error retrieving post data from the server.";
                displayToastMessage(toastMessage);
            }

        }
        catch (InterruptedException e) {
            e.printStackTrace();

            String toastMessage = "Error retrieving post data. Please try again later.";
            displayToastMessage(toastMessage);
        }
        catch (ExecutionException e) {
            e.printStackTrace();

            String toastMessage = "Error retrieving post data. Please try again later.";
            displayToastMessage(toastMessage);
        }
        return listPostRow;
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
        Toast toast = Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG);
        TextView v = toast.getView().findViewById(android.R.id.message);
        toast.setGravity(Gravity.CENTER,0,0);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }
}
