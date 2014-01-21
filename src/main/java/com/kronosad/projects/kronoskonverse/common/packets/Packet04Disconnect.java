package com.kronosad.projects.kronoskonverse.common.packets;

import com.kronosad.projects.kronoskonverse.common.user.NetworkUser;

/**
 * User: russjr08
 * Date: 1/20/14
 * Time: 4:25 PM
 */

/**
 * A packet for saying that a client disconnected or should be disconnected (Kick).
 */
public class Packet04Disconnect extends Packet {
    private NetworkUser disconnected;
    private boolean isKick;

    /**
     * All Packets should be constructed with an {@link com.kronosad.projects.kronoskonverse.common.packets.Packet.Initiator} as the parameter.
     *
     * @param initiator Initiator of said Packet.
     */
    public Packet04Disconnect(Initiator initiator, NetworkUser disconnected, boolean isKick) {
        super(initiator, 04);
        this.disconnected = disconnected;
        this.isKick = isKick;


    }

    public NetworkUser getDisconnected() {
        return disconnected;
    }

    public boolean isKick() {
        return isKick;
    }

}
