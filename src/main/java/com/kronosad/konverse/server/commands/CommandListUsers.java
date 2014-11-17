package com.kronosad.konverse.server.commands;

import com.kronosad.konverse.common.packets.Packet02ChatMessage;
import com.kronosad.konverse.common.user.User;
import com.kronosad.konverse.server.NetworkUser;
import com.kronosad.konverse.server.Server;

/**
 * @author Russell Richardson
 */
public class CommandListUsers implements ICommand {
    @Override
    public String getCommand() {
        return "/list";
    }

    @Override
    public void run(String[] args, Packet02ChatMessage packet) {
        NetworkUser user = Server.getInstance().getNetworkUserFromUser(packet.getChat().getUser());

        StringBuilder builder = new StringBuilder();
        for(User connectedUser : Server.getInstance().getOnlineUsers()) {
            builder.append(String.format("\n%s (%s - %s)", connectedUser.getUsername(),
                    connectedUser.getClientInfo().getClientName(), connectedUser.getClientInfo().getClientVersion().getReadable()));
        }

        Server.getInstance().sendMessageToClient(user, "Here is a list of connected users:" + builder.toString());


    }

    @Override
    public String getHelpText() {
        return "/list";
    }

    @Override
    public String getDescription() {
        return "Returns a list of users connected to the server.";
    }

    @Override
    public boolean requiresElevation() {
        return false;
    }

    @Override
    public void runFromConsole(String[] args) {
        System.out.println("Here is a list of connected users");
        for(User connectedUser : Server.getInstance().getOnlineUsers()) {
            System.out.println(String.format("%s - %s - %s", connectedUser.getUsername(),
                    connectedUser.getClientInfo().getClientName(), connectedUser.getClientInfo().getClientVersion().getReadable()));
        }
    }
}
