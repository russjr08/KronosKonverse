package com.kronosad.projects.kronoskonverse.common.packets;

import com.google.gson.Gson;
import com.kronosad.projects.kronoskonverse.common.objects.ChatMessage;
import com.kronosad.projects.kronoskonverse.common.objects.PrivateMessage;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 8:31 PM
 */
public class Packet02ChatMessage extends Packet {
    private ChatMessage chat;
    private PrivateMessage privateMessage;
    private boolean isPrivate;

    /**
     * All Packets should be constructed with an {@link com.kronosad.projects.kronoskonverse.common.packets.Packet.Initiator} as the parameter.
     *
     * @param initiator Initiator of said Packet.
     */
    public Packet02ChatMessage(Initiator initiator, ChatMessage chat) {
        super(initiator, 02);
        this.chat = chat;
        if(chat instanceof PrivateMessage){
            this.privateMessage = (PrivateMessage)chat;
            this.setPrivate(true);
        }

    }

    @Override
    public String toJSON() {
        return new Gson().toJson(this);
    }

    public ChatMessage getChat() {
        return chat;
    }

    public PrivateMessage getPrivateMessage() {
        return privateMessage;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    /**
     * Basically does the same thing as getChat().getMessage(); A few people kept using this instead of getChat().getMessage();
     * @return The chat message of this chat packet.
     */
    @Override
    public String getMessage() {
        return getChat().getMessage();
    }

}
