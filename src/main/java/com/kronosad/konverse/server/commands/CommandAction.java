package com.kronosad.konverse.server.commands;

import com.kronosad.konverse.common.objects.ChatMessage;
import com.kronosad.konverse.common.packets.Packet;
import com.kronosad.konverse.common.packets.Packet02ChatMessage;
import com.kronosad.konverse.server.Server;

import java.io.IOException;

/**
 * Created by Russell Richardson.
 */
public class CommandAction implements ICommand {

    @Override
    public String getCommand() {
        return "/me";
    }

    @Override
    public void run(String[] args, Packet02ChatMessage packet) {
        ChatMessage msg = new ChatMessage();
        msg.setAction(true);
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            if(arg != null && !arg.equalsIgnoreCase(getCommand())){
                builder.append(arg + " ");
            }
        }
        msg.setMessage(builder.toString());
        msg.setUser(packet.getChat().getUser());

        try {
            Server.getInstance().sendPacketToClients(new Packet02ChatMessage(Packet.Initiator.CLIENT, msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHelpText() {
        return "/me ";
    }

    @Override
    public String getDescription() {
        return "Sends your message in third person!";
    }

    @Override
    public boolean requiresElevation() { return false; }

    @Override
    public void runFromConsole(String[] args) {
        System.err.println("Console cannot run /me!");
    }
}
