package org.sphindroid.sample.command.executor;

import android.content.Context;

import org.sphindroid.sample.command.util.ResponseCatalog;

/**
 * Created by mgreibus on 14.3.23.
 */
public class HowdyCommand implements GeneralCommand {
    private static final String HOW_ARE_YOU= "kaip sekasi";
    ResponseCatalog responses = new ResponseCatalog("Dėkui, gerai. Kaip tau?", "Puikiai. Kaip Jums?", "Žinot, visai gerai. O jūs kaip?");

    @Override
    public String execute(String command,Context context) {
        return responses.anyItem();
    }

    @Override
    public boolean isSupport(String command) {
        return HOW_ARE_YOU.equals(command);
    }

    @Override
    public String retrieveCommandSample() {
        return HOW_ARE_YOU;
    }
}
