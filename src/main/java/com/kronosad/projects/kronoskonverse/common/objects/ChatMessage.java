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
