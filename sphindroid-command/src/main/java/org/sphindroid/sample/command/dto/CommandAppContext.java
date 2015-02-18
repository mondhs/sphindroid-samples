package org.sphindroid.sample.command.dto;

/**
 * Created by mgreibus on 14.4.6.
 */
public class CommandAppContext {
    private boolean autoVad;
    private boolean listening;

    public boolean getAutoVad() {
        return autoVad;
    }

    public void setAutoVad(boolean autoVad) {
        this.autoVad = autoVad;
    }

    public boolean getListening() {
        return listening;
    }

    public void setListening(boolean listening) {
        this.listening = listening;
    }
}
