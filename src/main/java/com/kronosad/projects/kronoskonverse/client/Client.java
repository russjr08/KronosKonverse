package com.kronosad.projects.kronoskonverse.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kronosad.projects.kronoskonverse.common.objects.Version;
import com.kronosad.projects.kronoskonverse.common.packets.Packet;
import com.kronosad.projects.kronoskonverse.common.packets.Packet00Handshake;
import com.kronosad.projects.kronoskonverse.common.packets.Packet01LoggedIn;
import com.kronosad.projects.kronoskonverse.server.implementation.NetworkUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 6:40 PM
 */
public class Client implements Runnable {
    private static Socket socket;
    private static Version version = new Version().setProtocol("1.0-ALPHA").setReadable("1.0 Alpha");

    private static Thread receive;

    private NetworkUser user;

    private Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

    private Client(){
        try {
            socket = new Socket("localhost", 8081);
            Packet00Handshake handshake = new Packet00Handshake(Packet.Initiator.CLIENT, "russjr08");
            handshake.setVersion(version);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println(new Gson().toJson(handshake));
            System.out.println(prettyGson.toJson(handshake));

            receive = new Thread(this);
            receive.start();


        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error connecting to server!");
        }
    }
    public static void main(String... args){
        new Client();
    }


    @Override
    public void run() {
        while (true){
            BufferedReader reader;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = reader.readLine();

                Packet packet = new Gson().fromJson(response, Packet.class);

                if(packet.getId() == 01){
                    Packet01LoggedIn loggedIn = new Gson().fromJson(response, Packet01LoggedIn.class);
                    System.out.println("Received Logged In packet! Parsing now...");
                    System.out.println(prettyGson.toJson(loggedIn));

                    user = (NetworkUser) loggedIn.getUser();

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
