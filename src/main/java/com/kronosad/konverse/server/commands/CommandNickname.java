package com.kronosad.konverse.server.commands;

import com.kronosad.konverse.common.packets.Packet02ChatMessage;
import com.kronosad.konverse.server.Server;

import java.io.IOException;

/**
 * @author Russell Richardson
 */
public class CommandNickname implements ICommand {

    @Override
    public String getCommand() {
        return "/nick";
    }

    @Override
    public void run(String[] args, Packet02ChatMessage packet) {
        if(args.length >= 1) {

            if(args[0].isEmpty()) {
                Server.getInstance().sendMessageToClient(Server.getInstance().getNetworkUserFromUser(packet.getChat().getUser())
                        , "You can't have an empty nickname!");
                return;
            }

            if(args[0].length() > 10) {
                Server.getInstance().sendMessageToClient(Server.getInstance().getNetworkUserFromUser(packet.getChat().getUser())
                        , "Your nickname is too long!");
                return;
            }

            Server.getInstance().getNetworkUserFromUser(packet.getChat().getUser()).setNickname(args[0]);

            try {
                Server.getInstance().updateUserList(); // Broadcast updated nickname.
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Server.getInstance().getNetworkUserFromUser(packet.getChat().getUser()).setNickname(null);

            try {
                Server.getInstance().updateUserList(); // Broadcast updated nickname.
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getHelpText() {
        return "/nick <nickname> OR Just do /nick to clear your nickname.";
    }

    @Override
    public String getDescription() {
        return "Sets your Nickname!";
    }

    @Override
    public boolean requiresElevation() {
        return false;
    }

    @Override
    public void runFromConsole(String[] args) {
        System.err.println("The Server console can't have a nickname silly!");
    }
}
