package com.kronosad.projects.kronoskonverse.common.packets;

import com.kronosad.projects.kronoskonverse.server.implementation.NetworkUser;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 7:08 PM
 */
public class Packet01LoggedIn extends Packet {
    private NetworkUser user;
    /**
     * All Packets should be constructed with an {@link com.kronosad.projects.kronoskonverse.common.packets.Packet.Initiator} as the parameter.
     *
     * @param initiator Initiator of said Packet.
     */
    public Packet01LoggedIn(Initiator initiator, NetworkUser user) {
        super(initiator, 01);
        if(!assertInitiator(initiator, Initiator.SERVER)){

            throw new IllegalArgumentException("Initiator MUST be Server!");

        }

        this.user = user;

    }

    public NetworkUser getUser() {
        return user;
    }

    public void setUser(NetworkUser user) {
        this.user = user;
    }
}
