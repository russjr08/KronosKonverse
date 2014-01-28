package com.kronosad.projects.kronoskonverse.common.objects;

import com.google.gson.Gson;
import com.kronosad.projects.kronoskonverse.common.interfaces.INetworkable;
import com.kronosad.projects.kronoskonverse.common.user.User;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 8:32 PM
 */
public class ChatMessage implements INetworkable {
    private boolean serverMsg, action = false;
    private String message;
    private User user;
    private Room room;

    /**
     * The room associated with this chat message.
     * @return The room this chat message is associated with.
     */
    public Room getRoom() {
        return room;
    }

    /**
     * Sets the room that this message is associated with.
     * @param room The Room this message is assocated with.
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    public boolean isServerMsg() {
        return serverMsg;
    }

    public void setServerMsg(boolean serverMsg) {
        this.serverMsg = serverMsg;
    }

    public boolean isAction() {
        return action;
    }

    public void setAction(boolean action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toJSON() {
        return new Gson().toJson(this);
    }
}
