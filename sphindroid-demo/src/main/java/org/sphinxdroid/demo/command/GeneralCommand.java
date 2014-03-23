package org.sphinxdroid.demo.command;

/**
 * Created by mgreibus on 14.3.23.
 */
public interface GeneralCommand {
    String execute(String command);
    boolean isSupport(String command);
    String retrieveCommandSample();
}
