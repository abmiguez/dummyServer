package es.uvigo.ei.sing.dummyserver;

import static es.uvigo.ei.sing.dummyserver.Constants.REQUEST_SECRET_KEY;
import static es.uvigo.ei.sing.dummyserver.Constants.REST_API_KEY;
import static java.util.concurrent.CompletableFuture.runAsync;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.management.OperatingSystemMXBean;

@SuppressWarnings("restriction")
public class ServerThread extends Thread {
    private Socket socket = null;
    private Socket requestSocket;

    public ServerThread(Socket socket) {
      super("ServerThread");
      this.socket = socket;
    }

    public void run() {
      try {
    	  final OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
    	  final ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
          out.flush();
          final ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
          final String clientRequest = (String)in.readObject();
          final JSONObject json = new JSONObject(clientRequest);
          System.out.println("client> "+clientRequest);
          String message = "";
          if(json.getString("becalm_key").equals(REQUEST_SECRET_KEY)){
	          switch(json.getString("method")){
		  		case "getState": 
		  			String state = "Overloaded";
	    		    if(operatingSystemMXBean.getSystemCpuLoad() < 0.7){
	    		    	state = "Running";
	    		    }	    		    
	    		    message = "{\"status\": 200, \"success\": true, \"becalm_key\":\""+REST_API_KEY+"\", \"data\": {"+
		  					"\"state\":\""+state+"\", \"version\":\"4.4.3\", \"version_changes\":\"Description of changes\", "+
		  					"\"max_analizable_documents\":\"515\"} }";
		  			out.writeObject(message);
			        out.flush();
		  			break;
				case "getAnnotations":
					message = "{\"status\": 200, \"success\": true, \"becalm_key\":\""+REST_API_KEY+"\", \"data\": {} }";
					out.writeObject(message);
			        out.flush();
			        Thread.sleep(2000);
			        annottatePatents().exceptionally(err -> {
						System.out.println("Error while getting annotations: " + err);
						return null;
					});
					break;
				default:
					message = "{\"status\": 500, \"success\": false, \"becalm_key\":\""+REST_API_KEY+"\", \"data\": {} }";
					out.writeObject(message);
			        out.flush();
					break;
	          }
          }else{
        	  message = "{\"status\": 401, \"success\": false, \"becalm_key\":\""+REST_API_KEY+"\", \"data\": {} }";
        	  out.writeObject(message);
		      out.flush();
          }
          System.out.println("server> "+message);
     } catch (Exception e) {
      e.printStackTrace();
     }
   }
    
    private CompletableFuture<Void> annottatePatents() {
		return runAsync(() -> {    
			final String annotations = "[ {\"document_id\": \"CA2073855C\", \"section\": \"T\", \"init\": 0, "+
									   "\"end\": 14, \"score\": 0.856016, \"annotated_text\": \"Glycoalkaloids\","+
					                   " \"type\": \"unknown\",  \"database_id\": \"ED5266\" } ]";
			ObjectInputStream in = null;
			ObjectOutputStream out = null;
			try {
				requestSocket = new Socket("localhost", 8089);
				System.out.println("server> Connected to localhost in port 8089");
				out = new ObjectOutputStream(requestSocket.getOutputStream());
	            out.flush();
	            requestSocket.setSoTimeout(5000);
	            in = new ObjectInputStream(requestSocket.getInputStream());
	            
	            System.out.println("server> " + annotations);
	            
			    out.writeObject(annotations);
	            out.flush();    
	            int tries = 0;
	            boolean exit = false;
	            do{
	            	exit = resendAnnotations(in, tries+1);
	            	tries++;
	            }while(tries<5 && !exit);
	            if(!exit){
	            	System.out.println("sever> Do not receive response from the requester");
	            	//TODO send email via stmp
	            }
			} catch (IOException e) {
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
    
    private boolean resendAnnotations(ObjectInputStream in, int tries){
    	try{
    		System.out.println("server> Send annotations, try: "+ tries);
        	String ok = (String)in.readObject();
        	final JSONObject json = new JSONObject(ok);
        	if(json.getInt("status") == 200){
        		System.out.println("client> "+ok);
        		return true;
        	}else{
        		return false;
        	}
        }catch(ClassNotFoundException | IOException | JSONException e){
        	return false;
        }
    }
}
