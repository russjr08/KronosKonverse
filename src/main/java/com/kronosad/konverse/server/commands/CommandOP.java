package com.kronosad.konverse.server.commands;

import com.kronosad.konverse.common.packets.Packet02ChatMessage;
import com.kronosad.konverse.server.Server;

/**
 * @author Russell Richardson
 */
public class CommandOP implements ICommand {

    @Override
    public String getCommand() {
        return "/op";
    }

    @Override
    public void run(String[] args, Packet02ChatMessage packet) {

        if(args.length < 1) {
            Server.getInstance().sendMessageToClient(Server.getInstance().getNetworkUserFromUser(packet.getChat().getUser()), getHelpText());
            return;
        }
        Server.getInstance().addOP(args[0]);

    }

    @Override
    public String getHelpText() {
        return "/op <username>";
    }

    @Override
    public String getDescription() {
        return "OPs a User.";
    }

    @Override
    public boolean requiresElevation() {
        return true;
    }

    @Override
    public void runFromConsole(String[] args) {
        if(args.length <= 1) {
            System.err.println(getHelpText());
            return;
        }
        Server.getInstance().addOP(args[0]);
    }

}
