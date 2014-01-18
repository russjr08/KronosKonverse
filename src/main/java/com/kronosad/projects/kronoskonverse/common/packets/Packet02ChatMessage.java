package com.kronosad.projects.kronoskonverse.common.packets;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 8:31 PM
 */
public class Packet02ChatMessage extends Packet {

    /**
     * All Packets should be constructed with an {@link com.kronosad.projects.kronoskonverse.common.packets.Packet.Initiator} as the parameter.
     *
     * @param initiator Initiator of said Packet.
     */
    public Packet02ChatMessage(Initiator initiator) {
        super(initiator, 02);
    }


}
