package com.kronosad.konverse.common.auth;

public class AuthenticationMessage {

    private String message;

    /**
     * @return The message passed back from the authentication system. This could be an error, or a confirmation message.
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "AuthenticationMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
