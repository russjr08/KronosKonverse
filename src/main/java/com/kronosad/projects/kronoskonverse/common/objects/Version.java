package com.kronosad.projects.kronoskonverse.common.objects;

import com.google.gson.Gson;
import com.kronosad.projects.kronoskonverse.common.interfaces.INetworkable;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 7:00 PM
 */
public class Version implements INetworkable {

    private String protocol;
    private String readable;

    public String getProtocol() {
        return protocol;
    }

    public Version setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getReadable() {
        return readable;
    }

    public Version setReadable(String readable) {
        this.readable = readable;
        return this;
    }

//    @Override
//    public INetworkable fromJSON(String json) {
//        return new Gson().fromJson(json, Version.class);
//    }

    @Override
    public String toJSON() {
        return new Gson().toJson(this);
    }
}
