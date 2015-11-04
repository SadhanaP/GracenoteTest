package org.sadhana.gracenotetest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.twitter.sdk.android.core.models.Tweet;

import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by Sadhana on 11/3/15.
 */
public class BuildTweet extends Tweet{

    public String searchQuery;
    public Date date;
    public String photoUrl;
    public Bitmap bitmap;


    private final SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss '+0000' yyyy");


    BuildTweet(Tweet tweet) {
        super(tweet.coordinates, tweet.createdAt, tweet.currentUserRetweet, tweet.entities, tweet.favoriteCount,
                tweet.favorited, tweet.filterLevel, tweet.id, tweet.idStr, tweet.inReplyToScreenName,
                tweet.inReplyToStatusId, tweet.inReplyToStatusIdStr, tweet.inReplyToUserId, tweet.inReplyToUserIdStr,
                tweet.lang, tweet.place, tweet.possiblySensitive, tweet.scopes, tweet.retweetCount,
                tweet.retweeted, tweet.retweetedStatus, tweet.source, tweet.text, tweet.truncated,
                tweet.user, tweet.withheldCopyright, tweet.withheldInCountries, tweet.withheldScope);

        // First the get the first word of the tweet to pass as search query to flickr
        searchQuery=getSearchTerm(tweet.text);
        //pass this term to flickr as search parameter
        photoUrl= new FlickrFetcher().fetchURL(searchQuery);
        // Convert the url to bitmap
        bitmap = getBitmap(photoUrl);

        try {
            date = format.parse(tweet.createdAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String getSearchTerm(String text) {
        String[] words = text.split(" ");
        return words[0];
    }

    // Pass the url download the bitmap simultaneously
    private Bitmap getBitmap(final String urlString) {
        Bitmap bitmap = null;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Bitmap> callable = new Callable<Bitmap>() {
            public Bitmap call() throws Exception {
                Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(urlString).getContent());
                return bitmap;
            }
        };
        FutureTask<Bitmap> futureTask = new FutureTask<Bitmap>(callable);
        executor.execute(futureTask);
        try {
            bitmap = futureTask.get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}
