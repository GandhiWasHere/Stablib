package test;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Commands {
	
	// i am not sure this is the ideal way
	//TODO
	public static float cor ;
	public static List<AnomalyReport> reports;
	public static int NUMBER_LINES = 0;

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
	
	public class CSV{
		private String filename;
		private String DELIMITER = ",";
		public ArrayList<String[]> list;
		
		
		public CSV(String filename){
			this.filename = filename;
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
		
		public void readCSV(){
			String line = "";
			list = new ArrayList<String[]>() ;
	        try (FileReader reader = new FileReader(this.filename);
	                BufferedReader bw = new BufferedReader(reader))
	        {
				while (line != null) {
					if (!line.equals("")) {
						list.add(line.split(DELIMITER));
					}
					line = bw.readLine();
				}
				bw.close();
	        }
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
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
		
		@Override
		public void execute() {
			// Upload Train CSV
			dio.write("Please upload your local train CSV file.\n");
			new CSV("train.csv");
			dio.write("Upload complete.\n");
			
			// Upload Test CSV
			dio.write("Please upload your local test CSV file.\n");
			new CSV("test.csv");
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
			NUMBER_LINES = ts.getLen();
			SimpleAnomalyDetector ad=new SimpleAnomalyDetector(cor);
			ad.learnNormal(ts);
			TimeSeries ts2=new TimeSeries("test.csv");
			reports = ad.detect(ts2);
			dio.write("anomaly detection complete.\n");
			}
		}
	
	public class reportAnomalies extends Command{

		public reportAnomalies() {
			super("This function report anomalies to the client");
		}

		@Override
		public void execute() {
			for (AnomalyReport r:reports)
				dio.write((r.timeStep + "\t" + r.description +'\n'));			
			dio.write("Done.\n");
			}
		}
	
	public class uploadAnomalies extends Command{

		public uploadAnomalies() {
			super("This function let the user the abillity to upload anomalies timestep");
		}

		@Override
		public void execute() {
			dio.write("Please upload your local anomalies file.\n");
			//String filename = dio.readText();
			CSV csvfile = new CSV("shimi.csv"); // TODO : CHANGE THE FILENAME
			dio.write("Upload complete.\n");
			csvfile.readCSV();
			
			// This method checks if the window is between start and end time.
			float tp = 0;
			float fp = 0;
			float p = csvfile.list.size();
			float n = NUMBER_LINES;
			float N = n - sumValues(csvfile.list);
			for(String[] l: csvfile.list) {
				int temp = checkWindow(l);
				if (temp > 0) {tp++;}
				else {fp++;};
			}
			DecimalFormat df = new DecimalFormat("0.0##");
			df.setRoundingMode(RoundingMode.DOWN); 
			dio.write(new StringBuilder("True Positive Rate: ").append(df.format((tp/p))).append("\n").toString());
			dio.write(new StringBuilder("False Positive Rate: ").append(df.format((fp/(N)))).append("\n").toString());
			}
		
		public int sumValues(ArrayList<String[]> l) {
			int sum = 0;
			for(String[] small: l)
			{
				sum += (Integer.parseInt(small[1]) - Integer.parseInt(small[0]))+1;
			}
			return sum;
		}
		
		public int checkWindow(String[] l) { //TODO : BY COR FEATURE
			int windowsize = 0;
			int start = Integer.parseInt(l[0]);
			int end = Integer.parseInt(l[1]);
			for (AnomalyReport r:reports) {
				if ((r.timeStep >= start) && (r.timeStep <= end))
					windowsize++;
			}
			return windowsize;
		}
		
	}
}
