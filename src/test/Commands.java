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
	
	// read from data write to csv
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
		public float cor ;
		public List<AnomalyReport> reports;
		public int NUMBER_LINES = 0;
		public ArrayList<RangeAnomaly> rAnomalies;
		public ArrayList<RangeAnomaly> uAnomalies;
		
		
	}
	
	// Class that defines a range window of anomaly
	public class RangeAnomaly {
		public String feature;
		public long start;
		public long end;
		public RangeAnomaly(long l, long timeStep , String feature) {
			this.feature = feature;
			this.start = l;
			this.end = timeStep;
		}
		public RangeAnomaly() {
			this.feature = null;
			this.start = 0;
			this.end = 0;
		}
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
			sharedState.cor = dio.readVal();
			while (sharedState.cor > 1 | sharedState.cor < 0) {
				dio.write("please choose a value between 0 and 1.\n");
				sharedState.cor = dio.readVal();
			}
		}
		}
	public class InitialTimeSeries extends Command{

		public InitialTimeSeries() {
			super("This function inital anomaly detection");
		}

		@Override
		public void execute() {
			
			// initial time series and start learning
			TimeSeries ts = new TimeSeries("train.csv");
			sharedState.NUMBER_LINES = ts.getLen();
			SimpleAnomalyDetector ad=new SimpleAnomalyDetector(sharedState.cor);
			ad.learnNormal(ts);
			TimeSeries ts2=new TimeSeries("test.csv");
			sharedState.reports = ad.detect(ts2);
			dio.write("anomaly detection complete.\n");
			}
		}
	
	public class reportAnomalies extends Command{

		public reportAnomalies() {
			super("This function report anomalies to the client");
		}

		@Override
		public void execute() {
			
			// report all anomlies
			for (AnomalyReport r:sharedState.reports)
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
			CSV csvfile = new CSV("shimi.csv"); // TODO : CHANGE THE FILENAME
			dio.write("Upload complete.\n");
			csvfile.readCSV();
			
			// Parse anomalies into a range of anomalies seperated by type
			sharedState.rAnomalies = new ArrayList<RangeAnomaly>();
			AnomalyParser();
			
			// Parse uploaded anomalies
			sharedState.uAnomalies = new ArrayList<RangeAnomaly>();
			for(String[] l: csvfile.list) {
				AnomalyParser(l);
			}
			// This method checks if the window is between start and end time.
			float tp = 0;
			float fp = 0;
			float p = csvfile.list.size();
			float n = sharedState.NUMBER_LINES;
			float N = n - sumValues(csvfile.list);
			for(RangeAnomaly r: sharedState.rAnomalies)
				for(RangeAnomaly u: sharedState.uAnomalies)
					if (r.start <= u.end && r.end >= u.start) {
						tp++;
					}
			boolean af;
			for(RangeAnomaly r: sharedState.rAnomalies) {
				af = false;
				for(RangeAnomaly u: sharedState.uAnomalies)
					if (r.start <= u.end && r.end >= u.start) {
						af = true;
						break;
					}
				if (!af) 
					fp++;
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
		
		public void AnomalyParser(String[] l) { 
			sharedState.uAnomalies.add(new RangeAnomaly(Long.parseLong(l[0]),
				Long.parseLong(l[1]),""));
		}
		public  void AnomalyParser() {
			int counter = 0;
			AnomalyReport prev = sharedState.reports.get(0);
			for (int i = 1; i< sharedState.reports.size(); i++) {
				AnomalyReport current = sharedState.reports.get(i);
				if (prev.description.equals(current.description) && (prev.timeStep == current.timeStep-1)) {counter++;}
				else {
					if (counter > 0){
						sharedState.rAnomalies.add(new RangeAnomaly(prev.timeStep-counter,prev.timeStep,prev.description));
						counter = 0;
					}
				}
				prev = current;
			}
			if (counter > 0){
				sharedState.rAnomalies.add(new RangeAnomaly(prev.timeStep-counter,prev.timeStep,prev.description));
				counter = 0;
			}
		}
	}
	public class exit extends Command{

		public exit() {
			super("This function exit");
		}

		@Override
		public void execute() {
			dio.write("bye\n");
			}
		}
		}
