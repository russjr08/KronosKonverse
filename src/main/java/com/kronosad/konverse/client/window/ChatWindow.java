package com.kronosad.konverse.client.window;

import com.kronosad.konverse.client.App;
import com.kronosad.konverse.client.interfaces.IMessageReceptor;
import com.kronosad.konverse.common.objects.ChatMessage;
import com.kronosad.konverse.common.objects.PrivateMessage;
import com.kronosad.konverse.common.packets.Packet;
import com.kronosad.konverse.common.packets.Packet02ChatMessage;
import com.kronosad.konverse.common.user.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Russell on 5/24/2014.
 */
public class ChatWindow implements Initializable, IMessageReceptor {
    @FXML private TextArea txtAreaMessages;
    @FXML private TextField txtToSend;

    @FXML private Button btnSend;

    @FXML private ListView<String> userListView;
    private ObservableList<String> userList = FXCollections.observableArrayList();

    @Override
    public void handleMessage(ChatMessage message) {
        Platform.runLater(() -> txtAreaMessages.appendText("[" + message.getUser().getUsername() + "] " + message.getMessage() + "\n"));
    }

    @Override
    public void handlePrivateMessage(PrivateMessage message) {
        // TODO: Handle private messages :P
    }

    @Override
    public void handleUserListChange(List<User> users) {
        userList.clear();
        users.forEach((user) -> userList.add(user.getUsername()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        App.getInstance().setMessageReceptor(this);
        userListView.setItems(userList);
    }

    @FXML
    public void sendMessage(MouseEvent event) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(txtToSend.getText());
        chatMessage.setUser(App.getInstance().getLocalUser());

        Packet02ChatMessage chatPacket = new Packet02ChatMessage(Packet.Initiator.CLIENT, chatMessage);

        try {
            App.getInstance().getNetwork().sendPacket(chatPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
