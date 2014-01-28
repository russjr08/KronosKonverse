package com.kronosad.projects.kronoskonverse.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kronosad.projects.kronoskonverse.common.KronosKonverseAPI;
import com.kronosad.projects.kronoskonverse.common.objects.ChatMessage;
import com.kronosad.projects.kronoskonverse.common.objects.Room;
import com.kronosad.projects.kronoskonverse.common.packets.*;
import com.kronosad.projects.kronoskonverse.common.user.NetworkUser;
import com.kronosad.projects.kronoskonverse.common.user.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 5:40 PM
 */
public class ConnectionHandler implements Runnable {
    private Server server;
    private Socket client;
    private Gson prettyGson;

    private NetworkUser user;

    private Thread manage;

    public ConnectionHandler(Server server, Socket client){
        this.server = server;
        this.client = client;
        prettyGson = new GsonBuilder().setPrettyPrinting().create();

        manage = new Thread(this);
        manage.start();
    }


    @Override
    public void run() {
        while (server.running){
            try {
                BufferedReader inputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String response = inputStream.readLine();
                Packet packet = new Gson().fromJson(response, Packet.class);

                if(response == null || response.equals("-1")){
                    System.out.println("Client disconnected: " + user.getUsername());
                    server.removeUser(user);

                    ArrayList<User> loggedInUsers = new ArrayList<User>();
                    for(NetworkUser user : server.users){
                        loggedInUsers.add(user);
                    }
                    Packet03UserListChange change = new Packet03UserListChange(Packet.Initiator.SERVER, loggedInUsers);

                    change.setMessage("remove");
                    server.sendPacketToClients(change);
                    client.close();

                    ChatMessage message = new ChatMessage();
                    message.setMessage(user.getUsername() + " has left.");
                    message.setServerMsg(true);
                    message.setUser(server.serverUser);

                    Packet02ChatMessage chatPacket = new Packet02ChatMessage(Packet.Initiator.SERVER, message);
                    server.sendPacketToClients(chatPacket);
                    break;
                }
                switch(packet.getId()){
                    case 0:
                        System.out.println("Parsing handshake packet!");
                        System.out.println(response);
                        Packet00Handshake handshake = new Gson().fromJson(response, Packet00Handshake.class);
                        System.out.println(prettyGson.toJson(handshake));
                        System.out.println(handshake.getVersion().toJSON());

                        user = new NetworkUser(client, handshake.getMessage().split("-")[1].split(" ")[0], UUID.randomUUID().toString(), false);

                        if(handshake.getVersion().getProtocol().equalsIgnoreCase(server.getVersion().getProtocol())){
                            String username = handshake.getMessage().split("-")[1].split(" ")[0];


                            for(NetworkUser networkUser : server.users){
                                if(networkUser.getUsername().equalsIgnoreCase(username) || username.equalsIgnoreCase("server")){
                                    Packet04Disconnect disconnect = new Packet04Disconnect(Packet.Initiator.SERVER, user, true);
                                    disconnect.setMessage("Someone is already logged into the server with that name!");
                                    server.sendPacketToClient(user, disconnect);

                                }
                            }

                            System.out.println("User connected: " + user.getUsername());
                            server.addUser(user);

                            PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

                            ArrayList<User> loggedInUsers = new ArrayList<User>();
                            for(NetworkUser user : server.users){
                                loggedInUsers.add(user);
                            }

                            Packet01LoggedIn loggedIn = new Packet01LoggedIn(Packet.Initiator.SERVER, user, loggedInUsers);

                            writer.println(loggedIn.toJSON());

                            Packet03UserListChange change = new Packet03UserListChange(Packet.Initiator.SERVER, loggedInUsers);

                            change.setMessage("add");

                            server.sendPacketToClients(change);

                            ChatMessage message = new ChatMessage();
                            message.setMessage(user.getUsername() + " has joined!");
                            message.setServerMsg(true);
                            message.setUser(server.serverUser);

                            Packet02ChatMessage chatPacket = new Packet02ChatMessage(Packet.Initiator.SERVER, message);

                            server.sendPacketToClients(chatPacket);



                        }else{

                            System.err.println("Version mismatch! Disconnecting client!");
                            server.addUser(user);
                            Packet04Disconnect kickPacket = new Packet04Disconnect(Packet.Initiator.SERVER, user, true);
                            kickPacket.setMessage("Your version is out of date! This server runs: " + KronosKonverseAPI.API_VERSION.getReadable() +
                                    " , please download the latest version from https://drone.io/github.com/russjr08/KronosKonverse/files");

                            server.sendPacketToClient(user, kickPacket);

                            client.close();
                            server.removeUser(user);
                            manage.stop();
                        }
                        break;
                    case 1:
                        break;
                    case 2:
                        Packet02ChatMessage chat = new Gson().fromJson(response, Packet02ChatMessage.class);
                        if(chat.getChat().getMessage().startsWith("/nick")){
                            NetworkUser oldUser = server.getNetworkUserFromUser(chat.getChat().getUser());

                            // TODO: Verify security here...
                            NetworkUser newUser = new NetworkUser(this.client, chat.getChat().getMessage().split(" ")[1], oldUser.getUuid(), oldUser.isElevated());
                            server.removeUser(oldUser);
                            server.addUser(newUser, false);

                            ChatMessage message = new ChatMessage();
                            message.setAction(true);
                            message.setMessage(" is now known as " + newUser.getUsername());
                            message.setServerMsg(true);
                            message.setUser(oldUser);
                            Packet02ChatMessage chatMessage = new Packet02ChatMessage(Packet.Initiator.SERVER, message);
                            server.sendPacketToClients(chatMessage);

                            ArrayList<User> loggedInUsers = new ArrayList<User>();
                            for(NetworkUser user : server.users){
                                loggedInUsers.add(user);
                            }

                            Packet03UserListChange listChange = new Packet03UserListChange(Packet.Initiator.SERVER, loggedInUsers);
                            server.sendPacketToClients(listChange);

                            Packet01LoggedIn loggedIn = new Packet01LoggedIn(Packet.Initiator.SERVER, newUser, loggedInUsers);
                            server.sendPacketToClient(newUser, loggedIn);
                            this.user = newUser;
                        }else if(chat.getChat().getMessage().startsWith("/room")){
                            Room room = server.getRoom(chat.getChat().getMessage().split(" ")[1]);
                            if(room == null){
                                room = new Room(chat.getChat().getMessage().split(" ")[1], user);
                                this.user.room = room;
                                server.setUsersRoom(user.getUsername(), room);
                            }else{
                                server.setUsersRoom(user.getUsername(), server.getRoom(chat.getChat().getMessage().split(" ")[1]));
                                this.user.room = server.getRoom(chat.getChat().getMessage().split(" ")[1]);
                            }

                        }else{
                            server.sendPacketToClients(chat);
                        }
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Client disconnected with exception: " + prettyGson.toJson(user));
                server.removeUser(user);

                ChatMessage message = new ChatMessage();
                message.setMessage(user.getUsername() + " has left.");
                message.setServerMsg(true);
                message.setUser(server.serverUser);

                Packet02ChatMessage chatPacket = new Packet02ChatMessage(Packet.Initiator.SERVER, message);
                try {
                    server.sendPacketToClients(chatPacket);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                break;
            }
        }

    }
}
