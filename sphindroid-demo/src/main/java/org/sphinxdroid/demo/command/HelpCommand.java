package org.sphinxdroid.demo.command;

import java.util.List;

/**
 * Created by mgreibus on 14.3.23.
 */
public class HelpCommand implements GeneralCommand {
    private static final String HELP = "KOKIAS ŽINAI KOMANDAS";
    private final List<GeneralCommand> commandList;

    public HelpCommand(List<GeneralCommand> commandList) {
        this.commandList = commandList;
    }

    @Override
    public String execute(String command) {
        StringBuilder sb = new StringBuilder();
        sb.append("Aš moku vykdyti šias komandas. ");
        String separator = ". ";
        for (GeneralCommand executor: getCommandList()){
            sb.append(executor.retrieveCommandSample()).append(separator);
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
