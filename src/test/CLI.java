package test;

import java.util.ArrayList;
import java.util.HashMap;

import test.Commands.Command;
import test.Commands.DefaultIO;

public class CLI {

	HashMap<Float, Command> commands;
	DefaultIO dio;
	Commands c;
	
	public CLI(DefaultIO dio) {
		this.dio=dio;
		c=new Commands(dio); 
		commands=new HashMap<>();
		commands.put(1f, c.new UploadCSV());
		commands.put(2f, c.new ChangeCorr());
		commands.put(3f, c.new InitialTimeSeries());
		commands.put(4f, c.new reportAnomalies());
		
		// implement
	}
	
	public void start() {
		StringBuilder s = new StringBuilder()
				.append("Welcome to the Anomaly Detection Server.\n")
				.append("Please choose an option:\n")
				.append("1. upload a time series csv file\n")
				.append("2. algorithm settings\n")
				.append("3. detect anomalies\n")
				.append("4. display results\n")
				.append("5. upload anomalies and analyze results\n")
				.append("6. exit\n");
		this.dio.write(s.toString());
		float c = this.dio.readVal();
		this.commands.get(c).execute();
		
		// THERE IS A BIG ERROR WITH THE WRITING TO THE FILE I PROBBLY ADD ONE CHAR THAT DONT NEED TO BE THERE
		this.commands.get(this.dio.readVal()).execute();
		this.commands.get(this.dio.readVal()).execute();
		this.commands.get(this.dio.readVal()).execute();
		this.dio.write(s.toString());
		this.commands.get(this.dio.readVal()).execute();
	}
}
