package org.sadhana.gracenotetest;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sadhana on 11/3/15.
 *
 */

// This class is used to hit flickr search API and fetch url for each tweet
public class FlickrFetcher {

    private static final String TAG = "FlickrFetcher";
    //Query parameters
    private static final String API_KEY = "6ef1fd721fb0625732facbc13e24e859";
    private static final String EXTRA_SMALL_URL = "url_s";
    private static String TAGS;
    public String photoUrl;

    // takes the input url, converts the raw data from the url and retruns array of byte
    byte[] getUrlBytes(String urlspec) throws IOException {
        URL url = new URL(urlspec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
    //Converts bytes fetched into string
    public String getUrl(String urlspec) throws IOException {
        return new String(getUrlBytes(urlspec));
    }
    // fetchs the imageurl from the JSON object received
    public String fetchURL(String query) {
        try {
            TAGS = query;
            String JSONString = getUrl(getSearchUrl());
            JSONObject jsonObject = new JSONObject(JSONString);
            JSONObject firstImageJSONObject = jsonObject.getJSONObject("photos");
            photoUrl = firstImageJSONObject.getJSONArray("photo").getJSONObject(0).getString("url_s");
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return photoUrl;
    }

    // Builds the Flickr search URI:
   //It takes flickr api_key, search tags and format of the data to be displayed

    public static String getSearchUrl() {
        return "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=" + API_KEY + "&tags=" + TAGS + "&extras=" + EXTRA_SMALL_URL + "&format=json&nojsoncallback=1';";
    }
}
