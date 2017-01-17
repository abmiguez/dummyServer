package es.uvigo.ei.sing.dummyserver;

import static es.uvigo.ei.sing.dummyserver.Constants.SERVERPORT;
import static es.uvigo.ei.sing.dummyserver.Constants.REQUESTERPORT;
import static es.uvigo.ei.sing.dummyserver.Constants.REQUEST_SECRET_KEY;
import static java.util.concurrent.CompletableFuture.runAsync;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import junit.framework.TestCase;

public class Requester extends TestCase {
	private ServerSocket serverSocket;
	private Socket requestSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
    public void testRequest(){    	
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
	        
	        System.out.println("server> Server stoped");
        
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
        assertTrue(true);
    }
    
    private CompletableFuture<Void> request() throws IOException {
    	return runAsync(() -> { 
    		try{    		
        		requestSocket = new Socket("localhost", SERVERPORT);
                System.out.println("client> Connected to localhost in port " + SERVERPORT);
                
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(requestSocket.getInputStream());
                
                final Random random = new Random();
                String method = "getState";
    		    if(random.nextBoolean())
    		    	method = "getAnnotations";
    		    final String petition = "{\"name\":\"BeCalm\", \"method\":\""+method+"\", \"becalm_key\":\""+REQUEST_SECRET_KEY+"\","+
    		                            "\"custom_parameters\" :{\"example\":true}, \"parameters\" : {} }";
    		    out.writeObject(petition);
                out.flush();
                
                System.out.println("client> " + petition);
                
                System.out.println("server> " + (String)in.readObject());
                
            }catch(UnknownHostException unknownHost){
                System.err.println("client> You are trying to connect to an unknown host!");
            }catch(IOException ioException){
                ioException.printStackTrace();
            }catch (ClassNotFoundException e) {
    			e.printStackTrace();
    		}finally{
                try{
                    in.close();
                    out.close();
                    requestSocket.close();
                }
                catch(IOException ioException){
                    ioException.printStackTrace();
                }
            }
    	});
    }
    private class ClientThread extends Thread{
    	private Socket socket = null;

        public ClientThread(Socket socket) {
          super("ClientThread");
          this.socket = socket;
        }

        public void run() {
          try {
        	  final ObjectOutputStream out = new ObjectOutputStream(this.socket.getOutputStream());
              out.flush();
              final ObjectInputStream in = new ObjectInputStream(this.socket.getInputStream());
              System.out.println("server> "+(String)in.readObject());
              final String ok = "{\"status\": 200, \"success\": true, \"becalm_key\":\""+REQUEST_SECRET_KEY+"\", \"data\": {} }";
              out.writeObject(ok);
		      out.flush();
		      System.out.println("client> "+ok);
              
         } catch (Exception e) {
          e.printStackTrace();
         }
       }
    }
}
