package es.uvigo.ei.sing.dummyserver;
import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(8088);
        } catch (IOException e) {
            System.err.println("server> Could not listen on port: 8088.");
            System.exit(-1);
        }
        System.out.println("server> Server started");
        while (listening)
          new ServerThread(serverSocket.accept()).start();
        
        System.out.println("server> Server stoped");
        
        serverSocket.close();
    }
}