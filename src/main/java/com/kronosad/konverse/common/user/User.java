package com.kronosad.konverse.common.user;

import com.google.gson.Gson;
import com.kronosad.konverse.common.interfaces.INetworkable;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 4:20 PM
 */

/**
 * Base User class, all Users should extend this class.
 *
 * @see com.kronosad.konverse.common.interfaces.INetworkable
 */
public class User implements INetworkable {

    protected String username;

    protected boolean elevated = false;

    /**
     * Gets the Username of the User.
     *
     * @return Username of this User.
     */
    public String getUsername() {
        return username;
    }


    /**
     * Users who are elevated have additional permissions
     * to perform special operations. This represents an 'Admin' of the Server
     *
     * @return Whether this User is elevated or not.
     */
    public boolean isElevated() {
        return elevated;
    }

    @Override
    public String toJSON() {
        return new Gson().toJson(this);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", elevated=" + elevated +
                '}';
    }
}
