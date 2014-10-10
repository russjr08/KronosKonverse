package com.kronosad.konverse.client.interfaces;

/**
 * Author russjr08
 * Created at 4/19/14
 */

/**
 * A class that implements {@link com.kronosad.konverse.client.interfaces.IInputHandler} is able to handle messages possibly
 * from a GUI and send it off to the network.
 */
public interface IInputHandler {

    public void handleInput(String message);

}
