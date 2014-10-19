package com.kronosad.konverse.server.events;

import com.kronosad.konverse.common.user.User;

/**
 * This event is posted whenever a {@link com.kronosad.konverse.common.user.User} <strong>successfully</strong>
 * joins the {@link com.kronosad.konverse.server.Server}
 */
public class UserJoinedEvent {
    private User user;

    public UserJoinedEvent(User user) {
        this.user = user;
    }

    /**
     * @return The {@link com.kronosad.konverse.common.user.User} who joined the {@link com.kronosad.konverse.server.Server}.
     */
    public User getUser() {
        return user;
    }
}
