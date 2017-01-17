package es.uvigo.ei.sing.dummyserver;
import java.net.*;

import static es.uvigo.ei.sing.dummyserver.Constants.SERVERPORT;

import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(SERVERPORT);
        } catch (IOException e) {
            System.err.println("server> Could not listen on port: "+SERVERPORT+".");
            System.exit(-1);
        }
        System.out.println("server> Server started");
        while (listening)
          new ServerThread(serverSocket.accept()).start();
        
        System.out.println("server> Server stoped");
        
        serverSocket.close();
    }
}