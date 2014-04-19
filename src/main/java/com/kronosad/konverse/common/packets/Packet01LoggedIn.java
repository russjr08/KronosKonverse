package com.kronosad.konverse.common.packets;

import com.kronosad.konverse.common.user.AuthenticatedUser;
import com.kronosad.konverse.common.user.User;

import java.util.List;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 7:08 PM
 */
public class Packet01LoggedIn extends Packet {
    private AuthenticatedUser user;

    private List<User> loggedInUsers;

    /**
     * All Packets should be constructed with an {@link com.kronosad.konverse.common.packets.Packet.Initiator} as the parameter.
     *
     * @param initiator Initiator of said Packet.
     */
    public Packet01LoggedIn(Initiator initiator, AuthenticatedUser user, List<User> loggedInUsers) {
        super(initiator, 01);
        if (!assertInitiator(initiator, Initiator.SERVER)) {

            throw new IllegalArgumentException("Initiator MUST be Server!");

        }
        this.loggedInUsers = loggedInUsers;

        this.user = user;

    }

    public User getUser() {
        return user;
    }

    public List<User> getLoggedInUsers() {
        return loggedInUsers;
    }

    public void setUser(AuthenticatedUser user) {
        this.user = user;
    }
}
