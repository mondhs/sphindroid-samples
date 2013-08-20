package org.sphindroid.sample.call.service.command;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public abstract class AbstractTtsAsrCommand extends AbstractAsrCommand implements OnInitListener {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractTtsAsrCommand.class); 
	
	private TextToSpeech tts;

	public AbstractTtsAsrCommand(Context context) {
		super(context);
		tts = new TextToSpeech(context, this);
//		tts.setEngineByPackageName(enginePackageName)
	}

	/**
	 * {@link OnInitListener#onInit(int)}
	 */
	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(Locale.US);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				LOG.error("This Language is not supported");
			}
		} else {
			LOG.error("TTS Initilization Failed!");
		}
	}

	public boolean speak(String message) {
		tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
		return true;
	}

}
