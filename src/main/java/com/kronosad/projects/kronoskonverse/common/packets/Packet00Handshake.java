package com.kronosad.projects.kronoskonverse.common.packets;

import com.kronosad.projects.kronoskonverse.common.objects.Version;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 4:59 PM
 */

/**
 * The Handshake Packet. The client sends this packet to the server, which tells the server to prepare a UUID for the user and
 * prepare a User object to be sent back to them.
 */
public class Packet00Handshake extends Packet {

    private Version version;

    public Packet00Handshake(Initiator initiator, String username) {
        super(Initiator.CLIENT, 0);

        if(initiator != Initiator.CLIENT){
            throw new IllegalArgumentException("The Initiator of this Packet MUST be the client!");
        }

        this.setMessage("#Handshake-" + username);

    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }
}
