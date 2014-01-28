/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.kronosad.projects.kronoskonverse.client.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kronosad.projects.kronoskonverse.common.KronosKonverseAPI;
import com.kronosad.projects.kronoskonverse.common.objects.ChatMessage;
import com.kronosad.projects.kronoskonverse.common.objects.PrivateMessage;
import com.kronosad.projects.kronoskonverse.common.objects.Room;
import com.kronosad.projects.kronoskonverse.common.objects.Version;
import com.kronosad.projects.kronoskonverse.common.packets.*;
import com.kronosad.projects.kronoskonverse.common.user.NetworkUser;
import com.kronosad.projects.kronoskonverse.common.user.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author russjr08
 */
public class WindowChat extends javax.swing.JFrame implements Runnable{
    private Socket connection;
    private String name;

    private Version version = KronosKonverseAPI.API_VERSION;

    private DefaultListModel usersList = new DefaultListModel();

    private Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

    private NetworkUser user;

    private Thread receive;

    private Room currentRoom;

    boolean disconnected = false;
    boolean firstLogin = true;


    private ArrayList<User> loggedInUsers = new ArrayList<User>();

    /**
     * Creates new form WindowChat
     */
    public WindowChat(Socket socket, String name) {
        initComponents();
        this.name = name;
        this.connection = socket;

        this.setVisible(true);

        handshake();

        receive = new Thread(this);
        receive.start();

        txtMessage.requestFocusInWindow();

        txtMessage.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {}

            @Override
            public void keyPressed(KeyEvent keyEvent) {}

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER){
                    sendCurrentText();
                }
            }
        });

        listUsers.setModel(usersList);
        txtSentMessagesMain.setLineWrap(true);
        txtSentMessagesMain.setEditable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.txtMessage.selectAll();


        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) {}

            @Override
            public void windowClosing(WindowEvent windowEvent) {}

            @Override
            public void windowClosed(WindowEvent windowEvent) {
                if(!WindowChat.this.disconnected){
                    System.exit(0);
                }

            }

            @Override
            public void windowIconified(WindowEvent windowEvent) {}

            @Override
            public void windowDeiconified(WindowEvent windowEvent) {}

            @Override
            public void windowActivated(WindowEvent windowEvent) {}

            @Override
            public void windowDeactivated(WindowEvent windowEvent) {}
        });

    }

    private void handshake(){
        Packet00Handshake handshake = new Packet00Handshake(Packet.Initiator.CLIENT, name);
        handshake.setVersion(version);

        PrintWriter out;
        try {
            out = new PrintWriter(connection.getOutputStream(), true);
            out.println(new Gson().toJson(handshake));

        } catch (IOException e) {
            addToChat("Error sending handshake!");
            addToChat(e.getMessage());
            e.printStackTrace();
        }

    }

    public void addToChat(String text){
        txtSentMessagesMain.append(text + "\n");
        txtSentMessagesMain.setCaretPosition(txtSentMessagesMain.getText().length());
    }

    public Component getTab(String tabName){
        for(int i = 0; i < roomsTabbedPane.getTabCount(); i++){
            if(roomsTabbedPane.getTitleAt(i).equals(tabName)){
                return roomsTabbedPane.getComponentAt(i);
            }
        }
        return null;
    }

    public void addTextToTab(String tabName, String text){
        for(int i = 0; i < roomsTabbedPane.getTabCount(); i++){
            if(roomsTabbedPane.getTitleAt(i).equals(tabName)){
                if(roomsTabbedPane.getComponentAt(i) instanceof JTextArea){
                    JTextArea textArea = (JTextArea) roomsTabbedPane.getComponentAt(i);
                    textArea.append(text + "\n");
                    textArea.setCaretPosition(textArea.getText().length());
                }
            }
        }
    }
    public JTextArea fabricateNewTextArea(){
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setCaretPosition(0);

        return area;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        listUsers = new javax.swing.JList();
        btnSend = new javax.swing.JButton();
        txtMessage = new javax.swing.JTextField();
        roomsTabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtSentMessagesMain = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane2.setViewportView(listUsers);

        btnSend.setText("Send!");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        txtMessage.setText("Type your thoughts here!");
        txtMessage.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        txtSentMessagesMain.setColumns(20);
        txtSentMessagesMain.setRows(5);
        jScrollPane1.setViewportView(txtSentMessagesMain);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
        );

        roomsTabbedPane.addTab("Main", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roomsTabbedPane))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnSend, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addComponent(roomsTabbedPane))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSend))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        sendCurrentText();

    }//GEN-LAST:event_btnSendActionPerformed

    public void updateUsers(){
        usersList.clear();
        usersList.removeAllElements();
        for(User logged : loggedInUsers){
            usersList.addElement(logged.getUsername());
        }
    }

    public void sendCurrentText(){
        if(disconnected) return;

        try {
            PrintWriter writer = new PrintWriter(connection.getOutputStream(), true);
            if(txtMessage.getText().startsWith("/tell")){
                PrivateMessage chat = new PrivateMessage();
                String[] pieces = txtMessage.getText().split(" ");
                if(pieces.length >= 3){
                    String username = pieces[1];
                    User recipient = new User();

                    try {
                        Field userField = recipient.getClass().getDeclaredField("username");
                        userField.setAccessible(true);
                        userField.set(recipient, username);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                        return;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return;
                    }
                    chat.setRecipient(recipient);

                    String message = txtMessage.getText().replace("/tell", "").replace(username, "");
                    chat.setMessage(message);


                }
                chat.setUser(this.user);
                chat.setMessage(txtMessage.getText().split(" ")[2]);
                chat.setRoom(currentRoom);

                Packet02ChatMessage packet = new Packet02ChatMessage(Packet.Initiator.CLIENT, chat);
                packet.setPrivate(true);
                writer.println(packet.toJSON());
            }else{
                ChatMessage chat = new ChatMessage();
                chat.setUser(user);
                chat.setMessage(txtMessage.getText());
                chat.setRoom(currentRoom);
                if(chat.getMessage().startsWith("/me")){
                    chat.setMessage(chat.getMessage().replace("/me", ""));
                    chat.setAction(true);
                }
                Packet02ChatMessage packet = new Packet02ChatMessage(Packet.Initiator.CLIENT, chat);

                writer.println(packet.toJSON());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        txtMessage.requestFocusInWindow();
        txtMessage.setText("");

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSend;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList listUsers;
    protected javax.swing.JTabbedPane roomsTabbedPane;
    private javax.swing.JTextField txtMessage;
    private javax.swing.JTextArea txtSentMessagesMain;
    // End of variables declaration//GEN-END:variables

    @Override
    public void run() {
        while (!disconnected){
            BufferedReader reader;
            try {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = reader.readLine();
                if(response == null){
                    addToChat("[ERROR: Disconnected!]");
                    btnSend.setEnabled(false);
                    connection.close();
                    disconnected = true;
                    break;
                }
                Packet packet = new Gson().fromJson(response, Packet.class);

                if(packet.getId() == 01){
                    Packet01LoggedIn loggedIn = new Gson().fromJson(response, Packet01LoggedIn.class);
                    System.out.println("Received Logged In packet! Parsing now...");
                    user = loggedIn.getUser();

                    if(firstLogin){
                        addToChat("[Logged in successfully! UUID: " + user.getUuid() + "]");
                        firstLogin = !firstLogin;
                    }

                    currentRoom = loggedIn.getRoom();
                    loggedInUsers = currentRoom.getUsers();
                    if(!currentRoom.getName().equals("Main")){
                        this.roomsTabbedPane.addTab(currentRoom.getName(), fabricateNewTextArea());
                        this.roomsTabbedPane.setSelectedComponent(getTab(currentRoom.getName()));
                    }
                    updateUsers();

                    this.setTitle("Topic: " + " Logged In As: " + user.getUsername());


                }
                if(packet.getId() == 02){
                    Packet02ChatMessage chatMessage = new Gson().fromJson(response, Packet02ChatMessage.class);


                    if(chatMessage.getChat().getMessage().contains(name) || chatMessage.isPrivate()){
                        NotificationHelper.mentioned(chatMessage);
                    }

                    if(chatMessage.isPrivate()){
                        PrivateMessage privateMsg = chatMessage.getPrivateMessage();
                        addToChat(String.format("[%s -> %s] %s", privateMsg.getUser().getUsername(), privateMsg.getRecipient().getUsername(), privateMsg.getMessage()));
                    }else{
                        if(chatMessage.getChat().getRoom() != null){
                            if(chatMessage.getChat().getRoom().getName().equals("Main")){
                                if(!chatMessage.getChat().isAction())
                                    addToChat("[" + chatMessage.getChat().getUser().getUsername() + "] " + chatMessage.getChat().getMessage());
                                else
                                    addToChat("* " + chatMessage.getChat().getUser().getUsername() + " " + chatMessage.getChat().getMessage());
                            }else{
                                if(!chatMessage.getChat().isAction())
                                    addTextToTab(chatMessage.getChat().getRoom().getName(), "[" + chatMessage.getChat().getUser().getUsername() + "] " + chatMessage.getChat().getMessage());
                                else
                                    addTextToTab(chatMessage.getChat().getRoom().getName(), "* " + chatMessage.getChat().getUser().getUsername() + " " + chatMessage.getChat().getMessage());
                            }
                        }else{
                            if(!chatMessage.getChat().isAction())
                                addToChat("[" + chatMessage.getChat().getUser().getUsername() + "] " + chatMessage.getChat().getMessage());
                            else
                                addToChat("* " + chatMessage.getChat().getUser().getUsername() + " " + chatMessage.getChat().getMessage());
                        }
                    }

                }

                if(packet.getId() == 03){
                    System.out.println(response);
                    Packet03UserListChange change = new Gson().fromJson(response, Packet03UserListChange.class);

                    loggedInUsers = change.getOnlineUsers();



                    updateUsers();
                }

                if(packet.getId() == 04){
                    System.out.println(response);
                    Packet04Disconnect disconnect = new Gson().fromJson(response, Packet04Disconnect.class);

                    if(disconnect.isKick()){
                        System.out.println("Kicked from Server!");
                        addToChat("[Error: You were kicked from the server: " + disconnect.getMessage());
                        connection.close();
                        disconnected = true;
                    }else{
                        System.out.println("Disconnect Packet Received!");
                        addToChat("[Error: You were disconnected from the server: " + disconnect.getMessage());
                    }
                    disconnected = true;

                }

            } catch (IOException e) {
                e.printStackTrace();
                addToChat("Connection error!");
                addToChat(e.getMessage());
                disconnected = true;
            }
        }
        new LoginWindow().setVisible(true);
    }
}
