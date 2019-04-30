package com.terminalreach.groupbored;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import jp.wasabeef.picasso.transformations.CropSquareTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by muhammadchehab on 12/10/17.
 * Modified by Andrew Rodeghero for specific GroupBored functionality.
 * Last modified: April 23, 2019
 */

public class ListViewCommentAdapter extends BaseAdapter {

    private Context context;
    private Activity activity;
    private List<CommentRow> listCommentRows;

    ListViewCommentAdapter(Context context, Activity activity, List<CommentRow> listCommentRows){
        this.context = context;
        this.activity = activity;
        this.listCommentRows = listCommentRows;
    }

    @Override
    public int getCount() {
        return listCommentRows.size();
    }

    @Override
    public Object getItem(int position) {
        return listCommentRows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.feed_comment_row, null);
            viewHolder = new ViewHolder();

            viewHolder.imageViewProfile = convertView.findViewById(R.id.imageViewProfile);
            viewHolder.textViewUsername = convertView.findViewById(R.id.textViewUsername);
            viewHolder.textViewTimestamp = convertView.findViewById(R.id.textViewTimestamp);
            viewHolder.textViewPostContents = convertView.findViewById(R.id.textViewPostContents);

            viewHolder.imageViewMore = convertView.findViewById(R.id.imageViewMore);

            viewHolder.imageViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, FullscreenProfileActivity.class);
                    intent.putExtra("username", viewHolder.textViewUsername.getText());
                    activity.startActivity(intent);
                }
            });

            final CommentRow commentRow = listCommentRows.get(position);
            viewHolder.imageViewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, viewHolder.commentID, commentRow.getUsername());
                }
            });

            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        CommentRow commentRow = listCommentRows.get(position);

        if (commentRow.getImageURL().isEmpty()) {
            Picasso.get()
                    .load(R.drawable.profile)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .resize(400,400)
                    .transform(new CropSquareTransformation())
                    .transform(new RoundedCornersTransformation(50,0))
                    .into(viewHolder.imageViewProfile);
        }
        else {
            Picasso.get()
                    .load(commentRow.getImageURL())
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .resize(400,400)
                    .transform(new CropSquareTransformation())
                    .transform(new RoundedCornersTransformation(50,0))
                    .into(viewHolder.imageViewProfile);
        }

        viewHolder.commentID = commentRow.getPostId();

        viewHolder.textViewUsername.setText(commentRow.getUsername());
        String timestampText = String.format(context.getResources().getString(R.string.timestamp), commentRow.getTimestamp());
        viewHolder.textViewTimestamp.setText(timestampText);
        viewHolder.textViewPostContents.setText(commentRow.getPostContents());

        return convertView;
    }

    private void showPopup(View v, final int commentID, String username) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_more_options, popup.getMenu());

        MenuItem item;
        SharedPreferences sp = context.getSharedPreferences("Login", MODE_PRIVATE);
        if (sp.getString("uname", null) != null) {
            if (sp.getString("uname", null).equals(username)) {
                item = popup.getMenu().getItem(0);
            }
            else {
                item = popup.getMenu().getItem(1);
            }
        }
        else {
            item = popup.getMenu().getItem(1);
        }
        item.setVisible(false);

        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle().equals("Report")) {
                    SharedPreferences sp = context.getSharedPreferences("Login", MODE_PRIVATE);
                    if (sp.contains("uname") && sp.contains("pword")) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "terminalreach@gmail.com", null));
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Comment Report");
                        String messagePlaceholder = "Comment ID: " + commentID + "\n---Enter report reason below---";
                        intent.putExtra(Intent.EXTRA_TEXT, messagePlaceholder);
                        activity.startActivity(intent);
                    } else {
                        ViewPager mPager = activity.findViewById(R.id.pager);
                        mPager.setCurrentItem(2);
                    }
                }
                else if (menuItem.getTitle().equals("Delete")) {
                    createAlertDialog(commentID);
                }

                return true;
            }
        });
    }

    private void createAlertDialog(final int commentID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialog)
                .setMessage("Are you sure you want to permanently delete this comment?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // call DeleteCommentActivity async class
                        try {
                            String result = new DeleteCommentActivity(context).execute(commentID).get();

                            try {
                                JSONObject object = new JSONObject(result);
                                String message = object.getString("message");
                                displayToastMessage(message);
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                                String toastMessage = "Error deleting the comment from the server, please try again.";
                                displayToastMessage(toastMessage);
                            }
                        }
                        catch (InterruptedException i) {
                            i.printStackTrace();
                            String toastMessage = "Error deleting the comment from the server, please try again.";
                            displayToastMessage(toastMessage);
                        }
                        catch (ExecutionException e) {
                            e.printStackTrace();
                            String toastMessage = "Error deleting the comment from the server, please try again.";
                            displayToastMessage(toastMessage);
                        }
                    }
                })
                .setNegativeButton("No", null);
        AlertDialog alert = builder.create();
        alert.show();

        Button negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        layoutParams.weight = 1000;
        positiveButton.setLayoutParams(layoutParams);
        negativeButton.setLayoutParams(layoutParams);
    }

    private void displayToastMessage(String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        TextView v = toast.getView().findViewById(android.R.id.message);
        toast.setGravity(Gravity.CENTER,0,0);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    class ViewHolder{
        int commentID;

        ImageView imageViewProfile;
        TextView textViewUsername;
        TextView textViewTimestamp;
        TextView textViewPostContents;

        ImageView imageViewMore;
    }
}