package com.kronosad.projects.kronoskonverse.common.objects;

import com.kronosad.projects.kronoskonverse.common.user.User;

/**
 * Used for sending private messages to another recipient.
 */
public class PrivateMessage extends ChatMessage {

    private User recipient;

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }
}
