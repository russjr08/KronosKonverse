package com.kronosad.konverse.server.events;

import com.kronosad.konverse.common.user.User;

/**
 * @author Russell Richardson
 */
public class UserKickedEvent {

    private User user;
    private String reason;
    private boolean isKick;

    public UserKickedEvent(User user, String reason, boolean isKick) {
        this.user = user;
        this.reason = reason;
        this.isKick = isKick;
    }

    /**
     * @return The {@link com.kronosad.konverse.common.user.User} who was kicked.
     */
    public User getUser() {
        return user;
    }

    /**
     * @return The reason the {@link com.kronosad.konverse.common.user.User} was kicked.
     */
    public String getReason() {
        return reason;
    }

    /**
     * @return If the {@link com.kronosad.konverse.common.user.User} was really kicked or if they were just forced "disconnected"
     */
    public boolean isKick() {
        return isKick;
    }
}
