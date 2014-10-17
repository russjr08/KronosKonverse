package com.kronosad.konverse.server.commands;

import com.kronosad.konverse.common.packets.Packet02ChatMessage;
import com.kronosad.konverse.common.user.User;
import com.kronosad.konverse.server.Server;

/**
 * Created by Russell Richardson.
 */
public class CommandKick implements ICommand {

    @Override
    public String getCommand() {
        return "/kick";
    }

    @Override
    public void run(String[] args, Packet02ChatMessage packet) {

        if(!packet.getChat().getUser().isElevated()) {
            Server.getInstance().sendMessageToClient(Server.getInstance().getNetworkUserFromUser(packet.getChat().getUser()), "You're not authorized to run this command!");
            System.err.println(String.format("%s tried to run /kick but is unauthorized!", packet.getChat().getUser().getUsername()));
            return;
        }

        if(args.length < 1) {
            // Create a message with the help text, because the user didn't supply all of the arguments.
            Server.getInstance().sendMessageToClient(Server.getInstance().getNetworkUserFromUser(packet.getChat().getUser()), getHelpText());
            return;
        }

        for (User user : Server.getInstance().getOnlineUsers()) {
            if(user.getUsername().equals(args[0])) {
                if(user.isElevated()) {
                    Server.getInstance().sendMessageToClient(Server.getInstance().getNetworkUserFromUser(user), "Can't kick other elevated users!");
                    return;
                }
                // Kick the user from the server.
                // Get the reason from the arguments.
                StringBuilder reason = new StringBuilder();

                for (String arg : args) {
                    // Make sure we're not including the username in the reason...
                    if (arg != null && !arg.equals(args[0])) reason.append(arg + " ");
                }
                Server.getInstance().getNetworkUserFromUser(user).disconnect(reason.toString(), true);
                Server.getInstance().sendMessageToAllClients(String.format("%s was kicked for: %s", user.getUsername(), reason.toString()));

            }
        }

    }

    @Override
    public String getHelpText() {
        return "/kick <username> <reason>";
    }

    @Override
    public void runFromConsole(String[] args) {
        if(args.length != 2) {
            System.err.println(getHelpText());
            return;
        }

        Server.getInstance().getOnlineUsers().stream().filter(user -> user.getUsername().equals(args[0])).forEach(user -> {
            // Kick the user from the server.

            // Get the reason from the arguments.
            StringBuilder reason = new StringBuilder();
            for (String arg : args) {
                // Make sure we're not including the username in the reason...
                if (arg.equals(args[0])) reason.append(arg + " ");
            }
            Server.getInstance().getNetworkUserFromUser(user).disconnect(reason.toString(), true);
        });
    }

}
