package com.kronosad.konverse.server;

import com.google.gson.Gson;
import com.kronosad.konverse.common.objects.ChatMessage;
import com.kronosad.konverse.common.objects.ClientInfo;
import com.kronosad.konverse.common.packets.*;
import com.kronosad.konverse.common.user.AuthenticatedUser;
import com.kronosad.konverse.server.events.UserKickedEvent;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

 /**
 * @author Russell Richardson
 */
public class NetworkUser extends AuthenticatedUser {

    public transient Socket socket;

    public NetworkUser(Socket socket, String name, String uuid, boolean elevated, ClientInfo info) {
        this.socket = socket;
        this.username = name;
        this.uuid = uuid;
        this.elevated = elevated;
        this.clientInfo = info;
    }

    public void setElevated(boolean elevate) {
        if (socket == null) throw new IllegalAccessError("Socket is null! This must be ran on the Server-Side!");
        this.elevated = elevate;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public String toJSON() {
        return new Gson().toJson(this);
    }

    public void sendStatus(int status, boolean isKick) {
        if (socket == null) throw new IllegalAccessError("Socket is null! This must be ran on the Server-Side!");

        Packet05ConnectionStatus packet = new Packet05ConnectionStatus(Packet.Initiator.SERVER, status);

        if (!socket.isClosed()) {
            PrintWriter writer;
            try {
                writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println(packet.toJSON());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (isKick) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Should only be used on the Server-Side!
     *
     * @param reason Reason to kick.
     * @param isKick Used in the disconnect packet.
     * @throws java.lang.IllegalAccessError - If used on the Client-Side, or the socket is null.
     */
    public void disconnect(String reason, boolean isKick) {
        if (socket == null) {
            throw new IllegalAccessError("Socket is null! This must be ran on the Server-Side!");
        }

        Packet04Disconnect packet = new Packet04Disconnect(Packet.Initiator.SERVER, this, true);
        packet.setMessage(reason);

        ChatMessage message = new ChatMessage();
//        message.setMessage(this.getUsername() + " has left.");
        message.setUser(Server.getInstance().serverUser);

        Packet02ChatMessage chatPacket = new Packet02ChatMessage(Packet.Initiator.SERVER, message);

        Server.getInstance().eventBus.post(new UserKickedEvent(this, reason, isKick));
        Server.getInstance().users.remove(this);

        Packet03UserListChange change = new Packet03UserListChange(Packet.Initiator.SERVER, Server.getInstance().getOnlineUsers());
        change.setMessage("remove");


        try {
            Server.getInstance().sendPacketToClients(chatPacket);
            Server.getInstance().sendPacketToClients(change);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if (isKick && !socket.isClosed()) {
            PrintWriter writer;
            try {
                writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println(packet.toJSON());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
