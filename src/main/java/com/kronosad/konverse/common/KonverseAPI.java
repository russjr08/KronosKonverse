package com.kronosad.konverse.common;

import com.kronosad.konverse.common.objects.Version;

/**
 * User: russjr08
 * Date: 1/20/14
 * Time: 6:36 PM
 */
public class KonverseAPI {
    public static final Version API_VERSION = new Version().setProtocol("2.0-BETA").setReadable("2.0 Beta");
    public static final String DEFAULT_AUTH_SERVER = "http://auth.kronosad.com";

    public static final String AUTHENTICATION_SUCCESSFUL = "Authentication Successful.";
    public static final String AUTHENTICATION_FAILED = "Authentication Failed.";
    public static final String AUTHENTICATION_TOKEN_VALID = "Authentication Token is valid.";
    public static final String AUTHENTICATION_TOKEN_INVALID = "Authentication Token is invalid.";

    public static final String AUTHENTICATION_PROFILE_USER_FOUND = "User exists.";


}
