package com.terminalreach.groupbored;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.picasso.transformations.CropSquareTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static android.content.Context.MODE_PRIVATE;

/**
 * Original ListViewAdapter code template created by muhammadchehab on 12/10/17.
 * Modified by Andrew Rodeghero for specific GroupBored functionality.
 * Last modified: April 23, 2019
 */

public class ListViewAdapter extends BaseAdapter {

    private Context context;
    private Activity activity;
    private List<PostRow> listPostRows;
    private List<Integer> userLikes = null;
    private List<Integer> userDislikes = null;

    ListViewAdapter(Context context, Activity activity, List<PostRow> listPostRows){
        this.context = context;
        this.activity = activity;
        this.listPostRows = listPostRows;

        SharedPreferences sp = context.getSharedPreferences("Login", MODE_PRIVATE);

        if (sp.contains("uname") && sp.contains("pword")) {
            String uname = sp.getString("uname", null);
            this.userLikes = getUserLikedPosts(uname);
            this.userDislikes = getUserDislikedPosts(uname);
        }
    }

    private List<Integer> getUserLikedPosts(String username) {
        List<Integer> userLikes = new ArrayList<>();
        String hostname = "arodsg.com";

        GetPostActivity postActivity = new GetPostActivity("http://" + hostname + "/android_connect/get_user_likes.php?username=" + username);
        try {
            String result = postActivity.execute().get();

            try {
                JSONObject object = new JSONObject(result);
                // check if json array exists in result object
                if (object.has("post")) {
                    JSONArray postArray = object.getJSONArray("post").getJSONArray(0);
                    int numPosts = postArray.length();

                    // Add retrieved liked posts to the liked posts array
                    for (int i = numPosts - 1; i >= 0; i--) {
                        JSONObject post = postArray.getJSONObject(i);
                        int postID = post.getInt("ID");
                        userLikes.add(postID);
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
                String toastMessage = "Error retrieving user likes from the server.";
                displayToastMessage(toastMessage);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            String toastMessage = "Error retrieving user likes, please try again.";
            displayToastMessage(toastMessage);
        }
        catch (ExecutionException e) {
            e.printStackTrace();
            String toastMessage = "Error retrieving user likes, please try again.";
            displayToastMessage(toastMessage);
        }

        return userLikes;
    }

    private List<Integer> getUserDislikedPosts(String username) {
        List<Integer> userDislikes = new ArrayList<>();
        String hostname = "arodsg.com";

        GetPostActivity postActivity = new GetPostActivity("http://" + hostname + "/android_connect/get_user_dislikes.php?username=" + username);

        try {
            String result = postActivity.execute().get();

            try {
                JSONObject object = new JSONObject(result);
                // check if json array exists in result object
                if (object.has("post")) {
                    JSONArray postArray = object.getJSONArray("post").getJSONArray(0);
                    int numPosts = postArray.length();

                    // Add retrieved disliked posts to the disliked posts array
                    for (int i = numPosts - 1; i >= 0; i--) {
                        JSONObject post = postArray.getJSONObject(i);
                        int postID = post.getInt("ID");
                        userDislikes.add(postID);
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
                String toastMessage = "Error retrieving user dislikes from the server.";
                displayToastMessage(toastMessage);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            String toastMessage = "Error retrieving user dislikes, please try again.";
            displayToastMessage(toastMessage);
        }
        catch (ExecutionException e) {
            e.printStackTrace();
            String toastMessage = "Error retrieving user dislikes, please try again.";
            displayToastMessage(toastMessage);
        }

        return userDislikes;
    }

    @Override
    public int getCount() {
        return listPostRows.size();
    }

    @Override
    public Object getItem(int position) {
        return listPostRows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.feed_post_row, null);
            viewHolder = new ViewHolder();

            viewHolder.imageViewProfile = convertView.findViewById(R.id.imageViewProfile);
            viewHolder.textViewUsername = convertView.findViewById(R.id.textViewUsername);
            viewHolder.textViewTimestamp = convertView.findViewById(R.id.textViewTimestamp);
            viewHolder.textViewPostGroup = convertView.findViewById(R.id.textViewPostGroup);
            viewHolder.textViewPostContents = convertView.findViewById(R.id.textViewPostContents);
            viewHolder.imageViewPost = convertView.findViewById(R.id.imageViewPost);

            viewHolder.imageViewPlus = convertView.findViewById(R.id.imageViewPlus);
            viewHolder.textViewPlus = convertView.findViewById(R.id.textViewPlus);
            viewHolder.imageViewMinus = convertView.findViewById(R.id.imageViewMinus);
            viewHolder.textViewMinus = convertView.findViewById(R.id.textViewMinus);
            viewHolder.imageViewMore = convertView.findViewById(R.id.imageViewMore);
            viewHolder.imageViewComment = convertView.findViewById(R.id.imageViewComment);

            viewHolder.isClickedPlus = false;
            viewHolder.isClickedMinus = false;

            viewHolder.imageViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, FullscreenProfileActivity.class);
                    intent.putExtra("username", viewHolder.textViewUsername.getText());
                    activity.startActivity(intent);
                }
            });

            // Upvote/Plus button click handler
            viewHolder.imageViewPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sp = context.getSharedPreferences("Login", MODE_PRIVATE);

                    if (sp.contains("uname") && sp.contains("pword")) {
                        String userID = sp.getString("uid", null);
                        // If the plus button has already been pressed...
                        if (viewHolder.isClickedPlus) {
                            int oldVal = Integer.parseInt(viewHolder.textViewPlus.getText().toString());

                            viewHolder.textViewPlus.setText(String.format(Locale.getDefault(), "%d", oldVal-1));
                            viewHolder.isClickedPlus = false;
                            new ChangeVoteActivity().execute(userID, Integer.toString(listPostRows.get(position).getPostId()), "3");
                            viewHolder.imageViewPlus.setBackgroundResource(R.drawable.icon_plus_gray);
                        }
                        // If the plus button has not already been pressed...
                        else {
                            // If the plus button is pressed while the minus button is already pressed, depress minus before pressing plus
                            if (viewHolder.isClickedMinus) {
                                int oldVal = Integer.parseInt(viewHolder.textViewMinus.getText().toString());
                                viewHolder.textViewMinus.setText(String.format(Locale.getDefault(), "%d", oldVal-1));
                                viewHolder.isClickedMinus = false;
                                new ChangeVoteActivity().execute(userID, Integer.toString(listPostRows.get(position).getPostId()), "4");
                                viewHolder.imageViewMinus.setBackgroundResource(R.drawable.icon_minus_gray);
                            }
                            int oldVal = Integer.parseInt(viewHolder.textViewPlus.getText().toString());
                            viewHolder.textViewPlus.setText(String.format(Locale.getDefault(), "%d", oldVal+1));
                            viewHolder.isClickedPlus = true;
                            new ChangeVoteActivity().execute(userID, Integer.toString(listPostRows.get(position).getPostId()), "1");
                            viewHolder.imageViewPlus.setBackgroundResource(R.drawable.icon_plus);
                        }
                    }
                    else {
                        // Gets the viewPager (pager) created in MainActivity and changes the current tab to 1
                        ViewPager mPager = activity.findViewById(R.id.pager);
                        mPager.setCurrentItem(2);
                    }
                }
            });

            // Downvote/Minus button click handler
            viewHolder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sp = context.getSharedPreferences("Login", MODE_PRIVATE);

                    if (sp.contains("uname") && sp.contains("pword")) {
                        String userID = sp.getString("uid", null);
                        // If the minus button has already been pressed...
                        if (viewHolder.isClickedMinus) {
                            int oldVal = Integer.parseInt(viewHolder.textViewMinus.getText().toString());
                            viewHolder.textViewMinus.setText(String.format(Locale.getDefault(), "%d", oldVal-1));
                            viewHolder.isClickedMinus = false;
                            new ChangeVoteActivity().execute(userID, Integer.toString(listPostRows.get(position).getPostId()), "4");
                            viewHolder.imageViewMinus.setBackgroundResource(R.drawable.icon_minus_gray);
                        }
                        // If the minus button has not already been pressed...
                        else {
                            // If the minus button is pressed while the plus button is already pressed, depress minus before pressing plus
                            if (viewHolder.isClickedPlus) {
                                int oldVal = Integer.parseInt(viewHolder.textViewPlus.getText().toString());
                                viewHolder.textViewPlus.setText(String.format(Locale.getDefault(), "%d", oldVal-1));
                                viewHolder.isClickedPlus = false;
                                new ChangeVoteActivity().execute(userID, Integer.toString(listPostRows.get(position).getPostId()), "3");
                                viewHolder.imageViewPlus.setBackgroundResource(R.drawable.icon_plus_gray);
                            }
                            int oldVal = Integer.parseInt(viewHolder.textViewMinus.getText().toString());
                            viewHolder.textViewMinus.setText(String.format(Locale.getDefault(), "%d", oldVal+1));
                            viewHolder.isClickedMinus = true;
                            new ChangeVoteActivity().execute(userID, Integer.toString(listPostRows.get(position).getPostId()), "2");
                            viewHolder.imageViewMinus.setBackgroundResource(R.drawable.icon_minus);
                        }
                    }
                    else {
                        // Gets the viewPager (pager) created in MainActivity and changes the current tab to 1
                        ViewPager mPager = activity.findViewById(R.id.pager);
                        mPager.setCurrentItem(2);
                    }
                }
            });

            final PostRow postRow = listPostRows.get(position);

            viewHolder.imageViewComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Detects when an individual post is clicked on.
                    //Can pass the clicked post's information and start an activity that displays the post in fullscreen.

                    SharedPreferences sp = context.getSharedPreferences("Login", MODE_PRIVATE);
                    if (sp.contains("uname") && sp.contains("pword")) {
                        Intent intent = new Intent(activity, FullscreenPostActivity.class);
                        //intent.putExtra("profileImage", viewHolder.imageViewProfile);
                        intent.putExtra("postID", Integer.toString(viewHolder.postID));
                        intent.putExtra("username", viewHolder.textViewUsername.getText());
                        intent.putExtra("timestamp", viewHolder.textViewTimestamp.getText());
                        intent.putExtra("group", viewHolder.textViewPostGroup.getText().toString());
                        intent.putExtra("contents", viewHolder.textViewPostContents.getText());
                        intent.putExtra("upvotes", viewHolder.textViewPlus.getText());
                        intent.putExtra("downvotes", viewHolder.textViewMinus.getText());
                        intent.putExtra("profilePictureURL", postRow.getImageURL());

                        activity.startActivity(intent);
                    }
                    else {
                        ViewPager mPager = activity.findViewById(R.id.pager);
                        mPager.setCurrentItem(2);
                    }
                }
            });

            viewHolder.imageViewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, viewHolder.postID);
                }
            });

            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final PostRow postRow = listPostRows.get(position);

        if (postRow.getImageURL().isEmpty()) {
            Picasso.with(context)
                    .load(R.drawable.profile)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .resize(400,400)
                    .transform(new CropSquareTransformation())
                    .transform(new RoundedCornersTransformation(50,0))
                    .into(viewHolder.imageViewProfile);
        }
        else {
            Picasso.with(context)
                    .load(postRow.getImageURL())
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .resize(400,400)
                    .transform(new CropSquareTransformation())
                    .transform(new RoundedCornersTransformation(50,0))
                    .into(viewHolder.imageViewProfile);
        }

        // Sets the individual feed_post_row layout to wrap_content if there is no image in the post.
        // If there is an image in the post, the layout is set to the appropriate dp value for 125.
        int imgHeight = 125;
        if(postRow.getPostImageOneURL().equals("none")) {
            viewHolder.imageViewPost.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            //viewHolder.imageViewPost.requestLayout();
        }
        else {
            viewHolder.imageViewPost.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, imgHeight, context.getResources().getDisplayMetrics());
        }

        final ImageView temp = viewHolder.imageViewPost;
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "OnImgClick", Toast.LENGTH_SHORT).show();
                //temp.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                //temp.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        });

        viewHolder.postID = postRow.getPostId();

        SharedPreferences sp = context.getSharedPreferences("Login", MODE_PRIVATE);

        if (sp.contains("uname") && sp.contains("pword")) {
            if (userLikes.contains(viewHolder.postID)) {
                viewHolder.isClickedPlus = true;
                viewHolder.imageViewPlus.setBackgroundResource(R.drawable.icon_plus);
                //viewHolder.imageViewMinus.setBackgroundResource(R.drawable.icon_minus_gray);
            } else if (userDislikes.contains(viewHolder.postID)) {
                viewHolder.isClickedMinus = true;
                viewHolder.imageViewMinus.setBackgroundResource(R.drawable.icon_minus);
                //viewHolder.imageViewPlus.setBackgroundResource(R.drawable.icon_plus_gray);
            }
        }

        viewHolder.textViewUsername.setText(postRow.getUsername());
        viewHolder.textViewTimestamp.setText(postRow.getTimestamp());
        viewHolder.textViewPostGroup.setText(Html.fromHtml("in <b>" + postRow.getPostGroup() + "</b> @"));
        viewHolder.textViewPostContents.setText(postRow.getPostContents());

        viewHolder.textViewPlus.setText(String.format(Locale.getDefault(), "%d", postRow.getPosRatingCount()));
        viewHolder.textViewMinus.setText(String.format(Locale.getDefault(), "%d", postRow.getNegRatingCount()));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Detects when an individual post is clicked on.
                //Can pass the clicked post's information and start an activity that displays the post in fullscreen.

                SharedPreferences sp = context.getSharedPreferences("Login", MODE_PRIVATE);
                if (sp.contains("uname") && sp.contains("pword")) {
                    Intent intent = new Intent(activity, FullscreenPostActivity.class);
                    //intent.putExtra("profileImage", viewHolder.imageViewProfile);
                    intent.putExtra("postID", Integer.toString(viewHolder.postID));
                    intent.putExtra("username", viewHolder.textViewUsername.getText());
                    intent.putExtra("timestamp", viewHolder.textViewTimestamp.getText());
                    intent.putExtra("group", viewHolder.textViewPostGroup.getText().toString());
                    intent.putExtra("contents", viewHolder.textViewPostContents.getText());
                    intent.putExtra("upvotes", viewHolder.textViewPlus.getText());
                    intent.putExtra("downvotes", viewHolder.textViewMinus.getText());
                    intent.putExtra("profilePictureURL", postRow.getImageURL());

                    activity.startActivity(intent);
                }
                else {
                    ViewPager mPager = activity.findViewById(R.id.pager);
                    mPager.setCurrentItem(2);
                }
            }
        });

        return convertView;
    }

    private void showPopup(View v, final int postID) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_more_options, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                SharedPreferences sp = context.getSharedPreferences("Login", MODE_PRIVATE);
                if (sp.contains("uname") && sp.contains("pword")) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto","sappi.ws.1@gmail.com", null));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Post Report");
                    String messagePlaceholder = "Post ID: " + Integer.toString(postID) + "\n---Enter report reason below---";
                    intent.putExtra(Intent.EXTRA_TEXT, messagePlaceholder);
                    activity.startActivity(intent);
                }
                else {
                    ViewPager mPager = activity.findViewById(R.id.pager);
                    mPager.setCurrentItem(2);
                }

                return true;
            }
        });
    }

    private void displayToastMessage(String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        TextView v = toast.getView().findViewById(android.R.id.message);
        toast.setGravity(Gravity.CENTER,0,0);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    class ViewHolder{
        int postID;

        ImageView imageViewProfile;
        TextView textViewUsername;
        TextView textViewTimestamp;
        TextView textViewPostGroup;
        TextView textViewPostContents;
        ImageView imageViewPost;

        ImageButton imageViewPlus;
        TextView textViewPlus;
        ImageButton imageViewMinus;
        TextView textViewMinus;
        ImageView imageViewComment;
        ImageView imageViewMore;


        boolean isClickedPlus;
        boolean isClickedMinus;
    }
}