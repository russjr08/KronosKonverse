package com.kronosad.konverse.common.auth;

import com.google.gson.Gson;

import com.kronosad.konverse.common.KonverseAPI;
import com.kronosad.konverse.common.auth.exceptions.AuthenticationFailedException;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

import java.io.IOException;

/**
 * A utility class for contacting an Authentication Server to login / verify a user's identity. You do not have to use
 * this class if you want to do things manually.
 * However, this class can also handle custom Authentication Server's (as long as they follow the same URL paths), just
 * use the {@link com.kronosad.konverse.common.auth.Authentication#Authentication(String)} constructor.
 */
public class Authentication {

    private String auth_server;

    /**
     * Sets up an authentication object using the default authentication server.
     * @see com.kronosad.konverse.common.KonverseAPI#DEFAULT_AUTH_SERVER
     */
    public Authentication() {
        this.auth_server = KonverseAPI.DEFAULT_AUTH_SERVER;
    }

    /**
     * Sets up an Authentication object using a custom authentication server. <STRONG>Obviously, both the client AND
     * the server must use the same authentication server, or the second step of authentication will fail! (Token check)
     * </STRONG>
     * @param server A custom authentication server to use. Use {@link #Authentication()} for the default authentication
     *               server.
     */
    public Authentication(String server) {
        this.auth_server = server;
    }

    /**
     * Allows logging in to the selected Authentication Server. <STRONG>Please note that the password is encrypted at
     * the server
     * side for the moment. (Using SHA256) </STRONG>
     * @param username Username of the user.
     * @param password Password of the user.
     *
     * @return An instance of {@link com.kronosad.konverse.common.auth.AuthenticationLoggedInMessage}
     * which provides the authenticated username and an Authentication Token.
     *
     * @throws IOException Thrown if there was an error contacting the Authentication Server.
     *
     * @throws AuthenticationFailedException Thrown if Authentication Failed and contains the message returned
     * by the Authentication Server.
     */
    public AuthenticationLoggedInMessage login(String username, String password) throws IOException, AuthenticationFailedException {
        String resultingJson = Request.Post(auth_server + "/api/login/")
                .bodyForm(Form.form().add("username", username).add("password", password).build())
                .execute()
                .returnContent()
                .toString();

        AuthenticationMessage message = new Gson().fromJson(resultingJson, AuthenticationMessage.class);

        // Check to see if login was successful or not.
        if(message.getMessage().equalsIgnoreCase(KonverseAPI.AUTHENTICATION_SUCCESSFUL)) {
            return new Gson().fromJson(resultingJson, AuthenticationLoggedInMessage.class);
        } else {
            throw new AuthenticationFailedException("Authentication Server returned: " + message.getMessage());
        }
    }

    /**
     * Allows a third party to verify a user's identity using a token system. On login a user is given an authentication
     * token, the user provides this to the third party, which uses this in combination of the user's username. The
     * Authentication Server then will send an answer stating whether the token is valid or not. It will only be valid
     * if the token matches AND if the username matches, otherwise the Authentication Server will reply with an "Invalid
     * Token" message.
     * @param username Username of the user.
     * @param token Authentication Token of the user provided by the Authentication Server.
     * @return True if the token and username match. False if they don't match.
     * @throws IOException Thrown if there was an error contacting the Authentication Server.
     */
    public boolean verifyAuthToken(String username, String token) throws IOException {
        String resultingJson = Request.Post(auth_server + "/api/check_token/")
                .bodyForm(Form.form().add("username", username).add("auth_token", token).build())
                .execute()
                .returnContent()
                .toString();

        AuthenticationMessage message = new Gson().fromJson(resultingJson, AuthenticationMessage.class);

        return message.getMessage().equalsIgnoreCase(KonverseAPI.AUTHENTICATION_TOKEN_VALID);
    }

}
