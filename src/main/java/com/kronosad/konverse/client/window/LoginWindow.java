package com.kronosad.konverse.client.window;

import com.kronosad.konverse.common.networking.Network;
import com.kronosad.konverse.common.packets.Packet;
import com.kronosad.konverse.common.packets.Packet00Handshake;
import com.kronosad.konverse.common.packets.Packet05ConnectionStatus;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
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
        lblStatus.setText("Connecting to server...");

        Packet00Handshake handshake = new Packet00Handshake(Packet.Initiator.CLIENT, txtUsername.getText());
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
                } else {

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
