package com.kronosad.projects.kronoskonverse.common.packets;

import com.kronosad.projects.kronoskonverse.server.NetworkUser;

import java.util.ArrayList;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 11:41 PM
 */
public class Packet03UserListChange extends Packet {

    private ArrayList<NetworkUser> networkUsers;

    /**
     * All Packets should be constructed with an {@link com.kronosad.projects.kronoskonverse.common.packets.Packet.Initiator} as the parameter.
     *
     * @param initiator Initiator of said Packet.
     */
    public Packet03UserListChange(Initiator initiator, ArrayList<NetworkUser> user) {
        super(initiator, 03);
        this.networkUsers = user;

    }

    public ArrayList<NetworkUser> getOnlineUsers() {
        return networkUsers;
    }
}
