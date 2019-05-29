package es.uvigo.ei.sing.dummyserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Functions {
    public static String parseRequest(BufferedReader in) {
        StringBuilder requestJson = new StringBuilder();

        try {
            // Read the headers
            String headerLine;
            while (!(headerLine = in.readLine()).isEmpty()) {
                // TODO: Do something with the headers...
            }

            // Read the body (JSON)
            while (in.ready()) {
                // TODO: Do some content validations...
                requestJson.append((char) in.read());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return requestJson.toString();
    }

    public static void writeHeadersAndMessage(PrintWriter out, String message) {
        // TODO: Add the desired headers...
        out.println("POST  HTTP/1.1");
        out.println("Content-Length:" + message.length());
        out.println("Content-Type: application/json; charset=utf-8");
        out.println("");
        out.println(message);
        out.flush();
    }
}
