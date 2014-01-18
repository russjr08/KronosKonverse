package com.kronosad.projects.kronoskonverse.common.user;

import com.google.gson.Gson;
import com.kronosad.projects.kronoskonverse.common.interfaces.INetworkable;

import java.util.UUID;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 4:20 PM
 */

/**
 * Base User class, all Users should extend this class.
 * @see com.kronosad.projects.kronoskonverse.common.interfaces.INetworkable
 */
public class User implements INetworkable {

    protected String username;

    protected UUID uuid;

    protected boolean elevated = false;

    /**
     * Gets the Username of the User.
     * @return Username of this User.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns a Unique Identifier of this User.
     * @return UUID of this User.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Users who are elevated have additional permissions
     * to perform special operations. This represents an 'Admin' of the server.
     * @return Whether this User is elevated or not.
     */
    public boolean isElevated() {
        return elevated;
    }

    @Override
    public String toJSON() {
        return new Gson().toJson(this);
    }
}
