package com.kronosad.konverse.server.commands;

import com.kronosad.konverse.common.packets.Packet02ChatMessage;
import com.kronosad.konverse.server.Server;

/**
 * @author Russell Richardson
 */
public class CommandDEOP implements ICommand {

    @Override
    public String getCommand() {
        return "/deop";
    }

    @Override
    public void run(String[] args, Packet02ChatMessage packet) {
        // This command can only be ran by the Console.
    }

    @Override
    public String getHelpText() {
        return "/deop <username>";
    }

    @Override
    public String getDescription() {
        return "De-OPs a User. Can only be ran by the server console!";
    }

    @Override
    public boolean requiresElevation() { return true; }

    @Override
    public void runFromConsole(String[] args) {
        if(args.length <= 1) {
            System.err.println(getHelpText());
            return;
        }
        System.out.println("Removed OP: " + args[0]);
        Server.getInstance().removeOP(args[0]);
    }
}
