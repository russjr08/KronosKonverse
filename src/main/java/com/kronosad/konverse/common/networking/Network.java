package com.kronosad.konverse.common.networking;

import com.google.gson.Gson;
import com.kronosad.konverse.common.interfaces.INetworkHandler;
import com.kronosad.konverse.common.packets.Packet;
import com.kronosad.konverse.common.packets.Packet00Handshake;
import com.kronosad.konverse.common.packets.Packet05ConnectionStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class allows developers to easily access the network and send/receive packets to the Server.
 * It is not a static class and should be initialized per-connection.
 * Should not be used for creating a 'Server', only clients.
 */
public class Network {

    private ArrayList<INetworkHandler> handlers = new ArrayList<INetworkHandler>();

    private Thread receive;

    private boolean connected;

    private Socket connection;

    /**
     * Used to open a new connection to a Server and start listening for {@link com.kronosad.konverse.common.packets.Packet}s
     *
     * @param address   Address of Server to connect to.
     * @param port      Port of Server to connect to.
     * @param handshake The {@link com.kronosad.konverse.common.packets.Packet00Handshake} to be sent to the Server when the connection is opened.
     * @throws IOException Thrown in case there was a problem with the Network.
     */
    public Network(String address, int port, Packet00Handshake handshake) throws IOException {
        connection = new Socket(address, port);
        connected = true;
        sendPacket(handshake);
    }

    public void connect() {
        receive = new Thread("Receive Thread - KronosKonverse Network API") {
            @Override
            public void run() {
                while (Network.this.connected) {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String response = reader.readLine();

                        if (response == null) {
                            disconnect();
                            break;
                        }

                        Packet packet = new Gson().fromJson(response, Packet.class);
                        if (packet != null) {
                            System.out.println(response);
                            for (INetworkHandler handler : handlers) {
                                handler.onPacketReceived(packet, response);
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
     * Adds an {@link com.kronosad.konverse.common.interfaces.INetworkHandler} to the list of Network Handlers
     *
     * @param handler The {@link com.kronosad.konverse.common.interfaces.INetworkHandler} to add.
     */
    public void addNetworkHandler(INetworkHandler handler) {
        handlers.add(handler);
    }

    /**
     * Removes an {@link com.kronosad.konverse.common.interfaces.INetworkHandler} from the list of Network Handlers
     *
     * @param handler The {@link com.kronosad.konverse.common.interfaces.INetworkHandler} to remove.
     */
    public void removeNetworkHandler(INetworkHandler handler) {
        handlers.remove(handler);
    }

    /**
     * Disconnects from the Server.
     *
     * @throws IOException
     */
    public void disconnect() throws IOException {
        this.connected = false;
        connection.close();
    }

    /**
     * Used to send a {@link com.kronosad.konverse.common.packets.Packet} to the connected Server.
     *
     * @param packet The {@link com.kronosad.konverse.common.packets.Packet} to send to the Server.
     * @throws IOException Thrown if there was an error sending the {@link com.kronosad.konverse.common.packets.Packet}
     */
    public void sendPacket(Packet packet) throws IOException {
        if (!connected) {
            throw new IllegalAccessError("Network connection is already closed!");
        }
        PrintWriter writer = new PrintWriter(connection.getOutputStream(), true);
        System.out.println(packet.toJSON());
        writer.println(packet.toJSON());
    }

    /**
     * This is a blocking method. It waits for the server to send a response on connection status.
     *
     * @return An instance of {@link com.kronosad.konverse.common.packets.Packet05ConnectionStatus}
     */
    public Packet05ConnectionStatus getConnectionStatus() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = reader.readLine();


            Packet packet = new Gson().fromJson(response, Packet.class);
            System.out.println(packet);
            if (packet.getId() != 5) {
                throw new IllegalArgumentException("Invalid Packet Received: Packet was NOT a connection status packet!");
            }

            return new Gson().fromJson(response, Packet05ConnectionStatus.class);

        } catch (IOException e) {
            e.printStackTrace();

            throw new IllegalStateException("Unable to connect to the server!", e);

        }

    }


}
