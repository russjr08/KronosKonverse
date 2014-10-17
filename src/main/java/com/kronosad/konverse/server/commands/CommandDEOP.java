package com.kronosad.konverse.server.commands;

import com.kronosad.konverse.common.packets.Packet02ChatMessage;
import com.kronosad.konverse.server.Server;

/**
 * Created by Russell Richardson.
 */
public class CommandDEOP implements ICommand {

    @Override
    public String getCommand() {
        return "/deop";
    }

    @Override
    public void run(String[] args, Packet02ChatMessage packet) {
        Server.getInstance().sendMessageToClient(Server.getInstance().getNetworkUserFromUser(packet.getChat().getUser()),
                "You can not de-op an OP!");
    }

    @Override
    public String getHelpText() {
        return "/deop <username>";
    }

    @Override
    public void runFromConsole(String[] args) {
        if(args.length != 1) {
            System.err.println(getHelpText());
            return;
        }
        Server.getInstance().removeOP(args[0]);
    }
}
