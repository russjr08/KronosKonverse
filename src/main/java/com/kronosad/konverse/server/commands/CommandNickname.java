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
            Server.getInstance().getNetworkUserFromUser(packet.getChat().getUser()).setNickname(args[0]);
            try {
                Server.getInstance().updateUserList();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Server.getInstance().getNetworkUserFromUser(packet.getChat().getUser()).setNickname(null);
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
