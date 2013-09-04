package org.sphindroid.sample.call.service.command;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sphindroid.core.service.SfdCoreFactory;
import org.sphindroid.core.service.grammar.GenusEnum;
import org.sphindroid.core.service.grammar.LithuanianGrammarHelperImpl;
import org.sphindroid.sample.call.service.aidl.AsrCommandParcelable;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.SparseArray;

public class WeatherAsrCommand extends AbstractTtsAsrCommand {
	private static final Logger LOG = LoggerFactory
			.getLogger(WeatherAsrCommand.class);

	private static final String KEY_COMMAND = "WHAT_WEATHER_IS";
	public static final String COMMAND_TRANSCRIPTION = "KOKS ORAS";
	public static final SparseArray<String> resolveWeatherMap = new SparseArray<String>();

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

	public WeatherAsrCommand(Context context) {
		super(context);
	}

	@Override
	public boolean isSupports(AsrCommandParcelable commandDto) {
		return COMMAND_TRANSCRIPTION.equals(commandDto.getCommandName());
	}

	@Override
	public Set<String> getDictionary() {
		Set<String> dict = new HashSet<String>();
		for (String string : COMMAND_TRANSCRIPTION.split(" ")) {
			dict.add(string);
		}
		return dict;
	}

	@Override
	public Map<String, String> getCommandMap() {
		Map<String, String> cmdMap = new HashMap<String, String>();
		cmdMap.put(KEY_COMMAND, COMMAND_TRANSCRIPTION);
		return cmdMap;
	}

	@Override
	public AsrCommandResult execute(AsrCommandParcelable commandDto) {
		String weatherForSpeech = createWeatherForSpeech();
		LOG.debug("[execute] {}", weatherForSpeech);
		return new AsrCommandResult(speak(weatherForSpeech));
	}

	private String createWeatherForSpeech() {
		try {
			WeatherDto weather = retrieveWeather(new URL(
					"http://weather.yahooapis.com/forecastrss?w=479616&u=c"));
			return createWeatherForSpeech(weather);
		} catch (MalformedURLException e) {
			LOG.error("This cannot happen. Ever!", e);
		} catch (XmlPullParserException e) {
			LOG.error("Xml cannot be parsed", e);
		} catch (IOException e) {
			LOG.error("Something bad happend during input reading", e);
		}
		return "Nežinau";
	}

	private String createWeatherForSpeech(WeatherDto weather) {
		LithuanianGrammarHelperImpl grammarHelper = SfdCoreFactory.getInstance().createLithuanianGrammarHelper();
		String rtn = MessageFormat.format("{0}, {1} {2}", resolveWeatherMap.get(weather.code), 
				grammarHelper.resolveNumber(weather.temperature, GenusEnum.masculine).toUpperCase(), 
				grammarHelper.matchNounToNumerales(weather.temperature, "laipsnis") 
				);
		return rtn;
	}

	private WeatherDto retrieveWeather(URL url) throws XmlPullParserException,
			IOException {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(false);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(url.openConnection().getInputStream(), "UTF_8");
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
		return null;

	}

	class WeatherDto {
		Integer code;
		Integer temperature;
	}

}
