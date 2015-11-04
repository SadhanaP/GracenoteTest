package org.sadhana.gracenotetest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sadhana on 11/3/15.
 */
public class CustomAdapter extends ArrayAdapter<BuildTweet> {
    private ArrayList<BuildTweet> mListItems;
    private LayoutInflater mLayoutInflater;
    private final Context context;

    public CustomAdapter(Context context, int resource, ArrayList<BuildTweet> tweets) {
        super(context, resource, tweets);
        this.context = context;
        this.mListItems = tweets;

        //get the layout inflater
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        // create a ViewHolder reference
        ViewHolder holder;
        BuildTweet tweet = mListItems.get(position);
        //check to see if the reused view is null or not, if is not null then reuse it
        if (view == null) {
            holder = new ViewHolder();

            view = mLayoutInflater.inflate(R.layout.tweet_item, null);
            holder.tweetView = (TextView) view.findViewById(R.id.textView);
            holder.dateView=(TextView)view.findViewById(R.id.dateView);
            holder.image=(ImageView)view.findViewById(R.id.imageView);
            // the setTag is used to store the data within this view
            view.setTag(holder);
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)view.getTag();
        }

        //get the string item from the position "position" from array list to put it on the TextView
        String stringItem = mListItems.get(position).toString();
        if (stringItem != null) {
                if(holder.tweetView!=null && holder.dateView!=null && holder.image!=null) {
                    //set the item values in corresponding views
                    holder.dateView.setText(tweet.date.toString());
                    holder.tweetView.setText(tweet.text);
                    holder.image.setImageBitmap(tweet.bitmap);
                }
        }
        //this method must return the view corresponding to the data at the specified position.
        return view;

    }


    /**
     * Static class used to avoid the calling of "findViewById" every time the getView() method is called,
     * because this can impact to your application performance when your list is too big. The class is static so it
     * cache all the things inside once it's created.
     */
    private static class ViewHolder {

        protected TextView tweetView;
        protected TextView dateView;
        protected ImageView image;

    }


}

