package org.sphindroid.sample.command.executor;

import android.content.Context;

/**
 * Created by mgreibus on 14.3.23.
 */
public class HiCommand implements GeneralCommand {
    private static final String HI= "LABAS";
    @Override
    public String execute(String command, Context context) {
        return "Laba Diena";
    }

    @Override
    public boolean isSupport(String command) {
        return HI.equals(command);
    }

    @Override
    public String retrieveCommandSample() {
        return HI;
    }
}
