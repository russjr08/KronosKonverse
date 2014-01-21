package com.kronosad.projects.kronoskonverse.common.packets;

import com.kronosad.projects.kronoskonverse.common.user.NetworkUser;

import java.util.ArrayList;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 7:08 PM
 */
public class Packet01LoggedIn extends Packet {
    private NetworkUser user;
    private ArrayList<NetworkUser> loggedInUsers;
    /**
     * All Packets should be constructed with an {@link com.kronosad.projects.kronoskonverse.common.packets.Packet.Initiator} as the parameter.
     *
     * @param initiator Initiator of said Packet.
     */
    public Packet01LoggedIn(Initiator initiator, NetworkUser user, ArrayList<NetworkUser> loggedInUsers) {
        super(initiator, 01);
        if(!assertInitiator(initiator, Initiator.SERVER)){

            throw new IllegalArgumentException("Initiator MUST be Server!");

        }
        this.loggedInUsers = loggedInUsers;

        this.user = user;

    }

    public NetworkUser getUser() {
        return user;
    }

    public ArrayList<NetworkUser> getLoggedInUsers() {
        return loggedInUsers;
    }

    public void setUser(NetworkUser user) {
        this.user = user;
    }
}
