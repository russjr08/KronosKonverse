package com.kronosad.projects.kronoskonverse.server.implementation;

import com.google.gson.Gson;
import com.kronosad.projects.kronoskonverse.common.user.User;

import java.net.Socket;
import java.util.UUID;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 5:43 PM
 */
public class NetworkUser extends User{

    private transient Socket socket;

    public NetworkUser(Socket socket, String name,  UUID uuid, boolean elevated){
        this.socket = socket;
        this.username = name;
        this.uuid = uuid.toString();
        this.elevated = elevated;
    }

    public Socket getSocket() {
        return socket;
    }

    //    @Override
//    public INetworkable fromJSON(String json) {
//        return new Gson().fromJson(json, NetworkUser.class);
//    }

    @Override
    public String toJSON() {
        return new Gson().toJson(this);
    }
}
