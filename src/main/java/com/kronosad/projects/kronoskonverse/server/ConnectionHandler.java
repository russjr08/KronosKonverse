package com.kronosad.projects.kronoskonverse.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kronosad.projects.kronoskonverse.common.KronosKonverseAPI;
import com.kronosad.projects.kronoskonverse.common.objects.ChatMessage;
import com.kronosad.projects.kronoskonverse.common.packets.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
                    server.users.remove(user);
                    Packet03UserListChange change = new Packet03UserListChange(Packet.Initiator.SERVER, server.users);

                    change.setMessage("remove");
                    server.sendPacketToClients(change);
                    client.close();

                    ChatMessage message = new ChatMessage();
                    message.setMessage(user.getUsername() + " has left.");
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

                        if(handshake.getVersion() == null){
                            System.out.println("Client " + user.getUsername() + " doesn't have a version set! Disconnecting...");
                            user.disconnect("Your handshake did not contain a version!", true);
                        }

                        if(handshake.getVersion().getProtocol().equalsIgnoreCase(server.getVersion().getProtocol())){
                            String username = handshake.getMessage().split("-")[1].split(" ")[0];


                            for(NetworkUser networkUser : server.users){
                                if(networkUser.getUsername().equalsIgnoreCase(username) || username.equalsIgnoreCase("server")){
                                    user.disconnect("Someone is already logged into the server with that name!", true);
                                }
                            }

                            System.out.println("User connected: " + user.getUsername());
                            server.users.add(user);

                            PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

                            Packet01LoggedIn loggedIn = new Packet01LoggedIn(Packet.Initiator.SERVER, user, server.users);

                            writer.println(loggedIn.toJSON());

                            Packet03UserListChange change = new Packet03UserListChange(Packet.Initiator.SERVER, server.users);

                            change.setMessage("add");

                            server.sendPacketToClients(change);

                            ChatMessage message = new ChatMessage();
                            message.setMessage(user.getUsername() + " has joined!");
                            message.setUser(server.serverUser);

                            Packet02ChatMessage chatPacket = new Packet02ChatMessage(Packet.Initiator.SERVER, message);

                            server.sendPacketToClients(chatPacket);



                        }else{

                            System.err.println("Version mismatch! Disconnecting client!");
                            server.users.add(user);
                            user.disconnect("Your version is out of date! You are running " + handshake.getVersion().getReadable() + " This server runs: " + KronosKonverseAPI.API_VERSION.getReadable() +
                                    " , please download the latest version from https://drone.io/github.com/russjr08/KronosKonverse/files", true);

                            if(!client.isClosed()){
                                client.close();

                            }
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
                            server.users.remove(oldUser);
                            server.users.add(newUser);

                            ChatMessage message = new ChatMessage();
                            message.setAction(true);
                            message.setMessage(" is now known as " + newUser.getUsername());
                            message.setUser(oldUser);
                            Packet02ChatMessage chatMessage = new Packet02ChatMessage(Packet.Initiator.SERVER, message);
                            server.sendPacketToClients(chatMessage);

                            Packet03UserListChange listChange = new Packet03UserListChange(Packet.Initiator.SERVER, server.users);
                            server.sendPacketToClients(listChange);

                            Packet01LoggedIn loggedIn = new Packet01LoggedIn(Packet.Initiator.SERVER, newUser, server.users);
                            server.sendPacketToClient(newUser, loggedIn);
                            this.user = newUser;
                        }else{
                            server.sendPacketToClients(chat);
                        }
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Client disconnected with exception: " + prettyGson.toJson(user));
                user.disconnect("Disconnected via Exception", false);
                break;
            }
        }

    }
}
