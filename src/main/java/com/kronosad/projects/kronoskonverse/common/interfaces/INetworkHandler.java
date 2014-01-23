package com.kronosad.projects.kronoskonverse.common.interfaces;

import com.kronosad.projects.kronoskonverse.common.packets.Packet;

/**
 * This interface is used by the networking API to handle packets and such.
 */
public interface INetworkHandler {

    /**
     * This method is called whenever a {@link com.kronosad.projects.kronoskonverse.common.packets.Packet} is received from the server.
     * @param packet The {@link com.kronosad.projects.kronoskonverse.common.packets.Packet} received from the Server.
     */
    public void onPacketReceived(Packet packet, String response);

}
