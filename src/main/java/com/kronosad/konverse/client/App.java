package com.kronosad.konverse.client;

import com.google.gson.Gson;
import com.kronosad.konverse.client.interfaces.IMessageReceptor;
import com.kronosad.konverse.common.interfaces.INetworkHandler;
import com.kronosad.konverse.common.networking.Network;
import com.kronosad.konverse.common.packets.Packet;
import com.kronosad.konverse.common.packets.Packet00Handshake;
import com.kronosad.konverse.common.packets.Packet02ChatMessage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Author russjr08
 * Created at 4/19/14
 */
public class App extends Application implements INetworkHandler {

    private Network network;

    private Stage stage;

    public static Gson gson = new Gson();

    public static Parameters params;


    private IMessageReceptor messageReceptor;

    private static App instance;

    @Override
    public void start(Stage stage) throws Exception {
        instance = this;
        this.stage = stage;

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("jfx/LoginWindow/LoginWindow.fxml"));

        Scene scene = new Scene(root);

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();

        params = this.getParameters();

        if(App.params.getNamed().containsKey("auth-server")){
            System.out.println("Using custom Authentication Server: " + App.params.getNamed().get("auth-server"));
            System.err.println("Warning: Using a custom Authentication Server may authenticate you locally, but if the " +
                    "server is not using the same Authentication Server, then you will fail to connect.");

            if(!App.params.getNamed().get("auth-server").endsWith("/")){
                System.out.println("Hey! The Auth Server URL you provided didn't end with a trailing slash!" +
                        " I'll append it for you, but next time could you do that for me? :P");
            }
        }

    }

    public static void main(String... args) {
        launch(args);
    }

    public static App getInstance() {
        return instance;
    }

    public IMessageReceptor getMessageReceptor() {
        return messageReceptor;
    }

    public void setMessageReceptor(IMessageReceptor messageReceptor) {
        this.messageReceptor = messageReceptor;
    }

    public void setServer(Packet00Handshake handshake, String address, int port) throws IOException {
        network = new Network(address, port, handshake);

    }


    @Override
    public void onPacketReceived(Packet packet, String response) {
        switch(packet.getId()){
            case 01:
                // TODO: Handle login packet here...
                break;
            case 02:
                Packet02ChatMessage chatPacket = gson.fromJson(response, Packet02ChatMessage.class);
                messageReceptor.handleMessage(chatPacket.getChat());
        }
    }
}
