package com.kronosad.konverse.server.events;

import com.kronosad.konverse.common.user.User;

/**
 * This event is <strong>sometimes</strong> posted when a {@link com.kronosad.konverse.common.user.User} left the Server.
 */
public class UserLeftEvent {

    private User user;
    private Throwable throwable;

    /**
     * @param user The User who left
     * @param throwable The {@link java.lang.Exception} thrown when the user left. (May be null!)
     */
    public UserLeftEvent(User user, Throwable throwable) {
        this.user = user;
        this.throwable = throwable;
    }

    /**
     * @return The {@link com.kronosad.konverse.common.user.User} who left.
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the {@link java.lang.Exception} that may have been thrown when the {@link com.kronosad.konverse.common.user.User} left.
     * @return The {@link java.lang.Exception} thrown when the {@link com.kronosad.konverse.common.user.User} left. (May be null!)
     */
    public Throwable getThrowable() {
        return throwable;
    }

}
