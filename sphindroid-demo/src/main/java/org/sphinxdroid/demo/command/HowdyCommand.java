package org.sphinxdroid.demo.command;

/**
 * Created by mgreibus on 14.3.23.
 */
public class HowdyCommand implements GeneralCommand {
    private static final String HOW_ARE_YOU= "KAIP SEKASI";

    @Override
    public String execute(String command) {
        return "Dėkui, gerai. Kaip tau?";
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
