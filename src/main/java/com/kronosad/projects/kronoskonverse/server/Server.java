package com.kronosad.projects.kronoskonverse.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 5:30 PM
 */
public class Server {
    private int port;

    private ServerSocket server;



    public Server(int port){
        this.port = port;

        System.out.println("Opening server on port: " + port);

        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("There was a problem binding to port: " + port);
        }finally {
            System.out.println("Sucessfully bounded to port!");
            serve();
        }
    }

    public void serve(){
        while (true){
            try {
                new ConnectionHandler(this, server.accept());
            } catch (IOException e) {
                System.err.println("Error accepting connection!");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        new Server(Integer.valueOf(args[0]));
    }

}
