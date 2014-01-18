package com.kronosad.projects.kronoskonverse.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kronosad.projects.kronoskonverse.common.packets.Packet;
import com.kronosad.projects.kronoskonverse.common.packets.Packet00Handshake;
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

    public ConnectionHandler(Server server, Socket client){
        this.server = server;
        this.client = client;
        prettyGson = new GsonBuilder().setPrettyPrinting().create();

        new Thread(this).start();
    }


    @Override
    public void run() {
        try {
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String response = inputStream.readLine();
            Packet packet = new Gson().fromJson(response, Packet.class);

            if(packet.getId() == 0){
                System.out.println("Parsing handshake packet!");
                Packet00Handshake handshake = new Gson().fromJson(response, Packet00Handshake.class);

                NetworkUser user = new NetworkUser(client, handshake.getMessage().split("-")[1], UUID.randomUUID(), false);

                System.out.println("User connected!");
                System.out.println(prettyGson.toJson(user));
            }

            PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
