package com.kronosad.konverse.server.commands;

import com.kronosad.konverse.common.packets.Packet02ChatMessage;

import java.util.ArrayList;

/**
 * Created by Alex on 11/23/2014.
 */
public class CommandMute implements ICommand {

    public ArrayList<String> mutedUsers = new ArrayList<String>();

    @Override
    public String getCommand() {
        return "/mute";
    }

    @Override
    public void run(String[] args, Packet02ChatMessage packet) {
        if (args.length >= 1) {
            if (!args[0].equals(packet.getChat().getUser().getUsername())) {
                if (mutedUsers.contains(packet.getChat().getUser().getUsername())) {
                    mutedUsers.remove(args[0]);
                } else {
                    mutedUsers.add(args[0]);
                }
            }
        }
    }

    @Override
    public String getHelpText() {
        return "/mute <user>";
    }

    @Override
    public String getDescription() {
        return "Mute anyone so they cannot talk.";
    }

    @Override
    public boolean requiresElevation() {
        return true;
    }

    @Override
    public void runFromConsole(String[] args) {
        if (args.length >= 1) {
            if (mutedUsers.contains(args[0])) {
                mutedUsers.remove(args[0]);
            }
            else {mutedUsers.add(args[0]);}
        }
    }
}

