package org.sphindroid.sample.command.executor;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;

/**
 * Created by mgreibus on 14.3.23.
 */
public class ReadNewsCommand implements GeneralCommand {

//    private Context context;
    private static final String TAG = ReadNewsCommand.class.getSimpleName();

    private static final String READ_NEWS = "skaityk naujienas";
    ;


    @Override
    public String execute(String command, Context context) {
        String chosenPortal = command.replace("skaityk", "").replace("naujienas", "").trim();
        String newsForSpeech = "Nėra naujienų";
        if (isNetworkAvailable(context)) {
            try {
                NewsDto newsInfo = new RetreiveFeedTask().execute("http://vz.lt/RSS.aspx?type=3").get();
                newsForSpeech = createNewsForSpeech(newsInfo);
            } catch (InterruptedException e) {
                Log.e(TAG, "Something went wrong", e);
            } catch (ExecutionException e) {
                Log.e(TAG, "Something went wrong", e);
            }
        } else {
            newsForSpeech = "Neturiu interneto";
        }
        Log.d(TAG, "[execute] " + newsForSpeech);
        return newsForSpeech;
    }

    private String createNewsForSpeech(NewsDto newsInfo) {
        String contentToRead = "Neradau naujienų";
        if(newsInfo.newsContent != null){
            contentToRead = MessageFormat.format("{0}. {1}. {2}.", newsInfo.category, newsInfo.title, newsInfo.newsContent);
        }
        return contentToRead;
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isSupport(String command) {
        return command.startsWith("skaityk") && command.endsWith("naujienas");
    }

    @Override
    public String retrieveCommandSample() {
        return READ_NEWS;
    }

    class NewsDto {
        String newsContent;
        String category;
        String title;
    }

    class RetreiveFeedTask extends AsyncTask<String, Void, NewsDto> {

        @Override
        protected NewsDto doInBackground(String... urls) {
            try {
                URL givenServerUrl = new URL(urls[0]);
                NewsDto newsInfo = retrieveWeather(givenServerUrl);
                return newsInfo;
            } catch (MalformedURLException e) {
                Log.e(TAG, "This cannot happen. Ever!", e);
            } catch (XmlPullParserException e) {
                Log.e(TAG, "Xml cannot be parsed", e);
            } catch (IOException e) {
                Log.e(TAG, "Something bad happend during input reading", e);
            }
            return null;
        }

        private NewsDto retrieveWeather(URL url) throws XmlPullParserException,
                IOException {

            URLConnection connection = url.openConnection();
            try {
                connection.setDoInput(true);

                InputStream stream = connection.getInputStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(stream, "UTF_8");
                boolean insideItem = false;
                int eventType = xpp.getEventType();
                String category = null;
                String title = null;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                        } else if (xpp.getName().equalsIgnoreCase("category")) {
                            xpp.next();
                            category = xpp.getText();
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            xpp.next();
                            title = xpp.getText();
                        } else if (xpp.getName().equalsIgnoreCase("description")) {
                            if (insideItem) {
                                xpp.next();
                                NewsDto newsInfo = new NewsDto();
                                newsInfo.newsContent = xpp.getText();
                                newsInfo.category = category;
                                newsInfo.title = title;
                                return newsInfo;
                            }
                        }
                    } else if (eventType == XmlPullParser.END_TAG
                            && xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = false;
                    }

                    eventType = xpp.next(); // move to next element
                }
            } catch (Throwable t) {
                Log.e(TAG, "Something went wrong", t);
            }
            return null;

        }
    }

}
