package com.kronosad.konverse.common.user;

import com.google.gson.Gson;
import com.kronosad.konverse.common.interfaces.INetworkable;
import com.kronosad.konverse.common.objects.ClientInfo;

/**
 * Base User class, all Users should extend this class.
 *
 * @see com.kronosad.konverse.common.interfaces.INetworkable
 */
public class User implements INetworkable {

    protected String username;
    protected String nickname;

    protected boolean elevated = false;

    protected ClientInfo clientInfo;

    /**
     * Gets the Username of the User.
     *
     * @return Username of this User.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the client info of the User.
     * @return An instance of {@link com.kronosad.konverse.common.objects.ClientInfo}
     * @see com.kronosad.konverse.common.objects.ClientInfo
     */
    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    /**
     * The nickname of this user (Can be null!)
     * @return The nickname of this user.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets this User's client info.
     * @param clientInfo This User's {@link com.kronosad.konverse.common.objects.ClientInfo}
     */
    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    /**
     * Sets the nickname for this user.
     * @param nickname The new nickname for this user.
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
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
