package com.kronosad.konverse.client.window;

import com.kronosad.konverse.client.interfaces.IMessageReceptor;
import com.kronosad.konverse.common.objects.ChatMessage;
import com.kronosad.konverse.common.objects.PrivateMessage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Russell on 5/24/2014.
 */
public class ChatWindow implements Initializable, IMessageReceptor {
    @FXML private TextArea txtAreaMessages;
    @FXML private TextField txtToSend;

    @FXML private Button btnSend;

    @FXML private ListView<String> userList;

    @Override
    public void handleMessage(ChatMessage message) {
        txtAreaMessages.appendText("[" + message.getUser().getUsername() + "] " + message.getMessage() + "\n");
    }

    @Override
    public void handlePrivateMessage(PrivateMessage message) {
        // TODO: Handle private messages :P
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
