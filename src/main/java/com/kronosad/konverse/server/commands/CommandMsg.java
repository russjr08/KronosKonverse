package com.kronosad.konverse.server.commands;

import com.kronosad.konverse.common.objects.PrivateMessage;
import com.kronosad.konverse.common.packets.Packet;
import com.kronosad.konverse.common.packets.Packet02ChatMessage;
import com.kronosad.konverse.common.user.User;
import com.kronosad.konverse.server.Server;

import java.io.IOException;

/**
 * @author Russell Richardson
 */
public class CommandMsg implements ICommand {
    @Override
    public String getCommand() {
        return "/msg";
    }

    @Override
    public void run(String[] args, Packet02ChatMessage packet) {
        if(args.length < 2) {
            Server.getInstance().sendMessageToClient(Server.getInstance().getNetworkUserFromUser(packet.getChat().getUser())
                    , getHelpText());
            return;
        }

        PrivateMessage msg = new PrivateMessage();
        for(User user : Server.getInstance().getOnlineUsers()) {

            // TODO: Fix lazy fix.
//            if(user.getUsername().equals(packet.getChat().getUser().getUsername())) {
//                Server.getInstance().sendMessageToClient(Server.getInstance().getNetworkUserFromUser(packet.getChat().getUser())
//                        , "Create yourself a note or something if you want to tell yourself something in private!");
//                return;
//            }
            if(user.getUsername().equals(args[0])) {
                // Found the user!
                StringBuilder builder = new StringBuilder();
                msg.setUser(Server.getInstance().getNetworkUserFromUser(packet.getChat().getUser()));
                msg.setRecipient(user);

                for (String arg : args) {
                    if(arg != null && !arg.equals(args[0])) {
                        builder.append(arg);
                        builder.append(" ");
                    }
                }

                msg.setMessage(builder.toString());
                Packet02ChatMessage privateMessage = new Packet02ChatMessage(Packet.Initiator.SERVER, msg);
                privateMessage.setPrivate(true);

                try {
                    Server.getInstance().sendPacketToClient(Server.getInstance().getNetworkUserFromUser(packet.getChat().getUser()), privateMessage);
                    Server.getInstance().sendPacketToClient(Server.getInstance().getNetworkUserFromUser(user), privateMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public String getHelpText() {
        return "/msg <recipient> <message>";
    }

    @Override
    public String getDescription() {
        return "Use this command to send private messages to other users.";
    }

    @Override
    public boolean requiresElevation() {
        return false;
    }

    @Override
    public void runFromConsole(String[] args) {
        if(args.length < 2) {
            System.err.println(getHelpText());
            return;
        }

        PrivateMessage msg = new PrivateMessage();
        for(User user : Server.getInstance().getOnlineUsers()) {
            if(user.getUsername().equals(args[0])) {
                // Found the user!
                StringBuilder builder = new StringBuilder();
                msg.setUser(Server.getInstance().getNetworkUserFromUser(Server.getInstance().getServerUser()));
                msg.setRecipient(user);

                for (String arg : args) {
                    if(arg != null && !arg.equals(args[0])) {
                        builder.append(arg);
                        builder.append(" ");
                    }
                }

                msg.setMessage(builder.toString());
                Packet02ChatMessage privateMessage = new Packet02ChatMessage(Packet.Initiator.SERVER, msg);
                privateMessage.setPrivate(true);

                try {
                    System.out.println(String.format("[Server -> %s] %s", user.getUsername(), msg.getMessage()));
                    Server.getInstance().sendPacketToClient(Server.getInstance().getNetworkUserFromUser(user), privateMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
