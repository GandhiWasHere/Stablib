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
		commands.put(5f, c.new uploadAnomalies());
		commands.put(6f, c.new exit());
		// implement
	}
	
	public void start() {
		
		// It depends on the input of the server
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
		this.dio.write(s.toString());
		this.commands.get(this.dio.readVal()).execute();
		this.dio.write(s.toString());
		this.commands.get(this.dio.readVal()).execute();
		this.dio.write(s.toString());
		this.commands.get(this.dio.readVal()).execute();
		this.dio.write(s.toString());
		this.commands.get(this.dio.readVal()).execute();
		this.dio.write(s.toString());
		this.commands.get(this.dio.readVal()).execute();
		this.dio.write(s.toString());
		this.commands.get(this.dio.readVal()).execute();
		this.dio.write(s.toString());
		this.commands.get(this.dio.readVal()).execute();
		this.dio.write(s.toString());
		this.commands.get(this.dio.readVal()).execute();
		this.dio.write(s.toString());
		this.commands.get(this.dio.readVal()).execute();
	}
}
