package test;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TimeSeries {
	private HashMap<String,List<Float>> map;
	
	
	public TimeSeries(){
		this.map = new HashMap<String,List<Float>>();
	}
	

	public TimeSeries(ArrayList<String> main_tokens, ArrayList<Float> tokens){
		this.map = new HashMap<String,List<Float>>();
	}
	
	// This Function purpose is initializing the TimeSeirs using external data
	public TimeSeries(String csvFileName) {
		this.map = new HashMap<String,List<Float>>();
		final String DELIMITER = ",";
        BufferedReader fileReader = null;
		try {
			String line = "";
            fileReader = new BufferedReader(new FileReader(csvFileName));
       
            // First line init the names of the columns.
            line = fileReader.readLine();
            String[] main_tokens = line.split(DELIMITER);
            for (String token: main_tokens)
            {
            	map.put(token, new ArrayList<Float>());
            }
            while ((line = fileReader.readLine()) != null) 
            {
                //Get all tokens available in line
                String[] tokens = line.split(DELIMITER);
                for(int i=0;i<tokens.length;i++)
                {
                	map.get(main_tokens[i]).add(Float.parseFloat(tokens[i]));
                }
            }
        }
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// getter of col
	public List<Float> get(String st){
		return this.map.get(st);
	}
	
	//getter of index
	public Float get(String i, int j){
		return this.map.get(i).get(j);
	}
	
	//getter of the hashmap 
	public HashMap<String, List<Float>> get(){
		return this.map;
	}
	
	public String[] getFeatures() {
		String[] s = (String[]) this.map.keySet().toArray(new String[0]);
		return (s);
	}
	
	public Set<String> getListFeatures() {
		return this.map.keySet();
	}
	
	
	// getter of col as FloatArray
	public float[] getArray(String st) {
		List<Float> floatList = this.map.get(st);
		float[] floatArray = new float[floatList.size()];
		int i = 0;

		for (Float f : floatList) {
		    floatArray[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
		}
		return floatArray;
	}
	
	public int getLen() {
		return (getArray(getFeatures()[0]).length);
	}


	//This function get a line and adds it to its location
	public void addLine(Float[] tokens) {
		String[] main_tokens = getFeatures();
		
        //Get all tokens available in line
        for(int i=0;i<tokens.length;i++)
        {
        	map.get(main_tokens[i]).add(tokens[i]);
        }
	}
}
	