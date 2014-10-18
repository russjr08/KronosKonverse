package com.kronosad.konverse.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kronosad.konverse.common.auth.AuthenticatedUserProfile;
import com.kronosad.konverse.common.objects.ChatMessage;
import com.kronosad.konverse.common.packets.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public ConnectionHandler(Server server, Socket client) {
        this.server = server;
        this.client = client;
        prettyGson = new GsonBuilder().setPrettyPrinting().create();

        manage = new Thread(this);
        manage.start();
    }


    @Override
    public void run() {
        while (server.running) {
            try {
                BufferedReader inputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String response = inputStream.readLine();
                Packet packet = new Gson().fromJson(response, Packet.class);

                if (response == null || response.equals("-1")) {
                    System.out.println("Client disconnected: " + user.getUsername());
                    server.users.remove(user);
                    server.broadcastUserChange(user, false);
                    client.close();

                    break;
                }
                switch (packet.getId()) {
                    case 0:
                        System.out.println("Parsing handshake packet!");
                        System.out.println(response);
                        Packet00Handshake handshake = new Gson().fromJson(response, Packet00Handshake.class);
                        System.out.println(prettyGson.toJson(handshake));
                        System.out.println(handshake.getVersion().toJSON());

                        String username = handshake.getMessage().split("-")[1];

                        boolean shouldElevate = false;
                        for (AuthenticatedUserProfile profile : server.getOps().getOps()) {
                            if(profile.getUsername().equals(username)) {
                                shouldElevate = true;
                            }
                        }

                        user = new NetworkUser(client, username, UUID.randomUUID().toString(), shouldElevate);

                        if(!server.isAuthenticationDisabled()) {
                            try {
                                if (!server.getAuthenticator().verifyAuthToken(user.getUsername(), handshake.getAuthToken())) {
                                    System.err.println("Authentication Server rejected " + user.getUsername() + "'s authentication token!");
                                    user.sendStatus(Packet05ConnectionStatus.AUTHENTICATION_FAILED_SERVER_SIDE, true);
                                    return;
                                } else {
                                    System.out.println(user.getUsername() + "'s identity has been verified by the Authentication Server!");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.err.println("Failed to contact the Authentication Server, rejecting login!");
                                user.sendStatus(Packet05ConnectionStatus.AUTHENTICATION_FAILED_SERVER_SIDE, true);
                                client.close();
                                return;
                            }
                        }

                        if (handshake.getVersion() == null) {
                            System.out.println("Client " + user.getUsername() + " doesn't have a version set! Disconnecting...");
                            user.sendStatus(Packet05ConnectionStatus.VERSION_MISMATCH, true);
                            client.close();
                            return;
                        }

                        if (handshake.getVersion().getProtocol().equalsIgnoreCase(server.getVersion().getProtocol())) {

                            for (NetworkUser networkUser : server.users) {
                                if (networkUser.getUsername().equalsIgnoreCase(username) || username.equalsIgnoreCase("server")) {
                                    // TODO: Until connection bugs are fixed, just kick the currently logged in instance of the user.
                                    Packet05ConnectionStatus connectionStatus = new Packet05ConnectionStatus(Packet.Initiator.SERVER, Packet05ConnectionStatus.NICK_IN_USE);
//                                    server.sendPacketToClient(user, connectionStatus);
//                                    user.sendStatus(Packet05ConnectionStatus.NICK_IN_USE, true);
//                                    client.close();

                                    ChatMessage message = new ChatMessage();
                                    message.setMessage("You logged in at another location, so you've been disconnected here.");
                                    message.setServerMsg(true);
                                    message.setUser(server.serverUser);
                                    Packet02ChatMessage msg = new Packet02ChatMessage(Packet.Initiator.SERVER, message);
                                    server.sendPacketToClient(networkUser, msg);
                                    networkUser.disconnect("You logged in at another location, so you've been disconnected here.", true);

                                    break;
                                }
                            }



                            System.out.println("User connected: " + user.getUsername());
                            server.users.add(user);

                            user.sendStatus(Packet05ConnectionStatus.CONNECTION_SUCCESSFUL, false);

                            Packet01LoggedIn loggedIn = new Packet01LoggedIn(Packet.Initiator.SERVER, user, server.getOnlineUsers());

                            server.sendPacketToClient(user, loggedIn);

                            server.broadcastUserChange(user, true);


                        } else {

                            System.err.println("Version mismatch! Disconnecting client!");
                            server.users.add(user);
                            user.sendStatus(Packet05ConnectionStatus.VERSION_MISMATCH, true);

                            if (!client.isClosed()) {
                                client.close();

                            }
                            manage.join();
                        }
                        break;
                    case 1:
                        break;
                    case 2:
                        Packet02ChatMessage chat = new Gson().fromJson(response, Packet02ChatMessage.class);
                        if(chat.getChat().getMessage().length() >= 550) {
                            user.disconnect("Your message was too long!", true);
                        }
                        if (chat.getChat().isServerMsg()) {
                            System.out.println("Invalid server message, setting to false.");
                            chat.getChat().setServerMsg(false);
                        }

                        server.processChat(chat);

                }

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Client disconnected with exception: " + prettyGson.toJson(user));
                user.disconnect("Disconnected via Exception", false);
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
