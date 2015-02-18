package org.sphindroid.sample.command.executor;

import android.content.Context;

import java.util.List;

/**
 * Created by mgreibus on 14.3.23.
 */
public class HelpCommand implements GeneralCommand {
    private static final String HELP = "kokias žinai komandas";
    private final List<GeneralCommand> commandList;

    public HelpCommand(List<GeneralCommand> commandList) {
        this.commandList = commandList;
    }

    @Override
    public String execute(String command, Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("Aš moku vykdyti šias komandas: ");
        String separator = ", ";
        for (GeneralCommand executor: getCommandList()){
            if(!HELP.equals(executor.retrieveCommandSample())) {
                sb.append(executor.retrieveCommandSample()).append(separator);
            }
        }
        return sb.toString();
    }

    @Override
    public boolean isSupport(String command) {
        return HELP.equals(command);
    }

    @Override
    public String retrieveCommandSample() {
        return HELP;
    }

    public List<GeneralCommand> getCommandList() {
        return commandList;
    }
}
