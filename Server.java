import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.net.*;

public class Server extends Thread {
   //attributes
   private ArrayList<Socket> clients;
   //private Vector<ThreadServer> clients = new Vector<ThreadServer>();
   private BufferedReader br;
	private PrintWriter opw;
   private Socket cs;
   private ServerSocket ss;
   
   public static void main(String[] args) {
      new Server(); 
   } //end of main
   
   public Server() {
      //ServerSocket
		ServerSocket ss = null;

		try {
		  System.out.println("getLocalHost: "+InetAddress.getLocalHost() );
		  System.out.println("getByName:    "+InetAddress.getByName("localhost") ); //ipAddress

		  ss = new ServerSocket(16789); //port number
		  cs = null;
        clients = new ArrayList<Socket>();
        
		  while(true) { 		// run forever once up
			//try{
			  cs = ss.accept(); 				// wait for connection
           clients.add(cs);            //adds client socket to arraylist
           System.out.println("A client has connected to the server");
           ThreadServer ths = new ThreadServer(cs);
           Thread t1 = new Thread(ths);
		     t1.start();
        } // end while
      }
      catch( BindException be ) {
			System.out.println("Server already running on this computer, stopping.");
		}
		catch( IOException ioe ) {
			System.out.println("IO Error");
			//ioe.printStackTrace();
		}      
   } //end of LetsChat()

  class ThreadServer extends Thread {
      Socket cs;
      
      /**
       * Initialize socket
       * @param cs client socket
       */
		public ThreadServer( Socket cs ) {
			this.cs = cs;
		} //end of ThreadServer()
		
      /**
       * Listens for messages from clients and remove clients that closed their connection to the server socket
       */
		public void run() {
			BufferedReader br;
			PrintWriter opw;
			String clientMsg;
			
         try {
           br = new BufferedReader(
						new InputStreamReader( 
							cs.getInputStream()));
         
			  opw = new PrintWriter(
						new OutputStreamWriter(
							cs.getOutputStream()));
           
           opw.println("You are now connected to the server");
           opw.flush();          
           
              while((clientMsg = br.readLine()) != null) {
                 if(clientMsg.equals("Disconnected")) {
                    for (int i = 0; i < clients.size(); i++) {
                       Socket client = clients.get(i);
                       
                       if (client == cs) {
                          clients.remove(i); //removes client from arraylist
                          cs.close();
                       }
                    }
                 }
                 else {    
                    broadcastToAll(clientMsg);
                    //System.out.println(clients); //debug to check if client was removed from arraylist
                 }
              }
			} //end of try
         catch(SocketException se) {
            System.out.println("A client has disconnected from the server.");
            //cs.close();
         }
			catch( IOException e ) { 
				System.out.println("Inside run catch"); 
				//e.printStackTrace();
			}
      } //end of run
      
      /**
       * Sends message to clients connected
       * @param clientMsg Message that the server want to send
       */
      public void broadcastToAll(String clientMsg) {
			PrintWriter opw;
			
         try {  
           System.out.println("Client Message: " + clientMsg);
              for(Socket client : clients) { //cycles through arraylist to send each client a message
                 opw = new PrintWriter(
   						new OutputStreamWriter(
   							client.getOutputStream()));       
                 opw.println(clientMsg);
                 opw.flush();   
              }     
         }
         catch( IOException e ) { 
				System.out.println("Inside broadcastToAll catch"); 
				//e.printStackTrace();
			} 
      } //end of broadcastToAll
         
   } //end of ThreadServer class
} //end of Server class
