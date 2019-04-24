package com.terminalreach.groupbored;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

//import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class Tab2 extends Fragment {
    private static final int NEW_POST_REQUEST = 1;

    List<PostRow> listPostRow = new ArrayList<>();
    LinearLayout holder;

    ListViewAdapter listViewAdapter;

    int postStart, postEnd;
    boolean loadMoreElements = true;
    boolean firstCreation = true;
    String groupSelection = null;

    private TextView noPostsTv;
    private SwipeRefreshLayout swipeRefresh;

    public Tab2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_tab2, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            TextView titleTextView = rootView.findViewById(R.id.postFeedTitle);
            titleTextView.setText(String.valueOf(bundle.getString("groupSelection")));
            groupSelection = String.valueOf(bundle.getString("groupSelection"));
        }

        noPostsTv = rootView.findViewById(R.id.noPostsTextView);

        // Floating Action Button on the "Feed" tab
        // Creates on onClick method to control the button click
        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getContext() != null) {
                    SharedPreferences sp = getContext().getSharedPreferences("Login", MODE_PRIVATE);
                    if (sp.contains("uname") && sp.contains("pword")) {
                        Intent intent = new Intent(getActivity(), NewPostActivity.class);
                        startActivityForResult(intent, NEW_POST_REQUEST);
                    } else {
                        if (getActivity() != null) {
                            ViewPager mPager = getActivity().findViewById(R.id.pager);
                            mPager.setCurrentItem(2);
                        }
                    }
                }
            }
        });

        swipeRefresh = rootView.findViewById(R.id.swipeRefreshTab2);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimary);
        swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.colorAccent);
        swipeRefresh.setRefreshing(true);

        swipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        //spinner.setVisibility(View.VISIBLE);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                firstCreation = true;
                                loadMoreElements = true;
                                createListView(rootView, groupSelection);
                            }
                        }).start();
                    }
                }
        );

        NestedScrollView nv = rootView.findViewById(R.id.nestedScrollView);
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
                                    createListView(getView(), groupSelection);
                                }
                            }).start();
                        }
                    }
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                createListView(rootView, groupSelection);
            }
        }).start();

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (requestCode == NEW_POST_REQUEST) {
            if (resultCode == RESULT_OK) {

            }
        }*/
    }

    private void createListView(final View v, final String group) {
        listPostRow.clear();
        listPostRow = createPostList(group);
        holder = v.findViewById(R.id.feed_list_view);

        if (getContext() != null) {
            listViewAdapter = new ListViewAdapter(getContext(), getActivity(), listPostRow);

            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.removeAllViews();
                        for (int i = 0; i < listPostRow.size(); i++) {
                            holder.addView(listViewAdapter.getView(i, null, holder));
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }
    }

    private List<PostRow> createPostList(String groupName) {
        String hostname = "arodsg.com";
        String link;

        SharedPreferences sp = getContext().getSharedPreferences("Login", MODE_PRIVATE);
        String parentID;

        if (groupName == null || groupName.equals("All Posts")) {
            link = "http://" + hostname + "/android_connect/get_all_posts.php";
        }
        else if (groupName.equals("Followed Users")) {
            if (sp.contains("uid") && sp.contains("following") && sp.getStringSet("following", null).size() > 0) {
                parentID = sp.getString("uid", null);
                noPostsTv.setVisibility(View.GONE);
                link = "http://" + hostname + "/android_connect/get_followed_user_posts.php?parentid=" + parentID;
            }
            else {
                noPostsTv.setVisibility(View.VISIBLE);
                link = null;
            }
        }
        else {
            link = "http://" + hostname + "/android_connect/get_group_posts.php?group=" + groupName;
        }

        if (link != null) {
            GetPostActivity postActivity = new GetPostActivity(link);

            try {
                String result = postActivity.execute().get();

                try {
                    JSONObject object = new JSONObject(result);
                    final JSONArray postArray = object.getJSONArray("post").getJSONArray(0);

                    if (firstCreation) {
                        postEnd = postArray.length();
                        postStart = postEnd - 25;
                        firstCreation = false;
                    }

                    if (postStart < 0) {
                        postStart = 0;
                        loadMoreElements = false;
                    }

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (postArray.length() == 0) {
                                    noPostsTv.setVisibility(View.VISIBLE);
                                } else {
                                    noPostsTv.setVisibility(View.GONE);
                                }
                            }
                        });
                    }

                    for (int i = postArray.length() - 1; i >= postStart; i--) {
                        JSONObject post = postArray.getJSONObject(i);

                        final int postID = post.getInt("ID");
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
                                postID,
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
        }

        return listPostRow;
    }

    private void displayToastMessage(String message) {
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        TextView v = toast.getView().findViewById(android.R.id.message);
        toast.setGravity(Gravity.CENTER,0,0);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }
}
