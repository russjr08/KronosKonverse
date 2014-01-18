package com.kronosad.projects.kronoskonverse.common.packets;

import com.kronosad.projects.kronoskonverse.server.implementation.NetworkUser;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 11:41 PM
 */
public class Packet03UserListChange extends Packet {
    private NetworkUser user;

    /**
     * All Packets should be constructed with an {@link com.kronosad.projects.kronoskonverse.common.packets.Packet.Initiator} as the parameter.
     *
     * @param initiator Initiator of said Packet.
     */
    public Packet03UserListChange(Initiator initiator, NetworkUser user) {
        super(initiator, 03);
        this.user = user;

    }

    public NetworkUser getUser() {
        return user;
    }
}
