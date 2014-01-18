package com.kronosad.projects.kronoskonverse.common.packets;

import com.kronosad.projects.kronoskonverse.common.objects.ChatMessage;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 8:31 PM
 */
public class Packet02ChatMessage extends Packet {
    private ChatMessage chat;

    /**
     * All Packets should be constructed with an {@link com.kronosad.projects.kronoskonverse.common.packets.Packet.Initiator} as the parameter.
     *
     * @param initiator Initiator of said Packet.
     */
    public Packet02ChatMessage(Initiator initiator, ChatMessage chat) {
        super(initiator, 02);
        this.chat = chat;
    }

    public ChatMessage getChat() {
        return chat;
    }
}
