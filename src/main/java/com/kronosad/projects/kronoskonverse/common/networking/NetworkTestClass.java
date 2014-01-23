package com.kronosad.projects.kronoskonverse.common.networking;

import com.google.gson.Gson;
import com.kronosad.projects.kronoskonverse.common.KronosKonverseAPI;
import com.kronosad.projects.kronoskonverse.common.interfaces.INetworkHandler;
import com.kronosad.projects.kronoskonverse.common.objects.ChatMessage;
import com.kronosad.projects.kronoskonverse.common.packets.*;
import com.kronosad.projects.kronoskonverse.common.user.User;

import java.io.IOException;

// You can implement INetworkHandler in your main class, or in a separate class
// This example demonstrates it with one class.
public class NetworkTestClass implements INetworkHandler {

    // The network object that we will use later to send/receive packets
    public Network network;

    // This will represent the 'user' that our bot is logged in as.
    public User user;

    public NetworkTestClass(){

        // Prepare a Handshake packet for the server. The purpose of this is to tell the server
        // what version we're running and what our username is.
        Packet00Handshake handshake = new Packet00Handshake(Packet.Initiator.CLIENT, "TestBot");
        handshake.setVersion(KronosKonverseAPI.API_VERSION);

        try {
            // Initialize the Network object with the server address and port, and of course
            // the handshake packet.
            network = new Network("localhost", 9090, handshake);

            // Adds an INetworkHandler for this connection. If you're using a separate class
            // then pass that class in (such as with 'new YourClassThatImplementsINetworkHandler()')
            // make sure the network object however is accessible from that class.
            network.addNetworkHandler(this);

            // Start the connection to the server. Actually, more like start receiving Packets from the server.
            network.connect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String... args){
        // This goes from being a 'static' instance, to a normal object. Kind of hard to explain. ¯\_(ツ)_/¯
        new NetworkTestClass();
    }


    @Override
    public void onPacketReceived(Packet packet, String response) {
        // We need this to 'decode' the Packets.
        Gson gson = new Gson();
        // Switch statements are like condensed if statements. (They don't work with Strings however)
        System.out.println(packet.getId());
        switch(packet.getId()){
            case 1:
                // Convert the Packet into a LoggedIn packet since we've verified that it is one.
                Packet01LoggedIn loggedIn = gson.fromJson(response, Packet01LoggedIn.class);

                // Assigns our bot's 'user' to the one from the server.
                this.user = loggedIn.getUser();
                // YOU NEED TO DO THIS AT THE END OF EVERY 'case'!
                break;
            case 2:
                Packet02ChatMessage chatMessage = gson.fromJson(response, Packet02ChatMessage.class);

                // Okay, we've got the chat packet from the server. Lets read the message and make a decision
                // on how to respond / do with it.



                if(chatMessage.getChat().getMessage().startsWith("!")){
                    // Since most of the time, we're going to send back a message, lets just create that chat packet now.

                    ChatMessage messageToSend = new ChatMessage();
                    messageToSend.setUser(this.user);

                    // We really could just construct a chat packet EVERY if statement, but for organization...
                    boolean sending = false;

                    // When checking for the chat message, be sure to use packet.getChat().getMessage() NOT
                    // packet.getMessage(), I keep making that mistake and will be sure to fix it soon. (So both works)
                    if(chatMessage.getChat().getMessage().equalsIgnoreCase("!hi")){
                        sending = true;
                        messageToSend.setMessage(String.format("Well hello there %s!", chatMessage.getChat().getUser().getUsername()));
                    }else if(chatMessage.getChat().getMessage().equalsIgnoreCase("!version")){
                        sending = true;
                        messageToSend.setMessage(String.format("%s, I am utilizing API Version %s", chatMessage.getChat().getUser().getUsername(), KronosKonverseAPI.API_VERSION.getReadable()));
                    } // You can daisy chain more else-if statements here. We'll do one last example here.
                    else if(chatMessage.getChat().getMessage().startsWith("!panic")){
                        sending = true;
                        String extracted = chatMessage.getChat().getMessage().replace("!panic ", "");
                        messageToSend.setMessage("panics " + extracted);
                        messageToSend.setAction(true); // This is the equivalent of doing '/me does something'
                    }

                    if(sending){
                        // Construct the chat packet
                        Packet02ChatMessage chatPacket = new Packet02ChatMessage(Packet.Initiator.CLIENT, messageToSend);

                        // Send the Packet to the server, this could throw a network error, so we try/catch it.
                        try {
                            network.sendPacket(chatPacket);
                        } catch (IOException e) {
                            // Uh-oh, we encountered an error. Lets handle it.
                            e.printStackTrace();
                            System.out.println("A network error occurred.");
                        }
                    }

                }
                break;
            // We need to handle one more thing...
            case 4:
                // Oh no! We've been told to disconnect from the server! (And we have...), lets honor it while
                // we still have a bit of our dignity intact.
                Packet04Disconnect disconnect = gson.fromJson(response, Packet04Disconnect.class);

                try {
                    // The Network API mainly takes care of this for you.
                    network.disconnect();
                } catch (IOException e) {
                    // Oddly enough, we can encounter a network error trying to disconnect. We'll catch it anyway.
                    e.printStackTrace();
                }

                System.out.println("I've been kicked from the server: " + disconnect.getMessage());
                break;
            default:
                // We don't know how to handle the Packet. Do something here.
                System.out.println("Unexpected Packet received from server!" + packet);
                break;

        }
    }
}
