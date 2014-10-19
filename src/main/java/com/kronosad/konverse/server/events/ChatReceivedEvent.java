package com.kronosad.konverse.server.events;

import com.kronosad.konverse.common.objects.ChatMessage;
import com.kronosad.konverse.common.packets.Packet02ChatMessage;
import com.kronosad.konverse.common.user.User;

/**
 * This event is posted whenever the {@link com.kronosad.konverse.server.Server} receives a Chat Packet from a client.
 */
public class ChatReceivedEvent {

    private Packet02ChatMessage chatPacket;

    public ChatReceivedEvent(Packet02ChatMessage msg) {
        this.chatPacket = msg;
    }

    public Packet02ChatMessage getChatPacket() { return chatPacket; }
    public User getUser() { return chatPacket.getChat().getUser(); }
    public ChatMessage getMessage() { return chatPacket.getChat(); }

}
