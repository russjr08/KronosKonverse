package com.kronosad.projects.kronoskonverse.common.packets;

import com.google.gson.Gson;
import com.kronosad.projects.kronoskonverse.common.interfaces.INetworkable;

import java.io.Serializable;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 4:44 PM
 */

/**
 * An abstract class representing 'Packets' (Not real packets, TCP is stream-based.) sent across the network.
 * @see com.kronosad.projects.kronoskonverse.common.interfaces.INetworkable
 */
public class Packet implements INetworkable {
    protected Initiator initiator = Initiator.UNKNOWN;

    protected int id;
    protected String message;
    protected INetworkable payload;

    /**
     * @deprecated Do not use this constructor, it's only meant to be used for deserialization/GSON purposes!
     */
    private Packet(){}

    /**
     * All Packets should be constructed with an {@link com.kronosad.projects.kronoskonverse.common.packets.Packet.Initiator} as the parameter.
     * @param initiator Initiator of said Packet.
     */
    public Packet(Initiator initiator, int id){
        this.initiator = initiator;
        this.id = id;
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


//    @Override
//    public INetworkable fromJSON(String json) {
//        return new Gson().fromJson(json, Packet.class);
//    }

    @Override
    public String toJSON() {
        return new Gson().toJson(this);
    }

    /**
     * The side who first sent this packet.
     */
    public enum Initiator implements Serializable{
        SERVER, CLIENT, UNKNOWN
    }

    protected boolean assertInitiator(Initiator current, Initiator correctInitiator){
        return current == correctInitiator;
    }

    @Override
    public String toString() {
        return toJSON();
    }
}
