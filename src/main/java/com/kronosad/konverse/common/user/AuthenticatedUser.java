package com.kronosad.konverse.common.user;

/**
 * Author russjr08
 * Created at 4/19/14
 */
public class AuthenticatedUser extends User {

    protected String uuid;


    /**
     * Returns a Unique Identifier of this User. This is like a 'session token' that is given to the client for the time
     * they are connceted. The client needs to send this to the server to authenticate themselves to do certain actions
     * such as Sending Messages, Changing a Nickname, Admin actions, etc.
     *
     * @return UUID of this User.
     */
    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "AuthenticatedUser{" +
                "uuid='" + uuid + '\'' +
                "} " + super.toString();
    }
}
