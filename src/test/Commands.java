package test;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class Commands {
	
	// i am not sure this is the ideal way
	//TODO
	public static float cor ;
	public static List<AnomalyReport> reports;
	// Default IO interface
	public interface DefaultIO{
		public String readText();
		public void write(String text);
		public float readVal();
		public void write(float val);

		// you may add default methods here
	}
	
	// the default IO to be used in all commands
	DefaultIO dio;
	public Commands(DefaultIO dio) {
		this.dio=dio;
	}
	
	// you may add other helper classes here
	
	
	
	// the shared state of all commands
	private class SharedState{
		// implement here whatever you need
		
	}
	
	private  SharedState sharedState=new SharedState();

	
	// Command abstract class
	public abstract class Command{
		protected String description;
		
		public Command(String description) {
			this.description=description;
		}
		
		public abstract void execute();
	}
	

	// implement here all other commands
	public class UploadCSV extends Command{

		public UploadCSV() {
			super("This function gets 2 paths of csv files and read them.");
		}
		

		public void readCsv(String filename){
			String line = "";
	        try (FileWriter writer = new FileWriter(filename);
	                BufferedWriter bw = new BufferedWriter(writer))
	        {
				while (!line.equals("done")) {
					if (!line.equals(""))
						bw.write(line+"\n");
					line = dio.readText();
				}
				bw.close();
	        }
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void execute() {
			// Upload Train CSV
			dio.write("Please upload your local train CSV file.\n");
			this.readCsv("train.csv");
			dio.write("Upload complete.\n");
			
			// Upload Test CSV
			dio.write("Please upload your local test CSV file.\n");
			this.readCsv("test.csv");
			dio.write("Upload complete.\n");
		}
	}
	
	public class ChangeCorr extends Command{

		public ChangeCorr() {
			super("This function prints and change correlation");
		}

		@Override
		public void execute() {
			
			// correlation value is static public so we can use it anywhere in the class.
			dio.write("The current correlation threshold is 0.9\n"
					+ "Type a new threshold\n");
			cor = dio.readVal();
			while (cor > 1 | cor < 0) {
				dio.write("please choose a value between 0 and 1.\n");
				cor = dio.readVal();
			}
		}
		}
	public class InitialTimeSeries extends Command{

		public InitialTimeSeries() {
			super("This function inital anomaly detection");
		}

		@Override
		public void execute() {
			TimeSeries ts = new TimeSeries("train.csv");
			SimpleAnomalyDetector ad=new SimpleAnomalyDetector(cor);
			ad.learnNormal(ts);
			TimeSeries ts2=new TimeSeries("test.csv");
			reports = ad.detect(ts2);
			dio.write("complete anomaly detection\n");
			}
		}
	
	public class reportAnomalies extends Command{

		public reportAnomalies() {
			super("This function report anomalies to the client");
		}

		@Override
		public void execute() {
			for (AnomalyReport r:reports)
				{
					System.out.println((r.timeStep + "\t" + r.description +'\t'));
				}
			}
		}
}
