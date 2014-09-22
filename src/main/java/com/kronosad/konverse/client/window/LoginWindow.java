package com.kronosad.konverse.client.window;

import com.kronosad.konverse.client.App;
import com.kronosad.konverse.common.KonverseAPI;
import com.kronosad.konverse.common.auth.Authentication;
import com.kronosad.konverse.common.auth.AuthenticationLoggedInMessage;
import com.kronosad.konverse.common.auth.exceptions.AuthenticationFailedException;
import com.kronosad.konverse.common.networking.Network;
import com.kronosad.konverse.common.packets.Packet;
import com.kronosad.konverse.common.packets.Packet00Handshake;
import com.kronosad.konverse.common.packets.Packet05ConnectionStatus;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Author russjr08
 * Created at 4/19/14
 */
public class LoginWindow implements Initializable {

    @FXML
    private TextField txtUsername, txtAddress, txtPort;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblStatus;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private Button btnConnect;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void connectClick(MouseEvent event) {
        System.out.println("Preparing to connect...");

        btnConnect.setDisable(true);

        progress.setDisable(false);
        lblStatus.setText("Authenticating...");

        // Attempt authentication.
        Authentication auth;

        if(App.params.getNamed().containsKey("auth-server")){
            String authServer = App.params.getNamed().get("auth-server");
            
            if(!authServer.endsWith("/")) {
                authServer += "/";
            }
            auth = new Authentication(authServer);
        } else {
            auth = new Authentication();
        }

        String authToken;
        AuthenticationLoggedInMessage msg;
        try {
            msg = auth.login(txtUsername.getText(), txtPassword.getText());
            System.out.println(msg);
            authToken = msg.getAuthToken();
        } catch (IOException e) {
            e.printStackTrace();
            lblStatus.setText("Failed to connect to Authentication Server...");
            btnConnect.setDisable(false);
            progress.setDisable(true);
            return;
        } catch (AuthenticationFailedException e) {
            e.printStackTrace();
            lblStatus.setText("Local Authentication Failed!");
            btnConnect.setDisable(false);
            progress.setDisable(true);
            return;
        }
        lblStatus.setText("Connecting to server...");

        Packet00Handshake handshake = new Packet00Handshake(Packet.Initiator.CLIENT, txtUsername.getText(), authToken, KonverseAPI.API_VERSION);
        Packet05ConnectionStatus status;

        try {
            lblStatus.setText("Sending handshake...");
            Network network = new Network(txtAddress.getText(), Integer.valueOf(txtPort.getText()), handshake);

            status = network.getConnectionStatus();

            if (status != null) {
                if (status.getStatus() == Packet05ConnectionStatus.NICK_IN_USE) {
                    lblStatus.setText("Nick name is already in use!");
                    btnConnect.setDisable(false);
                    progress.setDisable(true);
                } else if (status.getStatus() == Packet05ConnectionStatus.BANNED) {
                    lblStatus.setText("You are prohibited from connecting to this server.");
                    btnConnect.setDisable(false);
                    progress.setDisable(true);
                } else if (status.getStatus() == Packet05ConnectionStatus.VERSION_MISMATCH) {
                    lblStatus.setText("Client and Server's version does not match!");
                    btnConnect.setDisable(false);
                    progress.setDisable(true);
                } else if (status.getStatus() == Packet05ConnectionStatus.AUTHENTICATION_FAILED_SERVER_SIDE) {
                    lblStatus.setText("The server couldn't verify your identity!");
                    btnConnect.setDisable(false);
                    progress.setDisable(true);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            btnConnect.setDisable(false);
            progress.setDisable(true);
            lblStatus.setText("Connection failed!");
        }
    }


}
