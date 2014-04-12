package org.sphindroid.sample.command.executor;

import android.content.Context;

/**
 * Created by mgreibus on 14.3.23.
 */
public interface GeneralCommand {
    String execute(String command, Context context);
    boolean isSupport(String command);
    String retrieveCommandSample();
}
