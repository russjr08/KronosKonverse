package com.kronosad.konverse.server;

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
import com.kronosad.konverse.common.plugin.Side;
import com.kronosad.konverse.common.user.AuthenticatedUser;
import com.kronosad.konverse.common.user.User;
import com.kronosad.konverse.server.commands.CommandAction;
import com.kronosad.konverse.server.commands.ICommand;
import com.kronosad.konverse.server.misc.OperatorList;
import com.kronosad.konverse.common.plugin.PluginManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 5:30 PM
 */
public class Server {

    private ServerSocket server;
    private Version version = KonverseAPI.API_VERSION;
    private Authentication authenticator;
    private PluginManager manager = new PluginManager();

    protected AuthenticatedUser serverUser;

    protected List<NetworkUser> users = new ArrayList<NetworkUser>();
    protected List<ICommand> commands = new ArrayList<>();

    protected boolean running = false;
    private boolean authenticationDisabled = false;

    protected static Server instance;


    public Server(String args[]) {
        serverUser = new AuthenticatedUser();

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

        if (authenticator == null) {
            authenticator = new Authentication();
        }


        try {
            Field username = serverUser.getClass().getSuperclass().getDeclaredField("username");

            username.setAccessible(true);

            username.set(serverUser, "Server");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }


        System.out.println("Opening Server on port: " + args[0]);

        // Register commands.
        registerCommand(new CommandAction());

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
            Scanner in = new Scanner(System.in);
            String response = in.nextLine();

            if (response.equalsIgnoreCase("stop")) {
                try {
                    System.out.println("Stopping Server...");
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMessage("[WARNING: Server is shutting down, disconnecting all clients!]");
                    chatMessage.setUser(this.serverUser);
                    Packet02ChatMessage chatPacket = new Packet02ChatMessage(Packet.Initiator.SERVER, chatMessage);
                    sendPacketToClients(chatPacket);

                    for (NetworkUser user : users) {
                        user.getSocket().close();
                    }

                    this.server.close();
                    running = false;
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (response.startsWith("kick")) {
                String username = response.split(" ")[1];
                System.out.println("Kicking user: " + username);
                StringBuilder kickBuilder = new StringBuilder();
                for (int i = 0; i < response.split(" ").length; i++) {
                    if (i != 0 && i != 1) {
                        kickBuilder.append(response.split(" ")[i] + " ");
                    }
                }

                User user = new User();
                try {
                    Field usernameField = user.getClass().getDeclaredField("username");
                    usernameField.setAccessible(true);
                    usernameField.set(user, username);
                    NetworkUser networkUser = getNetworkUserFromUser(user);
                    if (networkUser == null) {
                        System.out.println("User was not found!");

                    } else {
                        networkUser.disconnect(kickBuilder.toString(), true);

                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (response.startsWith("op")) {
                String username = response.split(" ")[1];
                addOP(username);

            } else {
                ChatMessage message = new ChatMessage();
                message.setMessage(response);
                message.setUser(serverUser);

                Packet02ChatMessage packet02ChatMessage = new Packet02ChatMessage(Packet.Initiator.SERVER, message);
                try {
                    sendPacketToClients(packet02ChatMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public void sendPacketToClients(Packet packet) throws IOException {
        if (packet instanceof Packet02ChatMessage) {
            Packet02ChatMessage chatPacket = (Packet02ChatMessage) packet;
            if (((Packet02ChatMessage) packet).getChat().getMessage().isEmpty() || ((Packet02ChatMessage) packet).getChat().getMessage().trim().isEmpty()) {
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

    public void processChat(Packet02ChatMessage chat) {
        boolean isCommand = false;
        String[] args = chat.getChat().getMessage().split(" ");
        for(ICommand command : commands) {
            if(command.getCommand().equalsIgnoreCase(args[0])) {
                command.run(args, chat);
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
    }

    public NetworkUser getNetworkUserFromUser(User user) {
        for (NetworkUser network : users) {
            if (network.getUsername().equalsIgnoreCase(user.getUsername())) {
                return network;
            }
        }

        return null;
    }

    public List<User> getOnlineUsers() {
        List<User> online = new ArrayList<User>();
        for (NetworkUser onlineUser : users) {
            online.add(onlineUser);
        }
        return online;
    }

    public void serve() {
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

        Packet03UserListChange changePacket = new Packet03UserListChange(Packet.Initiator.SERVER, getOnlineUsers());

        sendPacketToClients(changePacket);

    }

    public void sendUserChange() throws IOException {
        Packet03UserListChange changePacket = new Packet03UserListChange(Packet.Initiator.SERVER, getOnlineUsers());
        sendPacketToClients(changePacket);
    }

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

    }

    public List<String> getOps() {
        File oplist = new File("ops.json");
        OperatorList operatorList;
        List<String> ops = new ArrayList<String>();
        if (oplist.exists()) {
            try (FileInputStream inputStream = new FileInputStream("ops.json")) {
                operatorList = new Gson().fromJson(IOUtils.toString(inputStream), OperatorList.class);
                ops.addAll(operatorList.getOps().stream().map(AuthenticatedUserProfile::getUsername).collect(Collectors.toList()));
                return ops;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ops;
    }

    public void registerCommand(ICommand command) {
        for (ICommand iCommand : commands) {
            if(command.getCommand().equalsIgnoreCase(iCommand.getCommand())) {
                throw new IllegalArgumentException("There is already a command registered under " + command.getCommand() + "!");
            }
        }
        System.out.println("Registered command: " + command.getCommand());
        commands.add(command);
    }

    public void deregisterCommand(ICommand command) {
        commands.remove(command);
    }

    public Version getVersion() {
        return version;
    }

    public Authentication getAuthenticator() {
        return authenticator;
    }

    public boolean isAuthenticationDisabled() { return authenticationDisabled; }

    public static void main(String[] args) {
        new Server(args);
    }

    public static Server getInstance() {
        return instance;
    }

}
