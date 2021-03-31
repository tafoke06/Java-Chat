import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.net.*;

public class LetsChat extends JFrame {
   //attributes
   private JTextField txtIp;
   //private JTextField txtPort; //add in later
   private JTextField jtfMessage;
   private JButton jbConnect;
   private JButton jbExit;
   private JButton jbSend;
   private JTextArea jta;
   private String IpAddress;   
   private BufferedReader br;
	private PrintWriter opw;
   private Socket cs;
   
   public static void main(String[] args) {
      new LetsChat();
   } //end of main
   
   public LetsChat() {
      //chat GUI

      //type in port number and ipaddress to get into server
      JPanel logIn = new JPanel(new GridLayout(1, 0));
      //JLabel port = new JLabel("Port:");
      //txtPort = new JTextField("16789");
      JLabel ip = new JLabel("IP Address:");
      txtIp = new JTextField();
      IpAddress = txtIp.getText();
      
      jbConnect = new JButton("Connect");
      jbConnect.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent ae) {
                     connect();
                     ThreadClient thc = new ThreadClient();
                     thc.start(); //starts thread
                  }
      });      
      
      jbExit = new JButton("Exit");
      jbExit.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent ae) {
                     logout();
                     System.exit(0); //closes client
                  }
      });
      
      //logIn.add(port); //add later
      //logIn.add(txtPort); //add later
      logIn.add(ip);
      logIn.add(txtIp);
      logIn.add(jbConnect);
      logIn.add(jbExit);
      add(logIn, BorderLayout.NORTH);      
            
      //chat log where messages should end up
      JPanel log = new JPanel();
      jta = new JTextArea(30, 30);
      jta.setEditable(false);
      log.add(jta, BorderLayout.CENTER);
      add(log);
      
      //type in message and send to server
      JPanel chat = new JPanel(new GridLayout(1, 2));
      
      jtfMessage = new JTextField(20);
      jtfMessage.requestFocusInWindow();
      chat.add(jtfMessage);
      
      jbSend = new JButton("Send");
      jbSend.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent ae) {
                     sendMessage(jtfMessage.getText());     //sends the text to the server to send back to client and other clients
                  }
      });
      
      chat.add(jbSend);
      
      add(chat, BorderLayout.SOUTH);
      
      setTitle("Chat Menu");
      pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);      
		setLocationRelativeTo(null);
      setVisible(true);
   } //end of LetsChat()
      
   /**
    * Client socket connects to server socket
    */
   //client socket connects to server
   public void connect() {
      try {
         cs = new Socket(IpAddress, 16789); //String IpAddress = txtIp.getText() doesn't work for some reason
      	opw = new PrintWriter(
   				   new OutputStreamWriter(
   						cs.getOutputStream()));
         jbConnect.setEnabled(false);
      }
   	catch(UnknownHostException uhe) {
   		System.out.println("no host");
   		//uhe.printStackTrace();
   	}
      catch(ConnectException ce) {
         jta.append("The server is not available at the moment.\n");
      }
      catch(NullPointerException npe) {
         jta.append("Check if the IpAddress used is correct.\n");
         jta.append(IpAddress + "\n"); //debug
      }
   	catch(IOException ioe) {
   		System.out.println("IO error 1 in connect method");
   		//ioe.printStackTrace();
   	}
   }
      
   /**
    * Sends message to server
    * @param clientMsg Message that the client want to send
    */
   public void sendMessage(String clientMsg) { 
      //logs you out of the server     
      if(clientMsg.equals("LOGOUT")) {
         opw.println("Disconnected");
         opw.flush();
         logout();
      }
      else if(cs == null) {
         jta.append("Connect to a server first.\n");
      }
      else {
         opw.println(clientMsg);
         opw.flush();
      }

      jtfMessage.setText("");      
   } //end of sendMessage
      
   /**
    * Closes PrintWriter, BufferedReader, and Socket
    */
   public void logout() {
      try {
         opw.println("Disconnected");
         opw.flush();
         jta.append("You are now disconnected\n");               
         opw.close();
         br.close();
         cs.close();
         jbConnect.setEnabled(true);
      }
      catch(NullPointerException npe) {}
      catch(IOException ioe) {
   		System.out.println("IO error 2 in logout method");
  			//ioe.printStackTrace();
  		}   
   }
      
   class ThreadClient extends Thread {
      String msg;
      /**
       * Listens to the server for a message
       */
      public void run() {
         try {
            br = new BufferedReader(
     	   	      new InputStreamReader( 
      		  	    cs.getInputStream()));      
               
            while((msg = br.readLine()) != null) {
               jta.append(msg + "\n");
            }
   
         }
         catch(ConnectException ce) {
            jta.append("The server is down. Please try again later.\n");
         }
         catch(NullPointerException npe) {}
         catch(IOException ioe) {
      		System.out.println("IO error 3 in inner class");
      	   //ioe.printStackTrace();
      	}
      }   
   } //end of ThreadClient class
} //end of LetsChat class