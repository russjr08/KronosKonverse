package com.kronosad.konverse.server.commands;

import com.kronosad.konverse.common.packets.Packet02ChatMessage;
import com.kronosad.konverse.server.Server;

/**
 * @author Russell Richardson
 */
public class CommandHelp implements ICommand {

    @Override
    public String getCommand() {
        return "/help";
    }

    @Override
    public void run(String[] args, Packet02ChatMessage packet) {
        StringBuilder help = new StringBuilder();

        for(ICommand command : Server.getInstance().getCommands()) {
            // If the command requires you to be OP, and the user isn't an OP, skip listing the command.
            if(command.requiresElevation() && !packet.getChat().getUser().isElevated()) continue;
            help.append(command.getCommand() + " - " + command.getDescription() + "\n");
        }

        Server.getInstance().sendMessageToClient(Server.getInstance().getNetworkUserFromUser(packet.getChat().getUser()), help.toString());
    }

    @Override
    public String getHelpText() {
        return "/help";
    }

    @Override
    public String getDescription() {
        return "Lists all registered commands and their description";
    }

    @Override
    public boolean requiresElevation() { return false; }

    @Override
    public void runFromConsole(String[] args) {

    }

}
