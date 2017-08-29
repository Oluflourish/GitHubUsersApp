package com.example.android.githubusersproject;

import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import com.squareup.picasso.Picasso;

/**
 * Created by Flourish on 30/07/2017.
 */
public class UserAdapter extends ArrayAdapter<User> {

    /** Constructor **/
    public UserAdapter(Activity context, List<User> users) {
        super(context, 0, users);
    }

    /**
     * Cache of the children views for a list item.
     */
    private static class ViewHolder {
        public TextView tvName;
        public TextView tvEmail;
        public TextView tvFollowers;
        public ImageView imageView;
    }

    /*
    * Returns a list item that displays information about the users at the given position
    * in the list of users
    * */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Checks if there is an existing list item view (called convertView) that we can reuse,
        // Otherwise, if convert view is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.user_list_item, parent, false);
        }

        // Find user at the given position in the list of users
        User currentUser = getItem(position);

        ViewHolder viewHolder= new ViewHolder();

        // Find the corresponding views
        viewHolder.tvName = (TextView) listItemView.findViewById(R.id.user_name);
        viewHolder.tvEmail = (TextView) listItemView.findViewById(R.id.user_email);
        viewHolder.tvFollowers = (TextView) listItemView.findViewById(R.id.user_followers);
        viewHolder.imageView = (ImageView) listItemView.findViewById(R.id.user_image);

        String followers = String.valueOf(currentUser.getFollowers());
        viewHolder.tvFollowers.setText(followers);

        // Display corresponding data into the TextViews
        viewHolder.tvName.setText(currentUser.getName());
        viewHolder.tvEmail.setText(currentUser.getEmail());

        //Picasso.with(getContext()).load(Uri.parse(currentUser.getAvatarUrl())).error(R.mipmap.ic_user).resize(100,100).into(imageView);
        Picasso.with(getContext())
                .load(Uri.parse(currentUser.getAvatarUrl()))
                //.error(R.mipmap.ic_user)
                .error(R.mipmap.ic_launcher)
                .transform(new CircleTransform())
                .resize(120,120)
                .into(viewHolder.imageView);

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

}
