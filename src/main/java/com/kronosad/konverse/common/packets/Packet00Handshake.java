package com.kronosad.konverse.common.packets;

import com.kronosad.konverse.common.objects.ClientInfo;
import com.kronosad.konverse.common.objects.Version;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 4:59 PM
 */

/**
 * The Handshake Packet. The client sends this packet to the Server, which tells theServer to prepare a UUID for the user and
 * prepare a User object to be sent back to them.
 */
public class Packet00Handshake extends Packet {

    private Version version;
    private String username;
    private String auth_token;
    private ClientInfo clientInfo;

    public Packet00Handshake(Initiator initiator, String username, String auth_token, Version version, ClientInfo info) {
        super(Initiator.CLIENT, 0);
        if (initiator != Initiator.CLIENT) {
            throw new IllegalArgumentException("The Initiator of this Packet MUST be the client!");
        }

        this.setMessage("#Handshake-" + username);
        this.auth_token = auth_token;
        this.username = username;
        this.version = version;
        this.clientInfo = info;

    }

    public Version getVersion() {
        return version;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return auth_token;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setVersion(Version version) {
        this.version = version;
    }
}
