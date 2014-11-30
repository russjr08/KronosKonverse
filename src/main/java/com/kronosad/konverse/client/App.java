package com.kronosad.konverse.client;

import com.google.gson.Gson;
import com.kronosad.konverse.client.interfaces.IMessageReceptor;
import com.kronosad.konverse.client.notification.Notification;
import com.kronosad.konverse.client.window.ChatWindow;
import com.kronosad.konverse.common.KonverseAPI;
import com.kronosad.konverse.common.interfaces.INetworkHandler;
import com.kronosad.konverse.common.networking.Network;
import com.kronosad.konverse.common.objects.ClientInfo;
import com.kronosad.konverse.common.packets.*;
import com.kronosad.konverse.common.user.AuthenticatedUser;
import javafx.application.Application;
import javafx.application.Platform;
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

    private Stage stage;

    private Network network;

    public static Gson gson = new Gson();

    public static Parameters params;

    private IMessageReceptor messageReceptor;

    private static App instance;

    private AuthenticatedUser user;

    public static final ClientInfo CLIENT_INFO = new ClientInfo("Konverse JavaFX Client", KonverseAPI.API_VERSION);

    @Override
    public void start(Stage stage) throws Exception {
        params = this.getParameters();
        Platform.setImplicitExit(true);

        instance = this;
        this.stage = stage;

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("jfx/LoginWindow/LoginWindow.fxml"));

        Scene scene = new Scene(root);

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
//        stage.setResizable(false);

        stage.setOnCloseRequest((windowEvent) -> {
            System.out.println("Someone is closing me! D:");

            if(network != null) {
                try {
                    network.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Hmm.. Apparently we had a problem disconnecting!?");
                }
            }

            Platform.exit();
        });

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

    public AuthenticatedUser getLocalUser() { return user; }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    @Override
    public void onPacketReceived(Packet packet, String response) {
        switch(packet.getId()){
            case 1:
                // TODO: Handle login packet here...
                Packet01LoggedIn loggedIn = gson.fromJson(response, Packet01LoggedIn.class);
                user = loggedIn.getUser();

                System.out.println("Received AuthenticatedUser from server: " + user.toString());

                Platform.runLater(() -> {
                    Parent chatWindow;
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("jfx/ChatWindow/ChatWindow.fxml"));

                        chatWindow = loader.load();
                        Scene chatScene = new Scene(chatWindow);
                        stage.setScene(chatScene);
                        stage.setTitle("You are live! (Konverse Version: " + KonverseAPI.API_VERSION.getReadable() + ")");
                        stage.show();
                        stage.setResizable(true);
                        messageReceptor = (ChatWindow)loader.getController();
                        messageReceptor.handleUserListChange(loggedIn.getLoggedInUsers());
                        Notification.Notifier.INSTANCE.notifySuccess("Logged In!", "You have successfully logged in.");

                        stage.setOnCloseRequest((windowEvent) -> {
                            System.out.println("Someone is closing me! D:");
                            Platform.exit();
                            System.exit(0);
                        });


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });


                break;
            case 2:
                Packet02ChatMessage chatPacket = gson.fromJson(response, Packet02ChatMessage.class);
                if(messageReceptor != null) {
                    if(chatPacket.isPrivate()) {
                        messageReceptor.handlePrivateMessage(chatPacket.getPrivateMessage());
                    }else {
                        messageReceptor.handleMessage(chatPacket.getChat());
                    }
                }
                break;
            case 3:
                Packet03UserListChange change = gson.fromJson(response, Packet03UserListChange.class);
                if(messageReceptor != null)
                    messageReceptor.handleUserListChange(change.getOnlineUsers()); // This should only happen ONCE.
                break;
            case 4:
                Packet04Disconnect disconnect = gson.fromJson(response, Packet04Disconnect.class);
                // TODO: A better way of doing this.
                if(messageReceptor instanceof ChatWindow) {
                    ChatWindow window = (ChatWindow)messageReceptor;
                    if(disconnect.getMessage() != null) {
                        window.appendText("You were kicked from the server: " + disconnect.getMessage() + "\n");
                    } else {
                        window.appendText("You were kicked form the server (No reason given).");
                    }
                }
        }
    }

    @Override
    public void onNetworkClosed(Throwable cause) {
        messageReceptor.handleNetworkClosed();
    }
}
