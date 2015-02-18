package org.sphindroid.sample.command.executor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import org.sphindroid.core.service.SfdCoreFactory;
import org.sphindroid.core.service.grammar.GenusEnum;
import org.sphindroid.core.service.grammar.LithuanianGrammarHelperImpl;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Created by mgreibus on 14.3.23.
 */
public class WeatherCommand implements GeneralCommand {
    public static final SparseArray<String> resolveWeatherMap = new SparseArray<String>();

    private static final String TAG = WeatherCommand.class.getSimpleName();

    private static final String WHAT_WEATHER_IS= "koks oras";

    static {
        resolveWeatherMap.put(0, "TORNADAS");// tornado
        resolveWeatherMap.put(1, "TROPINĖ AUDRA");// tropical storm
        resolveWeatherMap.put(2, "URAGANAS");// hurricane
        resolveWeatherMap.put(3, "AUDRA");// severe thunderstorms
        resolveWeatherMap.put(4, "AUDRA");// thunderstorms
        resolveWeatherMap.put(5, "ŠLAPDRIBA");// mixed rain and snow
        resolveWeatherMap.put(6, "ŠLAPDRIBA");// mixed rain and sleet
        resolveWeatherMap.put(7, "ŠLAPDRIBA");// mixed snow and sleet
        resolveWeatherMap.put(8, "DULKSNA");// freezing drizzle
        resolveWeatherMap.put(9, "DULKSNA");// drizzle
        resolveWeatherMap.put(10, "LIETUS");// freezing rain
        resolveWeatherMap.put(11, "DULKSNA");// showers
        resolveWeatherMap.put(12, "DULKSNA");// showers
        resolveWeatherMap.put(13, "SNINGA");// snow flurries
        resolveWeatherMap.put(14, "SNINGA");// light snow showers
        resolveWeatherMap.put(15, "SNINGA");// blowing snow
        resolveWeatherMap.put(16, "SNINGA");// snow
        resolveWeatherMap.put(17, "KRUŠA");// hail
        resolveWeatherMap.put(18, "ŠLAPDRIBA");// sleet
        resolveWeatherMap.put(19, "SMĖLIO AUDRA");// dust
        resolveWeatherMap.put(20, "RŪKAS");// foggy
        resolveWeatherMap.put(21, "MIGLA");// haze
        resolveWeatherMap.put(22, "RŪKANOTA");// smoky
        resolveWeatherMap.put(23, "VĖJUOTA");// blustery
        resolveWeatherMap.put(24, "VĖJUOTA");// windy
        resolveWeatherMap.put(25, "ŠALTA");// cold
        resolveWeatherMap.put(26, "DEBESUOTA");// cloudy
        resolveWeatherMap.put(27, "DEBESUOTA");// mostly cloudy (night)
        resolveWeatherMap.put(28, "DEBESUOTA");// mostly cloudy (day)
        resolveWeatherMap.put(29, "DEBESUOTA");// partly cloudy (night)
        resolveWeatherMap.put(30, "DEBESUOTA");// partly cloudy (day)
        resolveWeatherMap.put(31, "GIEDRA");// clear (night)
        resolveWeatherMap.put(32, "SAULĖTA");// sunny
        resolveWeatherMap.put(33, "GRAŽUS ORAS");// fair (night)
        resolveWeatherMap.put(34, "GRAŽUS ORAS");// fair (day)
        resolveWeatherMap.put(35, "LIETUS");// mixed rain and hail
        resolveWeatherMap.put(36, "KARŠTA");// hot
        resolveWeatherMap.put(37, "PERKŪNIJA");// isolated thunderstorms
        resolveWeatherMap.put(38, "PERKŪNIJA");// scattered thunderstorms
        resolveWeatherMap.put(39, "PERKŪNIJA");// scattered thunderstorms
        resolveWeatherMap.put(40, "PERKŪNIJA");// scattered showers
        resolveWeatherMap.put(41, "STIPRIAI SNINGA");// heavy snow
        resolveWeatherMap.put(42, "SNINGA");// scattered snow showers
        resolveWeatherMap.put(43, "SNINGA");// heavy snow
        resolveWeatherMap.put(44, "DEBESUOTA");// partly cloudy
        resolveWeatherMap.put(45, "PERKŪNIJA");// thundershowers
        resolveWeatherMap.put(46, "SNINGA");// snow showers
        resolveWeatherMap.put(47, "PERKŪNIJA");// isolated thundershowers
        resolveWeatherMap.put(3200, "");// not available
    }





    @Override
    public boolean isSupport(String command) {
        return WHAT_WEATHER_IS.equals(command);
    }

    @Override
    public String retrieveCommandSample() {
        return WHAT_WEATHER_IS;
    }

    @Override
    public String execute(String command, Context context) {
        String weatherForSpeech = "";
        if (isNetworkAvailable(context)) {
            try {
                WeatherDto weather = new RetreiveFeedTask().execute("http://weather.yahooapis.com/forecastrss?w=479616&u=c").get();
                weatherForSpeech = createWeatherForSpeech(weather);
            } catch (InterruptedException e) {
                Log.e(TAG, "Something went wrong", e);
            } catch (ExecutionException e) {
                Log.e(TAG, "Something went wrong", e);
            }
        } else {
            weatherForSpeech = "Neturiu interneto";
        }
        Log.d(TAG, "[execute] " + weatherForSpeech);
        return weatherForSpeech;
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


    private String createWeatherForSpeech(WeatherDto weather) {
        LithuanianGrammarHelperImpl grammarHelper = SfdCoreFactory.getInstance().createLithuanianGrammarHelper();
        String rtn = "Neturiu duomenų";
        String positive="";
        if(weather != null){
            if(weather.temperature<0){
                positive = "MINUS";
                weather.temperature *= -1;
            }
            rtn = MessageFormat.format("Anot jahū. {0}, {3} {1} {2}.", resolveWeatherMap.get(weather.code),
                grammarHelper.resolveNumber(weather.temperature, GenusEnum.masculine).toUpperCase(Locale.getDefault()),
                grammarHelper.matchNounToNumerales(weather.temperature, "laipsnis"), positive);
        }
        return rtn;
    }



    class WeatherDto {
        Integer code;
        Integer temperature;
    }

    class RetreiveFeedTask extends AsyncTask<String, Void, WeatherDto> {

        @Override
        protected WeatherDto doInBackground(String... urls) {
            try {
                URL yahooServerUrl = new URL(urls[0]);
                WeatherDto weather = retrieveWeather(yahooServerUrl);
                return weather;
            } catch (MalformedURLException e) {
                Log.e(TAG, "This cannot happen. Ever!", e);
            } catch (XmlPullParserException e) {
                Log.e(TAG, "Xml cannot be parsed", e);
            } catch (IOException e) {
                Log.e(TAG, "Something bad happend during input reading", e);
            }
            return null;
        }

        private WeatherDto retrieveWeather(URL url) throws XmlPullParserException,
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
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {

                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                        } else if (xpp.getName().equalsIgnoreCase("yweather:condition")) {
                            if (insideItem) {
                                WeatherDto weatherDto = new WeatherDto();
                                weatherDto.code = Integer.valueOf(xpp.getAttributeValue("", "code"));
                                weatherDto.temperature = Integer.valueOf(xpp.getAttributeValue("",
                                        "temp"));
                                return weatherDto;
                            }
                        }
                    } else if (eventType == XmlPullParser.END_TAG
                            && xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = false;
                    }

                    eventType = xpp.next(); // move to next element
                }
            }catch (Throwable t){
                Log.e(TAG,"Something went wrong", t);
            }
            return null;

        }
    }
}
