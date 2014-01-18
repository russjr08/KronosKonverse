package com.kronosad.projects.kronoskonverse.server;

import com.kronosad.projects.kronoskonverse.common.objects.ChatMessage;
import com.kronosad.projects.kronoskonverse.common.objects.Version;
import com.kronosad.projects.kronoskonverse.common.packets.Packet;
import com.kronosad.projects.kronoskonverse.common.packets.Packet02ChatMessage;
import com.kronosad.projects.kronoskonverse.common.user.User;
import com.kronosad.projects.kronoskonverse.server.implementation.NetworkUser;

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
    private int port;

    private ServerSocket server;
    private Version version = new Version().setProtocol("1.0-ALPHA").setReadable("1.0 Alpha");

    protected User serverUser;
    protected ArrayList<NetworkUser> users = new ArrayList<NetworkUser>();



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

        this.port = port;

        System.out.println("Opening server on port: " + port);

        try {
            server = new ServerSocket(port);
            System.out.println("Sucessfully bounded to port!");
            serve();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("There was a problem binding to port: " + port);
        }

        while(true){
            Scanner in = new Scanner(System.in);
            String response = in.nextLine();

            if(response.equalsIgnoreCase("stop")){
                try {
                    System.out.println("Stopping server...");
                    this.server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

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

    public void sendPacketToClients(Packet packet) throws IOException {
        if(packet instanceof Packet02ChatMessage){
            Packet02ChatMessage chatPacket = (Packet02ChatMessage) packet;
            if(((Packet02ChatMessage) packet).getChat().getMessage().isEmpty()){ return; /** Null message, don't send. **/}
            System.out.println("[" + chatPacket.getChat().getUser().getUsername() + "] " + chatPacket.getChat().getMessage());
        }
        for(NetworkUser user : users){
            PrintWriter writer = new PrintWriter(user.getSocket().getOutputStream(), true);
            writer.println(packet.toJSON());
        }


    }

    public void serve(){
        new Thread(){
            public void run(){
                while (true){
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

}
