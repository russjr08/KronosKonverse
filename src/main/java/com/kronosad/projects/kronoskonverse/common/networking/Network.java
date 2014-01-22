package com.kronosad.projects.kronoskonverse.common.networking;

import com.google.gson.Gson;
import com.kronosad.projects.kronoskonverse.common.interfaces.INetworkHandler;
import com.kronosad.projects.kronoskonverse.common.packets.Packet;
import com.kronosad.projects.kronoskonverse.common.packets.Packet00Handshake;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class allows developers to easily access the network and send/receive packets to the server.
 * It is not a static class and should be initialized per-connection.
 * Should not be used for creating a 'server', only clients.
 */
public class Network {

    private ArrayList<INetworkHandler> handlers = new ArrayList<INetworkHandler>();

    private Thread receive;

    private boolean connected;

    private Socket connection;

    /**
     * Used to open a new connection to a server and start listening for {@link com.kronosad.projects.kronoskonverse.common.packets.Packet}s
     * @param address Address of server to connect to.
     * @param port Port of server to connect to.
     * @param handshake The {@link com.kronosad.projects.kronoskonverse.common.packets.Packet00Handshake} to be sent to the server when the connection is opened.
     * @param networkHandler The inital {@link com.kronosad.projects.kronoskonverse.common.interfaces.INetworkHandler} to use for handling packets.
     * @throws IOException Thrown in case there was a problem with the Network.
     */
    public Network(String address, int port, Packet00Handshake handshake, INetworkHandler networkHandler) throws IOException {
        connection = new Socket(address, port);
        connected = true;
        handlers.add(networkHandler);
        sendPacket(handshake);
        receive = new Thread("Receive Thread - KronosKonverse Network API"){
            @Override
            public void run() {
                while(Network.this.connected){
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String response = reader.readLine();

                        if(response == null){
                            disconnect();
                            break;
                        }

                        Packet packet = new Gson().fromJson(response, Packet.class);
                        if(packet != null){
                            for(INetworkHandler handler : handlers){
                                handler.onPacketReceived(packet);
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        receive.start();

    }

    /**
     * Adds an {@link com.kronosad.projects.kronoskonverse.common.interfaces.INetworkHandler} to the list of Network Handlers
     * @param handler The {@link com.kronosad.projects.kronoskonverse.common.interfaces.INetworkHandler} to add.
     */
    public void addNetworkHandler(INetworkHandler handler){
        handlers.add(handler);
    }

    /**
     * Disconnects from the server.
     * @throws IOException
     */
    public void disconnect() throws IOException {
        this.connected = false;
        connection.close();
    }

    /**
     * Used to send a {@link com.kronosad.projects.kronoskonverse.common.packets.Packet} to the connected server.
     * @param packet The {@link com.kronosad.projects.kronoskonverse.common.packets.Packet} to send to the server.
     * @throws IOException Thrown if there was an error sending the {@link com.kronosad.projects.kronoskonverse.common.packets.Packet}
     */
    public void sendPacket(Packet packet) throws IOException {
        if(!connected){ throw new IllegalAccessError("Network connection is already closed!"); }
        PrintWriter writer = new PrintWriter(connection.getOutputStream());
        writer.println(packet.toJSON());
    }



}
