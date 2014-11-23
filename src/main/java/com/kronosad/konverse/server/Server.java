package com.kronosad.konverse.server;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kronosad.konverse.common.KonverseAPI;
import com.kronosad.konverse.common.auth.AuthenticatedUserProfile;
import com.kronosad.konverse.common.auth.Authentication;
import com.kronosad.konverse.common.objects.ChatMessage;
import com.kronosad.konverse.common.objects.PrivateMessage;
import com.kronosad.konverse.common.objects.Version;
import com.kronosad.konverse.common.packets.Packet;
import com.kronosad.konverse.common.packets.Packet02ChatMessage;
import com.kronosad.konverse.common.packets.Packet03UserListChange;
import com.kronosad.konverse.common.plugin.PluginManager;
import com.kronosad.konverse.common.plugin.Side;
import com.kronosad.konverse.common.user.AuthenticatedUser;
import com.kronosad.konverse.common.user.User;
import com.kronosad.konverse.server.commands.*;
import com.kronosad.konverse.server.events.ChatReceivedEvent;
import com.kronosad.konverse.server.misc.OperatorList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.util.*;

/**
 * This is where the magic all happens.
 */
public class Server {

    private ServerSocket server;
    private Version version = KonverseAPI.API_VERSION;
    private Authentication authenticator;
    private PluginManager manager = new PluginManager();
    private CommandMute muteCommand = new CommandMute();

    protected AuthenticatedUser serverUser;

    protected List<NetworkUser> users = new ArrayList<NetworkUser>();
    protected List<ICommand> commands = new ArrayList<>();

    protected boolean running = false;
    private boolean authenticationDisabled = false;

    protected static Server instance;

    public EventBus eventBus = new EventBus();


    private Server(String args[]) {
        serverUser = new AuthenticatedUser();

        // Check authentication flags.
        for (String string : args) {
            if (string.contains("--auth-server=")) {
                authenticator = new Authentication(string.split("=")[0]);
            }
            if (string.contains("--no-auth")) {
                authenticationDisabled = true;
                System.err.println("WARNING: You've chosen to disable authentication, this can allow any unverified" +
                        " user to connect with whatever (or no) credentials. It's recommended you leave authentication" +
                        " turned on!");
            }
        }

        // If a custom authentication server wasn't set, we'll initialize the authenticator with the default server.
        if (authenticator == null) {
            authenticator = new Authentication();
        }

        // Give the server it's own username! Reflection magic.
        try {
            Field username = serverUser.getClass().getSuperclass().getDeclaredField("username");

            username.setAccessible(true);

            username.set(serverUser, "Server");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        System.out.println("Starting Konverse Server! (Version: " + getVersion().getReadable() + ")");
        System.out.println("Opening Server on port: " + args[0]);

        // Register commands.
        registerCommand(new CommandAction());
        registerCommand(new CommandKick());
        registerCommand(new CommandOP());
        registerCommand(new CommandDEOP());
        registerCommand(new CommandHelp());
        registerCommand(new CommandStop());
        registerCommand(new CommandMsg());
        registerCommand(new CommandListUsers());
        registerCommand(new CommandNickname());
        registerCommand(muteCommand);

        try {
            server = new ServerSocket(Integer.valueOf(args[0]));
            System.out.println("Sucessfully bounded to port!");
            running = true;
            serve();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("There was a problem binding to port: " + args[0]);
        }

        instance = this;

        manager.loadPlugins(new File("plugins/"), Side.SERVER);

        while (running) {
            // Get input from the Server console.
            Scanner in = new Scanner(System.in);
            String response = in.nextLine();

            boolean isCommand = false;
            String[] cmdArgs = response.split(" ");
            for(ICommand command : commands) {
                if(command.getCommand().equalsIgnoreCase(cmdArgs[0])) {
                    List<String> tempArgs = new ArrayList<String>();
                    for (String arg : cmdArgs) {
                        tempArgs.add(arg);
                    }
                    // Remove the /CommandName part from the Array.
                    tempArgs.remove(0);

                    command.runFromConsole(tempArgs.toArray(cmdArgs));
                    isCommand = true;
                    break;
                }
            }

            if(!isCommand){
                try {
                    ChatMessage msg = new ChatMessage();
                    msg.setMessage(response);
                    msg.setUser(serverUser);
                    Packet02ChatMessage packet = new Packet02ChatMessage(Packet.Initiator.SERVER, msg);
                    sendPacketToClients(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * Sends a {@link com.kronosad.konverse.common.packets.Packet} to all connected clients.
     * @param packet The {@link com.kronosad.konverse.common.packets.Packet} to send to the clients.
     * @throws IOException Thrown if there was a problem sending the packet.
     */
    public void sendPacketToClients(Packet packet) throws IOException {
        if (packet instanceof Packet02ChatMessage) {
            Packet02ChatMessage chatPacket = (Packet02ChatMessage) packet;
            if(chatPacket.getChat().getMessage() == null)
                return;
            if (chatPacket.getChat().getMessage().isEmpty() || chatPacket.getChat().getMessage().trim().isEmpty()) {
                return; /** Null message, don't send. **/}
            if (chatPacket.isPrivate()) {
                PrivateMessage msg = chatPacket.getPrivateMessage();
                for (NetworkUser user : users) {
                    if (user.getUsername().equals(msg.getRecipient().getUsername()) || user.getUsername().equals(chatPacket.getChat().getUser().getUsername())) {
                        sendPacketToClient(user, packet);
                        System.out.println(String.format("[%s -> %s] %s", chatPacket.getChat().getUser().getUsername(), msg.getRecipient().getUsername(), chatPacket.getChat().getMessage()));
                        return;
                    }
                }

            } else {
                System.out.println("[" + chatPacket.getChat().getUser().getUsername() + "] " + chatPacket.getChat().getMessage());

            }

        }
        for (NetworkUser user : users) {
            PrintWriter writer = new PrintWriter(user.getSocket().getOutputStream(), true);
            writer.println(packet.toJSON());
        }


    }

    /**
     * Sends a {@link com.kronosad.konverse.common.packets.Packet} to a specific client.
     * @param user The {@link com.kronosad.konverse.server.NetworkUser} to send the {@link com.kronosad.konverse.common.packets.Packet} to.
     * @param packet The {@link com.kronosad.konverse.common.packets.Packet} to send to the {@link com.kronosad.konverse.server.NetworkUser}.
     * @throws IOException Thrown if there was a problem sending the packet.
     * @see #getNetworkUserFromUser(com.kronosad.konverse.common.user.User) to obtain a valid instance of {@link com.kronosad.konverse.server.NetworkUser}.
     */
    public void sendPacketToClient(NetworkUser user, Packet packet) throws IOException {
        if (packet instanceof Packet02ChatMessage) {
            Packet02ChatMessage chatPacket = (Packet02ChatMessage) packet;
            if (((Packet02ChatMessage) packet).getChat().getMessage().isEmpty()) {
                return; /** Null message, don't send. **/}
            System.out.println("[" + chatPacket.getChat().getUser().getUsername() + "] " + chatPacket.getChat().getMessage());
        }

        PrintWriter writer = new PrintWriter(user.getSocket().getOutputStream(), true);
        writer.println(packet.toJSON());


    }

    /**
     * Takes a {@link com.kronosad.konverse.common.packets.Packet02ChatMessage} and determines if it's an {@link com.kronosad.konverse.server.commands.ICommand},
     * if it is, then we'll go through all registered commands and run the matching one. Otherwise, we'll send it off to all users.
     * @param chat The {@link com.kronosad.konverse.common.packets.Packet02ChatMessage} to be processed.
     * @see #registerCommand(com.kronosad.konverse.server.commands.ICommand) To register a Command.
     */
    protected void processChat(Packet02ChatMessage chat) {
        boolean isCommand = false;
        String[] args = chat.getChat().getMessage().split(" ");

        if(args.length < 1) {
            sendMessageToClient(getNetworkUserFromUser(chat.getChat().getUser()),"Don't send empty messages, silly!");
            return;
        }
        if(muteCommand.mutedUsers.contains(chat.getChat().getUser().getUsername())) {
            sendMessageToClient(getNetworkUserFromUser(chat.getChat().getUser()), "You are muted!");
            return;
        }

        for(ICommand command : commands) {
            if(command.getCommand().equalsIgnoreCase(args[0])) {
                List<String> tempArgs = new ArrayList<String>();
                for (String arg : args) {
                    tempArgs.add(arg);
                }
                tempArgs.remove(0);
                // Just in case the client is trying to change their identity...
                chat.getChat().setUser(getNetworkUserFromUser(chat.getChat().getUser()));

                if(command.requiresElevation() && !chat.getChat().getUser().isElevated()) {
                    sendMessageToClient(getNetworkUserFromUser(chat.getChat().getUser()),
                            "You are not authorized to run this command!");
                    System.err.println(String.format("%s tried to run %s but is unauthorized!",
                            chat.getChat().getUser().getUsername(), command.getCommand()));
                    return;

                }

                command.run(tempArgs.toArray(args), chat);
                isCommand = true;
                break;
            }
        }

        if(!isCommand){
            try {
                sendPacketToClients(chat);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        eventBus.post(new ChatReceivedEvent(chat));

    }

    /**
     * Returns a valid instance of {@link com.kronosad.konverse.server.NetworkUser} from a regular
     * {@link com.kronosad.konverse.common.user.User} object (Such as from a Chat Packet/Message)
     * @param user The {@link com.kronosad.konverse.common.user.User} to get the {@link com.kronosad.konverse.server.NetworkUser} from.
     * @return
     */
    public NetworkUser getNetworkUserFromUser(User user) {
        for (NetworkUser network : users) {
            if (network.getUsername().equalsIgnoreCase(user.getUsername())) {
                return network;
            }
        }

        return null;
    }

    /**
     * Returns a {@link java.util.List} of Online Users.
     * @return A {@link java.util.List} of currently online users.
     */
    public List<User> getOnlineUsers() {
        List<User> online = new ArrayList<User>();
        for (NetworkUser onlineUser : users) {
            online.add(onlineUser);
        }
        return online;
    }

    /**
     * Listens for all incoming connections, and opens a {@link com.kronosad.konverse.server.ConnectionHandler} for them in a new {@link java.lang.Thread}.
     */
    private void serve() {
        new Thread() {
            public void run() {
                while (running) {
                    try {
                        new ConnectionHandler(Server.this, server.accept());
                    } catch (IOException e) {
                        System.err.println("Error accepting connection!");
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    /**
     * Sends a {@link com.kronosad.konverse.common.packets.Packet03UserListChange} to all connected clients and broadcasts it as a Chat Message.
     * @param user The {@link com.kronosad.konverse.common.user.User} who either connected, or dropped out.
     * @param newUser Did the new {@link com.kronosad.konverse.common.user.User} just connect, or have they left?
     * @throws IOException If the broadcast was unsuccessful.
     */
    public void broadcastUserChange(User user, boolean newUser) throws IOException {
        sendUserChange();

        ChatMessage message = new ChatMessage();
        String msg;
        if (newUser) {
            msg = "joined!";
        } else {
            msg = "left!";
        }
        message.setMessage(user.getUsername() + " has " + msg);
        message.setUser(serverUser);

        Packet02ChatMessage chatPacket = new Packet02ChatMessage(Packet.Initiator.SERVER, message);

        sendPacketToClients(chatPacket);

        updateUserList();

    }

    /**
     * A utility method that broadcasts a list of users to all connected clients.
     * @throws IOException Thrown if the broadcast was unsuccessful.
     */
    public void updateUserList() throws IOException {
        Packet03UserListChange changePacket = new Packet03UserListChange(Packet.Initiator.SERVER, getOnlineUsers());

        sendPacketToClients(changePacket);
    }

    /**
     * Similar to the {@link #broadcastUserChange(com.kronosad.konverse.common.user.User, boolean)} method, but doesn't broadcast it as a chat message.
     * @throws IOException
     */
    public void sendUserChange() throws IOException {
        Packet03UserListChange changePacket = new Packet03UserListChange(Packet.Initiator.SERVER, getOnlineUsers());
        sendPacketToClients(changePacket);
    }

    /**
     * A utility method for sending a string of text to a specific clients.
     * @param user The User to send this message to.
     * @param text The String of text to send to the User.
     */
    public void sendMessageToClient(NetworkUser user, String text) {
        PrivateMessage message = new PrivateMessage();
        message.setUser(Server.getInstance().getServerUser());
        message.setMessage(text);
        message.setRecipient(user);
        Packet02ChatMessage chatPacket = new Packet02ChatMessage(Packet.Initiator.SERVER, message);
        chatPacket.setPrivate(true);


        // Send it off, to just them.
        try {
            sendPacketToClient(getNetworkUserFromUser(user), chatPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Broadcasts a message
     * @param text
     */
    public void sendMessageToAllClients(String text) {
        ChatMessage message = new ChatMessage();
        message.setUser(Server.getInstance().getServerUser());
        message.setMessage(text);
        Packet02ChatMessage chatPacket = new Packet02ChatMessage(Packet.Initiator.SERVER, message);

        // Send it off, to just them.
        try {
            sendPacketToClients(chatPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an OP to the Server's OP List. (Retrieves the Account profile from the Auth Server this Server was started with.)
     * @param user Username of the user to add to the OP List.
     */
    public void addOP(String user) {
        File oplist = new File("ops.json");
        OperatorList operatorList = null;

        if (oplist.exists()) {
            try (FileInputStream inputStream = new FileInputStream("ops.json")) {
                operatorList = new Gson().fromJson(IOUtils.toString(inputStream), OperatorList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            operatorList = new OperatorList();
            try {
                oplist.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            AuthenticatedUserProfile profile = getAuthenticator().getUserProfile(user);

            if (profile != null) {
                operatorList.getOps().add(profile);
                System.out.println("Adding Server OP: " + user);
            } else {
                System.err.println("Authentication Server could not find user!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to contact Authentication Server!");
        }


        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileUtils.write(oplist, gson.toJson(operatorList));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (NetworkUser networkUser : users) {
            if(networkUser.getUsername().equals(user)) {
                networkUser.setElevated(true);
            }
        }

        Packet03UserListChange change = new Packet03UserListChange(Packet.Initiator.SERVER, getOnlineUsers());
        change.setMessage("op");

        try {
            sendPacketToClients(change);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes an OP fromt he Server's OP List.
     * @param user The User to remove from the Server's OP List.
     */
    public void removeOP(String user) {
        File oplist = new File("ops.json");

        for (int i = 0; i < getOps().getOps().size(); i++) {
            AuthenticatedUserProfile profile = getOps().getOps().get(i);
            if(profile.getUsername().equals(user)) {
                OperatorList operatorList = getOps();

                operatorList.getOps().remove(i);

                try {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    FileUtils.write(oplist, gson.toJson(operatorList));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            }
        }

        for (NetworkUser networkUser : users) {
            if(networkUser.getUsername().equals(user)) {
                networkUser.setElevated(false);
            }
        }

        Packet03UserListChange change = new Packet03UserListChange(Packet.Initiator.SERVER, getOnlineUsers());
        change.setMessage("de-op");

        try {
            sendPacketToClients(change);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves an instance of {@link com.kronosad.konverse.server.misc.OperatorList} which contains a {@link java.util.List}
     * of {@link com.kronosad.konverse.common.auth.AuthenticatedUserProfile}s.
     * @return
     */
    public OperatorList getOps() {
        File oplist = new File("ops.json");
        if (oplist.exists()) {
            try (FileInputStream inputStream = new FileInputStream("ops.json")) {
                return new Gson().fromJson(IOUtils.toString(inputStream), OperatorList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new OperatorList();
    }

    /**
     * Register's an {@link com.kronosad.konverse.server.commands.ICommand} to the Server's internal Command Registry.
     * @param command The {@link com.kronosad.konverse.server.commands.ICommand} to register.
     */
    public void registerCommand(ICommand command) {
        for (ICommand iCommand : commands) {
            if(command.getCommand().equalsIgnoreCase(iCommand.getCommand())) {
                throw new IllegalArgumentException("There is already a command registered under " + command.getCommand() + "!");
            }
        }
        System.out.println("Registered command: " + command.getCommand());
        commands.add(command);
    }

    /**
     * Returns an unmodifable list of {@link com.kronosad.konverse.server.commands.ICommand}.
     * @return An immutable {@link java.util.List} of {@link com.kronosad.konverse.server.commands.ICommand}s.
     */
    public List<ICommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    /**
     * Removes an {@link com.kronosad.konverse.server.commands.ICommand} from the Server's internal Command Registry.
     * @param command The {@link com.kronosad.konverse.server.commands.ICommand} to remove.
     */
    public void deregisterCommand(ICommand command) {
        commands.remove(command);
    }

    /**
     * Returns the {@link com.kronosad.konverse.common.objects.Version} of the Server.
     * @return The {@link com.kronosad.konverse.common.objects.Version} of the Server.
     */
    public Version getVersion() {
        return version;
    }

    /**
     * The {@link com.kronosad.konverse.common.auth.Authentication} that is being used by the Server to Authenticate all join requests.
     * @return
     */
    public Authentication getAuthenticator() {
        return authenticator;
    }

    /**
     * Is the Server using Authentication?
     * @return {@link true} if The server is using Authentication, otherwise, returns {@link false}.
     */
    public boolean isAuthenticationDisabled() { return authenticationDisabled; }

    /**
     * Returns the {@link com.kronosad.konverse.common.user.AuthenticatedUser} that the server is using for signing chat messages.
     * @return The {@link com.kronosad.konverse.common.user.AuthenticatedUser} that the server is using for signing chat messages.
     */
    public AuthenticatedUser getServerUser() { return serverUser; }

    /**
     * Don't use this...
     */
    public static void main(String[] args) {
        new Server(args);
    }

    /**
     * Allows you to statically gain an instance to the running {@link com.kronosad.konverse.server.Server}.
     * @return The currently running instance of {@link com.kronosad.konverse.server.Server}.
     */
    public static Server getInstance() {
        return instance;
    }

}
