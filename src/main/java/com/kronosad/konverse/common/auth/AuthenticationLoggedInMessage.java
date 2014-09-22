package com.kronosad.konverse.common.auth;

public class AuthenticationLoggedInMessage extends AuthenticationMessage {

    private String username;
    private String auth_token;

    /**
     * @return The username of the user who logged in successfully.
     */
    public String getUsername() {
        return username;
    }

    /**
     * An authentication token is a way of identifying to a third party that you are who you claim to be. The third
     * party sends this to the authentication server and it'll return a message claiming whether the token is valid or
     * not for the username specified. The token is a random sized (up to 100 chars) string that contains both letters
     * and numbers.
     * @return The authentication token generated for the login session.
     */
    public String getAuthToken() {
        return auth_token;
    }

    @Override
    public String toString() {
        return "AuthenticationLoggedInMessage{" +
                "username='" + username + '\'' +
                ", auth_token='" + auth_token + '\'' +
                "} " + super.toString();
    }
}
