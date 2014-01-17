package com.kronosad.projects.kronoskonverse.common.packets;

import com.kronosad.projects.kronoskonverse.common.interfaces.INetworkable;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 4:44 PM
 */

/**
 * An abstract class representing 'Packets' (Not real packets, TCP is stream-based.) sent across the network.
 * @see com.kronosad.projects.kronoskonverse.common.interfaces.INetworkable
 */
public abstract class Packet implements INetworkable {
    protected Initiator initiator = Initiator.UNKNOWN;

    protected int id;
    protected String message;
    protected INetworkable payload;

    /**
     * All Packets should be constructed with an {@link com.kronosad.projects.kronoskonverse.common.packets.Packet.Initiator} as the parameter.
     * @param initiator Initiator of said Packet.
     */
    public Packet(Initiator initiator){
        this.initiator = initiator;
    }

    /**
     * Sets the ID of this Packet.
     * @param id ID of this Packet.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the message of this Packet.
     * @param message Message of this Packet.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Data of this Packet, which should be {@link java.io.Serializable} / {@link com.kronosad.projects.kronoskonverse.common.interfaces.INetworkable}
     * @param payload Data of the Packet.
     */
    public void setPayload(INetworkable payload) {
        this.payload = payload;
    }

    /**
     * @return The {@link com.kronosad.projects.kronoskonverse.common.packets.Packet.Initiator} of who sent this Packet.
     */
    public Initiator getInitiator() {
        return initiator;
    }

    /**
     * Packet IDs are used for verifying the expected data on both sides of the network.
     * @return The ID of this Packet.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The message of this Packet.
     */
    public String getMessage() {
        return message;
    }

    /**
     * The data that this packet contains, the data should be {@link java.io.Serializable} / {@link com.kronosad.projects.kronoskonverse.common.interfaces.INetworkable}
     * @return The data that this packet contains.
     */
    public INetworkable getPayload() {
        return payload;
    }

    /**
     * The side who first sent this packet.
     */
    public enum Initiator{
        SERVER, CLIENT, UNKNOWN
    }



}
