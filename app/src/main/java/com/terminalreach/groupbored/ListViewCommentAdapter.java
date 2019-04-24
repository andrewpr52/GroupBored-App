package com.terminalreach.groupbored;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropSquareTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

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

            viewHolder.imageViewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, viewHolder.commentID);
                }
            });

            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        CommentRow commentRow = listCommentRows.get(position);

        if (commentRow.getImageURL().isEmpty()) {
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

    private void showPopup(View v, final int commentID) {
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
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Comment Report");
                    String messagePlaceholder = "Comment ID: " + Integer.toString(commentID) + "\n---Enter report reason below---";
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



    class ViewHolder{
        int commentID;

        ImageView imageViewProfile;
        TextView textViewUsername;
        TextView textViewTimestamp;
        TextView textViewPostContents;

        ImageView imageViewMore;
    }
}