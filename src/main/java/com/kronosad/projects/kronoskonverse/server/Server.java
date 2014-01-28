package com.kronosad.projects.kronoskonverse.server;

import com.kronosad.projects.kronoskonverse.common.KronosKonverseAPI;
import com.kronosad.projects.kronoskonverse.common.objects.ChatMessage;
import com.kronosad.projects.kronoskonverse.common.objects.PrivateMessage;
import com.kronosad.projects.kronoskonverse.common.objects.Room;
import com.kronosad.projects.kronoskonverse.common.objects.Version;
import com.kronosad.projects.kronoskonverse.common.packets.Packet;
import com.kronosad.projects.kronoskonverse.common.packets.Packet01LoggedIn;
import com.kronosad.projects.kronoskonverse.common.packets.Packet02ChatMessage;
import com.kronosad.projects.kronoskonverse.common.packets.Packet04Disconnect;
import com.kronosad.projects.kronoskonverse.common.user.NetworkUser;
import com.kronosad.projects.kronoskonverse.common.user.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 5:30 PM
 */
public class Server {

    private ServerSocket server;
    private Version version = KronosKonverseAPI.API_VERSION;

    protected User serverUser;
    protected ArrayList<NetworkUser> users = new ArrayList<NetworkUser>();
    protected ArrayList<Room> rooms = new ArrayList<Room>();
    protected boolean running = false;



    public Server(int port){
        serverUser = new User();
        try {
            Field username = serverUser.getClass().getDeclaredField("username");
            Field uuid = serverUser.getClass().getDeclaredField("uuid");

            username.setAccessible(true);
            uuid.setAccessible(true);

            username.set(serverUser, "Server");
            uuid.set(serverUser, "--SERVER--");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        rooms.add(new Room("Main", serverUser));
        System.out.println("Default room created!");


        System.out.println("Opening server on port: " + port);

        try {
            server = new ServerSocket(port);
            System.out.println("Sucessfully bounded to port!");
            running = true;
            serve();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("There was a problem binding to port: " + port);
        }

        while(running){
            Scanner in = new Scanner(System.in);
            String response = in.nextLine();

            if(response.equalsIgnoreCase("stop")){
                try {
                    System.out.println("Stopping server...");
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMessage("[WARNING: Server is shutting down, disconnecting all clients!]");
                    chatMessage.setUser(this.serverUser);
                    Packet02ChatMessage chatPacket = new Packet02ChatMessage(Packet.Initiator.SERVER, chatMessage);
                    sendPacketToClients(chatPacket);

                    for(NetworkUser user : users){
                        user.getSocket().close();
                    }

                    this.server.close();
                    running = false;
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(response.startsWith("kick")){
                String username = response.split(" ")[1];
                System.out.println("Kicking user: " + username);
                StringBuilder kickBuilder = new StringBuilder();
                for(int i = 0; i < response.split(" ").length; i++){
                    if(i != 0 && i != 1){
                        kickBuilder.append(response.split(" ")[i] + " ");
                    }
                }

                User user = new User();
                try {
                    Field usernameField = user.getClass().getDeclaredField("username");
                    usernameField.setAccessible(true);
                    usernameField.set(user, username);
                    NetworkUser networkUser = getNetworkUserFromUser(user);
                    if(networkUser == null){
                        System.out.println("User was not found!");

                    }else{
                        Packet04Disconnect disconnect = new Packet04Disconnect(Packet.Initiator.SERVER, getNetworkUserFromUser(user), true);
                        disconnect.setMessage(kickBuilder.toString());

                        try {
                            sendPacketToClient(getNetworkUserFromUser(user), disconnect);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }else{
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
        if(packet instanceof Packet02ChatMessage){
            Packet02ChatMessage chatPacket = (Packet02ChatMessage) packet;
            if(((Packet02ChatMessage) packet).getChat().getMessage().isEmpty() || ((Packet02ChatMessage) packet).getChat().getMessage().trim().isEmpty()){ return; /** Null message, don't send. **/}
            if(chatPacket.isPrivate()){
                PrivateMessage msg = chatPacket.getPrivateMessage();
                for(NetworkUser user : users){
                    if(user.getUsername().equals(msg.getRecipient().getUsername()) || user.getUsername().equals(chatPacket.getChat().getUser().getUsername())){
                        sendPacketToClient(user, packet);
                        System.out.println(String.format("[%s -> %s] %s", chatPacket.getChat().getUser().getUsername(), msg.getRecipient().getUsername(), chatPacket.getChat().getMessage()));
                        return;
                    }
                }

            }else{
                System.out.println("[" + chatPacket.getChat().getUser().getUsername() + "] " + chatPacket.getChat().getMessage());

            }
            if(chatPacket.getChat().getRoom() != null){
                Room serverRoom = getRoom(chatPacket.getChat().getRoom().getName());
                for(User user : serverRoom.getUsers()){
                    NetworkUser networkUser = getNetworkUserFromUser(user);
                    PrintWriter writer = new PrintWriter(networkUser.socket.getOutputStream(), true);
                    writer.println(packet.toJSON());
                    return;
                }
            }

        }
        for(NetworkUser user : users){
            PrintWriter writer = new PrintWriter(user.getSocket().getOutputStream(), true);
            writer.println(packet.toJSON());
        }


    }

    public void sendPacketToClient(NetworkUser user, Packet packet) throws IOException {
        if(packet instanceof Packet02ChatMessage){
            Packet02ChatMessage chatPacket = (Packet02ChatMessage) packet;
            if(((Packet02ChatMessage) packet).getChat().getMessage().isEmpty()){ return; /** Null message, don't send. **/}
            System.out.println("[" + chatPacket.getChat().getUser().getUsername() + "] " + chatPacket.getChat().getMessage());
        }

        PrintWriter writer = new PrintWriter(user.getSocket().getOutputStream(), true);
        writer.println(packet.toJSON());



    }

    public NetworkUser getNetworkUserFromUser(User user){
        for(NetworkUser network : users){
            if(network.getUsername().equalsIgnoreCase(user.getUsername())){
                return network;
            }
        }

        return null;
    }

    public void serve(){
        new Thread(){
            public void run(){
                while (running){
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

    public Version getVersion() {
        return version;
    }

    public static void main(String[] args){
        new Server(Integer.valueOf(args[0]));
    }

    public ArrayList<User> getLoggedInUsers(){
        ArrayList<User> loggedInUsers = new ArrayList<User>();
        for(NetworkUser user : users){
            loggedInUsers.add(user);
        }
        return loggedInUsers;
    }

    public void addUser(NetworkUser user){
        addUser(user, true);
    }

    public void addUser(NetworkUser user, boolean newUser){
        users.add(user);
        if(newUser){
            setUsersRoom(user.getUsername(), getRoom("Main"));
        }
    }

    public void removeUser(NetworkUser user){
        getRoom(user.room.getName()).removeUser(user.getUsername());
        users.remove(user);
    }

    public Room getRoom(String name){
        for(Room room : rooms){
            if(room.getName().equals(name)){
                return room;
            }
        }

        return null;
    }

    public void addRoom(Room room){
        rooms.add(room);
    }

    public void removeRoom(String roomName){
        for(Room room : rooms){
            if(room.getName().equals(roomName)){
                rooms.remove(room);
                break;
            }
        }
    }

    public void setUsersRoom(String user, Room room){
        for(NetworkUser userList : users){
            if(userList.getUsername().equals(user)){
                room.addUser(userList);
                if(userList.room != null){
                    getRoom(userList.room.getName()).removeUser(userList.getUsername());
                }
                userList.room = room;
                if(getRoom(room.getName()) == null){
                    addRoom(room);
                }
                Packet01LoggedIn loggedIn = new Packet01LoggedIn(Packet.Initiator.SERVER, userList, getLoggedInUsers());
                try {
                    sendPacketToClient(userList, loggedIn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

}
