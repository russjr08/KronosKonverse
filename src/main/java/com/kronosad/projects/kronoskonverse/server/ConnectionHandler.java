package com.kronosad.projects.kronoskonverse.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kronosad.projects.kronoskonverse.common.objects.ChatMessage;
import com.kronosad.projects.kronoskonverse.common.packets.*;
import com.kronosad.projects.kronoskonverse.server.implementation.NetworkUser;

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

                if(response == null){
                    System.out.println("Client disconnected: " + user.getUsername());
                    server.users.remove(user);
                    Packet03UserListChange change = new Packet03UserListChange(Packet.Initiator.SERVER, user);

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

                        if(handshake.getVersion().getProtocol().equalsIgnoreCase(server.getVersion().getProtocol())){

                            user = new NetworkUser(client, handshake.getMessage().split("-")[1], UUID.randomUUID(), false);

                            System.out.println("User connected!");
                            System.out.println(prettyGson.toJson(user));
                            server.users.add(user);

                            PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

                            Packet01LoggedIn loggedIn = new Packet01LoggedIn(Packet.Initiator.SERVER, user, server.users);

                            writer.println(loggedIn.toJSON());

                            Packet03UserListChange change = new Packet03UserListChange(Packet.Initiator.SERVER, user);

                            change.setMessage("add");

                            server.sendPacketToClients(change);

                            ChatMessage message = new ChatMessage();
                            message.setMessage(user.getUsername() + " has joined!");
                            message.setUser(server.serverUser);

                            Packet02ChatMessage chatPacket = new Packet02ChatMessage(Packet.Initiator.SERVER, message);

                            server.sendPacketToClients(chatPacket);



                        }else{
                            System.err.println("Version mismatch! Disconnecting client!");
                            client.close();
                        }
                        break;
                    case 1:
                        break;
                    case 2:
                        Packet02ChatMessage chat = new Gson().fromJson(response, Packet02ChatMessage.class);

                        server.sendPacketToClients(chat);
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Client disconnected: " + prettyGson.toJson(user));
                server.users.remove(user);
            }
        }

    }
}
