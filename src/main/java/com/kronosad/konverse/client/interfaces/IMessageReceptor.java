package com.kronosad.konverse.client.interfaces;

/**
 * Author russjr08
 * Created at 4/19/14
 */

import com.kronosad.konverse.common.objects.ChatMessage;
import com.kronosad.konverse.common.objects.PrivateMessage;

/**
 * A class that implements {@link com.kronosad.konverse.client.interfaces.IMessageReceptor} is able to receive and handle
 * a {@link com.kronosad.konverse.common.objects.ChatMessage}
 */
public interface IMessageReceptor {

    public void handleMessage(ChatMessage message);
    public void handlePrivateMessage(PrivateMessage message);

}
