package com.terminalreach.groupbored;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.android.volley.toolbox.Volley.newRequestQueue;


public class Tab3 extends Fragment {
    private static final int LOGIN_REQUEST = 2;
    private static final int SIGNUP_REQUEST = 3;
    private static final int UPDATE_BIO_REQUEST = 4;

    LinearLayout holder;
    ListViewAdapter listViewAdapter;

    ViewPager viewPager;
    ImageView profileImageIv;
    TextView profileUsernameTv;
    TextView profileJoinDateTv;
    TextView profileFavoriteGroupTv;
    TextView profileNumPostsTv;
    TextView profileBioTv;

    String profileUsername, profileBio, profileJoinDate, profileFavoriteGroup, profileNumPosts, profilePictureURL;

    int postStart, postEnd;
    boolean loadMoreElements = true;
    boolean firstCreation = true;

    List<PostRow> listPostRow = new ArrayList<>();

    private SwipeRefreshLayout swipeRefresh;

    public Tab3() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView;

        SharedPreferences sp = getContext().getSharedPreferences("Login", MODE_PRIVATE);

        if (sp.contains("uname") && sp.contains("pword")) {
            rootView = inflater.inflate(R.layout.fragment_tab_profile, container, false);

            viewPager = rootView.findViewById(R.id.pager);
            swipeRefresh = rootView.findViewById(R.id.swipeRefreshProfile);
            swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimary);
            swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.colorAccent);

            profileImageIv = rootView.findViewById(R.id.imageViewProfilePage);
            profileUsernameTv = rootView.findViewById(R.id.textViewProfileUsername);
            profileJoinDateTv = rootView.findViewById(R.id.textViewProfileJoinDate);
            profileFavoriteGroupTv = rootView.findViewById(R.id.textViewProfileFavGroup);
            profileNumPostsTv = rootView.findViewById(R.id.textViewProfileNumPosts);
            profileBioTv = rootView.findViewById(R.id.textViewProfileBio);
            profileUsername = sp.getString("uname", null);

            getUserProfileInfo(profileUsername);
            setProfileInfo(rootView, profileUsername, profileJoinDate, profileFavoriteGroup, profileNumPosts, profileBio, profilePictureURL);

            Button logoutButton = rootView.findViewById(R.id.logoutButton);
            Button followButton = rootView.findViewById(R.id.followButton);

            followButton.setVisibility(View.GONE);

            profileImageIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkAndroidVersion();
                }
            });

            profileBioTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ChangeBioActivity.class);
                    startActivityForResult(intent, UPDATE_BIO_REQUEST);
                }
            });

            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getContext() != null) {
                        getContext().getSharedPreferences("Login", 0).edit().clear().apply();
                    }

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            });

            final SwipeRefreshLayout swipeRefresh = rootView.findViewById(R.id.swipeRefreshProfile);
            swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimary);
            swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.colorAccent);

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
                                    setProfileInfo(rootView, profileUsername, profileJoinDate, profileFavoriteGroup, profileNumPosts, profileBio, profilePictureURL);
                                    //Toast.makeText(getActivity(), "Feed refreshed", Toast.LENGTH_SHORT).show();
                                }
                            }).start();
                        }
                    }
            );

            NestedScrollView nv = rootView.findViewById(R.id.profileScrollView);
            nv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView nestedScrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (loadMoreElements) {
                        View view = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);

                        int diff = view.getBottom() - (nestedScrollView.getHeight() + scrollY);

                        if (diff == 0) {

                            holder.invalidate();
                            if (getView() != null) {
                                swipeRefresh.setRefreshing(true);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        createListView(getView());
                                    }
                                }).start();
                            }
                        }
                    }
                }
            });
        }
        else {
            rootView = inflater.inflate(R.layout.fragment_tab3, container, false);

            Button loginButton = rootView.findViewById(R.id.loginButton);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, LOGIN_REQUEST);
                }
            });

            Button signupButton = rootView.findViewById(R.id.signupButton);
            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), SignupActivity.class);
                    startActivityForResult(intent, SIGNUP_REQUEST);
                }
            });
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_REQUEST || requestCode == SIGNUP_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Restarts the main activity, now with the logged in user info stored
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        }
        else if (requestCode == UPDATE_BIO_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Restarts the main activity, now with the logged in user info stored
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        }
        //RESULT FROM SELECTED IMAGE
        else if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if (getContext() != null) {
                Uri imageUri = CropImage.getPickImageResultUri(getContext(), data);

                cropRequest(imageUri);

            }
        }

        //RESULT FROM CROPPING ACTIVITY
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    if (getActivity() != null) {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), result.getUri());

                        profileImageIv.setImageBitmap(bitmap);

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

                        DateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss", Locale.getDefault());
                        Date date = new Date();
                        String imageUploadTimestamp = dateFormat.format(date);
                        String imageName = profileUsername + "_" + imageUploadTimestamp + ".jpg";



                        makeRequest(encodedImage, imageName, profileUsername);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
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

    private void setProfileInfo(final View rootView, final String username, String joinDate, String favoriteGroup, String numPosts, final String bio, String profilePictureURL) {
        final String joinDateText = String.format(getResources().getString(R.string.join_date), joinDate);
        final String favoriteGroupText = String.format(getResources().getString(R.string.favorite_group), favoriteGroup);
        final String numPostsText = String.format(getResources().getString(R.string.num_posts), numPosts);

        setProfileText(username, joinDateText, favoriteGroupText, numPostsText, bio, profilePictureURL);
        createListView(rootView);
    }

    private void setProfileText(final String username, final String joinDateText, final String favoriteGroupText, final String numPostsText, final String bio, final String profilePictureURL) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    profileUsernameTv.setText(username);
                    profileJoinDateTv.setText(joinDateText);
                    profileFavoriteGroupTv.setText(favoriteGroupText);
                    profileNumPostsTv.setText(numPostsText);
                    profileBioTv.setText(bio);

                    if (profilePictureURL.isEmpty()) {
                        Picasso.with(getContext())
                                .load(R.drawable.profile)
                                .placeholder(R.drawable.profile)
                                .error(R.drawable.profile)
                                .resize(400,400)
                                .into(profileImageIv);
                    }
                    else {
                        Picasso.with(getContext())
                                .load(profilePictureURL)
                                .placeholder(R.drawable.profile)
                                .error(R.drawable.profile)
                                .resize(400,400)
                                .into(profileImageIv);
                    }
                }
            });
        }
    }

    private void createListView(final View v) {
        listPostRow.clear();
        listPostRow = createPostList();
        holder = v.findViewById(R.id.profile_list_view);

        if (getContext() != null) {
            listViewAdapter = new ListViewAdapter(getContext(), getActivity(), listPostRow);

            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
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
                        swipeRefresh.setRefreshing(false);

                        //TextView textViewPostScore = v.findViewById(R.id.textViewProfilePostScore);
                        //String postScoreText = postScore + " post score";
                        //textViewPostScore.setText(postScoreText);
                    }
                });
            }
        }
    }

    private List<PostRow> createPostList() {
        SharedPreferences sp = getContext().getSharedPreferences("Login", MODE_PRIVATE);

        String hostname = "arodsg.com";
        String uname = sp.getString("uname", null);

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

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        return listPostRow;
    }

    private void checkAndroidVersion() {
        //REQUEST PERMISSION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 555);
        } else {
            pickImage();
        }
    }

    //FOR ACTIVITY RESULT PERMISSION
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 555 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        } else {
            checkAndroidVersion();
        }
    }

    //PICK IMAGE METHOD
    public void pickImage() {
        if (getContext() != null) {
            CropImage.startPickImageActivity(getContext(), this);
        }
    }

    //CROP REQUEST JAVA
    private void cropRequest(Uri imageUri) {
        if (getContext() != null) {
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMultiTouchEnabled(true)
                    .setAspectRatio(profileImageIv.getWidth(), profileImageIv.getHeight())
                    .start(getContext(), this);
        }
    }

    private void makeRequest(final String encodedImage, final String imageName, final String username) {
        final Context context = getContext();
        if (context != null) {
            final RequestQueue requestQueue = newRequestQueue(context);

            StringRequest request = new StringRequest(Request.Method.POST, "http://arodsg.com/android_connect/update_profile_image.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("encodedstring", encodedImage);
                    map.put("imagename", imageName);
                    map.put("username", username);

                    return map;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(request);
        }
    }
}
