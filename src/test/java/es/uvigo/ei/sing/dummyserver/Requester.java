package es.uvigo.ei.sing.dummyserver;

import junit.framework.TestCase;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import static es.uvigo.ei.sing.dummyserver.Constants.*;
import static java.util.concurrent.CompletableFuture.runAsync;

public class Requester extends TestCase {
    private ServerSocket serverSocket;
    private Socket requestSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void testRequest() {
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(REQUESTERPORT);
            System.out.println("client> Server started at port " + REQUESTERPORT);
            request().exceptionally(err -> {
                System.out.println("Error while init socket: " + err);
                return null;
            });

            while (listening)
                new ClientThread(serverSocket.accept()).start();

            System.out.println("server> Server stopped");

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue(true);
    }

    private CompletableFuture<Void> request() {
        return runAsync(() -> {
            try {
                requestSocket = new Socket("localhost", SERVERPORT);
                System.out.println("client> Connected to localhost in port " + SERVERPORT);

                out = new PrintWriter(new OutputStreamWriter(requestSocket.getOutputStream(), StandardCharsets.UTF_8));
                out.flush();
                in = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));

                // Use a random value to request one of the two methods
                final Random random = new Random();
                String method = "getState";
                if (random.nextBoolean())
                    method = "getAnnotations";
                final String petition = "{\"name\":\"BeCalm\", \"method\":\"" + method + "\", \"becalm_key\":\"" + REQUEST_SECRET_KEY + "\"," +
                        "\"custom_parameters\" :{\"example\":true}, \"parameters\" : {} }";
                Functions.writeHeadersAndMessage(out, petition);

                System.out.println("client> " + petition);

                System.out.println("server> " + Functions.parseRequest(in));
            } catch (UnknownHostException unknownHost) {
                System.err.println("client> You are trying to connect to an unknown host!");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                    requestSocket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    private class ClientThread extends Thread {
        private Socket socket = null;

        public ClientThread(Socket socket) {
            super("ClientThread");
            this.socket = socket;
        }

        public void run() {
            try {
                final PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                out.flush();
                final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("server> " + Functions.parseRequest(in));
                final String ok = "{\"status\": 200, \"success\": true, \"becalm_key\":\"" + REQUEST_SECRET_KEY + "\", \"data\": {} }";
                Functions.writeHeadersAndMessage(out, ok);
                System.out.println("client> " + ok);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
