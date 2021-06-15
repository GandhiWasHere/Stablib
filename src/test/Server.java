package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	// generic client handler
	public interface ClientHandler{
		public void startClient(InputStream in, OutputStream out);
		public void description();
	}
	

	volatile boolean stop;
	public Server() {
		stop=false;
	}
	
	// this fucntion intialize the server
	private void startServer(int port, ClientHandler ch) {
		//System.out.println("Server Side Intialize");
		try {
			ServerSocket server = new ServerSocket(port);
			Socket aClient = server.accept();
			//System.out.println("ClientConnevted");
			ch.startClient(aClient.getInputStream(),aClient.getOutputStream());
			server.close();
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// runs the server in its own thread
	public void start(int port, ClientHandler ch) {
		new Thread(()->startServer(port,ch)).start();
	}
	
	public void stop() {
		stop=true;
	}
}
