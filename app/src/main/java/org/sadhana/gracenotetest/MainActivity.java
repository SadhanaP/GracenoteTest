package org.sadhana.gracenotetest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    // Twitter Key and Secret Key
    private static final String TWITTER_KEY = "HBGRNkdMjpzSrJ8RCYaOLJFth";
    private static final String TWITTER_SECRET = "q6VJ2MBZSmofBfxt0KozaWX24cBrbfheYqFDY8HqtRf1mzXzZx";
    // Twitter screen name and result count to be fetched
    private static final String TWITTER_SCREENNAME = "googleresearch";
    private static final int RESULT_COUNT = 20;
    //building tweet data
    private ArrayList<BuildTweet> tweets = new ArrayList<BuildTweet>();
    //UI elements
    private Spinner spinner;
    private ListView mListView;
    private ArrayAdapter<BuildTweet> adapter;
    private static final String[]paths = {"  ","By Date", "By Alphabets"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listView);

        // Initial setup configuration required to use Fabric will be automatically generated when configure fabric for the app
        //we must indicate which kits(single in this case) we want to use via Fabric.with()
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        //Fetch the latest twitter feeds
        fetchTweets();
        // to sort the fetched data based on date and alphabets
        sortData();
    }


    /**
     * This method is used to fetch tweets by continuing as guest user.Hitting
     * "GET statuses/user_timeline" endpoint and retrieving tweets 20 results from "googleresearch" screenname
     *
     */

    public void fetchTweets()
    {
        TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
            @Override
            public void success(Result<AppSession> appSessionResult) {
                AppSession session = appSessionResult.data;
                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                twitterApiClient.getStatusesService().userTimeline(null, TWITTER_SCREENNAME, RESULT_COUNT, null, null, false, false, false, false, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> list) {
                        // On success, retrieve the list and create a newlist for further processing
                        List<Tweet> tweetList = list.data;
                        displayList(tweetList);
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Toast.makeText(MainActivity.this, "Could not retrieve tweets", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(MainActivity.this, "Could not retrieve get user tweets", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    /**
     * This method is used to fetch images for tweets from Flickr and finally display both  the tweet and image
     * together in list using the CustomAdapter. To do that, first get the text(search criteria) from tweet, pass it to flickr,
     * using "flickr.photos.search" API endpoint and the URL from JSON body, convert the URL to bitmap and finally display them.
     *
     *  In order to do all this,iterate through tweetList and  attach each tweet to a Thread and start the list of threads together.
     */
    public void displayList(List<Tweet> tweetList) {
        // Attaching to the thread
        ArrayList<Thread> threads = new ArrayList<>();
        for(final Tweet tweet : tweetList) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    BuildTweet processedTweet = new BuildTweet(tweet);
                    tweets.add(processedTweet);

                }
            });
            threads.add(thread);
            thread.start();
        }
        // Wait until all tweets are processed
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // display the results in listview
        adapter = new CustomAdapter(this, android.R.layout.simple_list_item_1, tweets);
        mListView.setAdapter(adapter);
    }
    /**
     * This method is used for providing sorting functionality.
     * two types of sort, based on Date and text are created.
     * Created a dropdown(spinner), based on the value selected, data will be sorted
     */
    public void sortData(){
        spinner = (Spinner)findViewById(R.id.spinner);
        final ArrayAdapter<String>spinneradapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,paths);

        spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinneradapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:{
                        break;
                    }
                    case 1:
                        if (adapter != null) {
                            adapter.sort(new Comparator<BuildTweet>() {
                                @Override
                                public int compare(BuildTweet lhs, BuildTweet rhs) {
                                    return lhs.date.compareTo(rhs.date);
                                }
                            });
                            break;
                        }
                    case 2:
                        if (adapter != null) {
                            adapter.sort(new Comparator<BuildTweet>() {
                                @Override
                                public int compare(BuildTweet lhs, BuildTweet rhs) {
                                    return lhs.text.compareTo(rhs.text);
                                }
                            });
                            break;
                        }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
