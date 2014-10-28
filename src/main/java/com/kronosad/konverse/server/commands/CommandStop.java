package com.kronosad.konverse.server.commands;

import com.kronosad.konverse.common.packets.Packet02ChatMessage;
import com.kronosad.konverse.common.user.User;
import com.kronosad.konverse.server.NetworkUser;
import com.kronosad.konverse.server.Server;

/**
 * @author Russell Richardson
 */
public class CommandStop implements ICommand {
    @Override
    public String getCommand() {
        return "/stop";
    }

    @Override
    public void run(String[] args, Packet02ChatMessage packet) {

    }

    @Override
    public String getHelpText() {
        return "/stop";
    }

    @Override
    public String getDescription() {
        return "Stops the server (Only runnable from console!).";
    }

    @Override
    public boolean requiresElevation() {
        return true;
    }

    @Override
    public void runFromConsole(String[] args) {
        Server.getInstance().sendMessageToAllClients("[WARNING: Server is shutting down, disconnecting all clients!]");
        for(User user : Server.getInstance().getOnlineUsers()) {
            NetworkUser networkUser = Server.getInstance().getNetworkUserFromUser(user);
            networkUser.disconnect("Server is shutting down!", true);
        }
        System.exit(0);
    }
}
