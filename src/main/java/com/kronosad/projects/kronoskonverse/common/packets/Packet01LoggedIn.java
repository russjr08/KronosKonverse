package com.kronosad.projects.kronoskonverse.common.packets;

import com.kronosad.projects.kronoskonverse.common.objects.Room;
import com.kronosad.projects.kronoskonverse.common.user.NetworkUser;
import com.kronosad.projects.kronoskonverse.common.user.User;

import java.util.ArrayList;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 7:08 PM
 */
public class Packet01LoggedIn extends Packet {
    private NetworkUser user;
    private ArrayList<User> loggedInUsers;
    private Room room;
    /**
     * All Packets should be constructed with an {@link com.kronosad.projects.kronoskonverse.common.packets.Packet.Initiator} as the parameter.
     *
     * @param initiator Initiator of said Packet.
     */
    public Packet01LoggedIn(Initiator initiator, NetworkUser user, ArrayList<User> loggedInUsers) {
        super(initiator, 01);
        if(!assertInitiator(initiator, Initiator.SERVER)){

            throw new IllegalArgumentException("Initiator MUST be Server!");

        }
        this.loggedInUsers = loggedInUsers;
        this.room = user.room;
        this.user = user;

    }

    public NetworkUser getUser() {
        return user;
    }

    public Room getRoom() {
        return room;
    }

    public ArrayList<User> getLoggedInUsers() {
        return loggedInUsers;
    }

    public void setUser(NetworkUser user) {
        this.user = user;
    }
}
