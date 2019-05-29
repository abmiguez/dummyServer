package es.uvigo.ei.sing.dummyserver;

import java.io.IOException;
import java.net.ServerSocket;

import static es.uvigo.ei.sing.dummyserver.Constants.SERVERPORT;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;

        try {
            // Start the server and listen the specified port
            serverSocket = new ServerSocket(SERVERPORT);
        } catch (IOException e) {
            System.err.println("server> Could not listen on port: " + SERVERPORT + ".");
            System.exit(-1);
        }

        System.out.println("server> Server started");
        // Keep listening until a request is received
        while (listening)
            new ServerThread(serverSocket.accept()).start();
        System.out.println("server> Server stopped");

        serverSocket.close();
    }
}