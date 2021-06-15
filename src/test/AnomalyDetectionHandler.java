package test;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import test.Commands.DefaultIO;
import test.Server.ClientHandler;

public class AnomalyDetectionHandler implements ClientHandler{
	@Override
	public void startClient(InputStream in, OutputStream out){
		SocketIO sio = new SocketIO(in,out);
		CLI cli=new CLI(sio);
		cli.start();
	}
	
	
	@Override
	public void description() {
		System.out.println("AnomalyDetectionHandlerINIT");
	}
	public class SocketIO implements DefaultIO{
		Scanner in;
		PrintWriter out;
		
		public SocketIO(InputStream in,OutputStream out) {
			this.in = new Scanner(new BufferedReader(new InputStreamReader(in)));
			this.out = new PrintWriter(out);
		}

		@Override
		public String readText(){
			String x = in.nextLine();
			//System.out.println("Client\n------------\n"+x);
			return x;
		}

		@Override
		public void write(String text) {
			//System.out.println("Server\n------------\n"+text);
			out.print(text);
			out.flush();
		}

		@Override
		public float readVal() {
			return in.nextFloat();
		}

		@Override
		public void write(float val) {
			out.print(val);
		}

		public void close() {
			in.close();
			out.close();
		}

	}
}
