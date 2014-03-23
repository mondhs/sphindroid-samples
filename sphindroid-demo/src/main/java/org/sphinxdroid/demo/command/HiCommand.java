package org.sphinxdroid.demo.command;

/**
 * Created by mgreibus on 14.3.23.
 */
public class HiCommand implements GeneralCommand {
    private static final String HI= "LABAS";
    @Override
    public String execute(String command) {
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
