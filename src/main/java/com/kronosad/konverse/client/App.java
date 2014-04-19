package com.kronosad.konverse.client;

import com.kronosad.konverse.common.networking.Network;
import com.kronosad.konverse.common.packets.Packet00Handshake;
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
public class App extends Application {

    private Network network;

    private Stage stage;

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
    }

    public static void main(String... args) {
        launch(args);
    }

    public static App getInstance() {
        return instance;
    }

    public void setServer(Packet00Handshake handshake, String address, int port) throws IOException {
        network = new Network(address, port, handshake);

    }


}
