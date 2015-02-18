package org.sphindroid.sample.command.executor;

import android.content.Context;

import org.sphindroid.sample.command.util.ResponseCatalog;

/**
 * Created by mgreibus on 14.3.23.
 */
public class HiCommand implements GeneralCommand {
    private static final String HI= "labas";
    ResponseCatalog responses = new ResponseCatalog("Laba Diena", "Sveiki", "Malonu Jus matyti");
    @Override
    public String execute(String command, Context context) {
        return responses.anyItem();
    }

    @Override
    public boolean isSupport(String command) {
        return HI.equals(command) || "laba diena".equals(command);
    }

    @Override
    public String retrieveCommandSample() {
        return HI;
    }
}
