package com.kronosad.konverse.common.packets;

import com.kronosad.konverse.common.user.User;

import java.util.List;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 11:41 PM
 */
public class Packet03UserListChange extends Packet {

    private List<User> online;

    /**
     * All Packets should be constructed with an {@link com.kronosad.konverse.common.packets.Packet.Initiator} as the parameter.
     *
     * @param initiator Initiator of said Packet.
     */
    public Packet03UserListChange(Initiator initiator, List<User> user) {
        super(initiator, 03);
        this.online = user;

    }

    public List<User> getOnlineUsers() {
        return online;
    }
}
