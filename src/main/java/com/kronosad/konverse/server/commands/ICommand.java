package com.kronosad.konverse.server.commands;

import com.kronosad.konverse.common.packets.Packet02ChatMessage;

/**
 * Use this class to register your class as a command.
 */
public interface ICommand {

    public String getCommand();
    public void run(String[] args, Packet02ChatMessage packet);
    public String getHelpText();

}
