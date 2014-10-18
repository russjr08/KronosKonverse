package com.kronosad.konverse.server.commands;

import com.kronosad.konverse.common.packets.Packet02ChatMessage;

/**
 * Use this class to register your class as a command.
 */
public interface ICommand {

    /**
     * @return What the user will use to access your command, including the '/'
     */
    public String getCommand();

    /**
     * This method will be called when your command is ran.
     * @param args Arguments of the command (Not including /NameOfCommand)
     * @param packet The packet where the command originated from.
     */
    public void run(String[] args, Packet02ChatMessage packet);

    /**
     * Should return a String that tells the user how to use your command.
     * @return A String that tells the user how to use your command.
     */
    public String getHelpText();

    /**
     * Use this method to return a description of your command to the user.
     * @return The description of this command.
     */
    public String getDescription();

    /**
     * If your command requires the user to be elevated (such as a kick command), return true here.
     * @return Whether or not this command requires elevation.
     */
    public boolean requiresElevation();

    /**
     * This method will be ran when your command is called by the console, should be similar to
     * {@link com.kronosad.konverse.server.commands.ICommand#run(String[], com.kronosad.konverse.common.packets.Packet02ChatMessage)}.
     */
    public void runFromConsole(String[] args);
}
