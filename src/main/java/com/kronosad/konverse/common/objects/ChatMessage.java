package com.kronosad.konverse.common.objects;

import com.google.gson.Gson;
import com.kronosad.konverse.common.interfaces.INetworkable;
import com.kronosad.konverse.common.user.AuthenticatedUser;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 8:32 PM
 */
public class ChatMessage implements INetworkable {

    private boolean serverMsg, action = false;
    private String message;
    private AuthenticatedUser user;

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

    public AuthenticatedUser getUser() {
        return user;
    }

    public void setUser(AuthenticatedUser user) {
        this.user = user;
    }

    @Override
    public String toJSON() {
        return new Gson().toJson(this);
    }
}
