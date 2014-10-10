package com.kronosad.konverse.server.misc;

import com.kronosad.konverse.common.auth.AuthenticatedUserProfile;

import java.util.ArrayList;

/**
 * Created by russjr08 on 9/25/14.
 */
public class OperatorList {

    private ArrayList<AuthenticatedUserProfile> ops = new ArrayList<>();

    public ArrayList<AuthenticatedUserProfile> getOps() {
        return ops;
    }

}
