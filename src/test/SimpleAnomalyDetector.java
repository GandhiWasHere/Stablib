package test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {
	private float threshold = 0.1F;
	private HashMap<String,Float> max;
	private HashMap<String,Line> max_line;
	private List<CorrelatedFeatures> cf;
	
	// default constructor
	public SimpleAnomalyDetector()
	{
		this.cf = new ArrayList<CorrelatedFeatures>();
		this.max = new HashMap<String,Float>();
		this.max_line = new HashMap<String,Line>();
	}
	
	// empty constructor
	public SimpleAnomalyDetector(float t)
	{
		this.threshold = t;
		this.cf = new ArrayList<CorrelatedFeatures>();
		this.max = new HashMap<String,Float>();
		this.max_line = new HashMap<String,Line>();
	}
	
	//This method is used to "learn" the machine using a given Timesires(dataset)
	@Override
	public void learnNormal(TimeSeries ts) {
		float p;
		int tmp_index_max = 0;
		float tmp_max = 0F;
		String[] features = ts.getFeatures();
		List<String> alreadyin = new ArrayList<String>();
		for(int i =0; i<features.length; i++) {
			float[] f1 = ts.getArray(features[i]);
			tmp_max = 0F;
			for (int j=0; j<features.length; j++)
			{
				float[]  f2 = ts.getArray(features[j]);
				if (alreadyin.contains(features[i])) {
					break;
				}
				if (alreadyin.contains(features[j])) {
					continue;
				}
				if (features[i].equals(features[j])){
					continue;
				}
				
				// Check if it is the maximal pearson
				p = Math.abs(StatLib.pearson(f1, f2));
				if (p > tmp_max ){
					tmp_index_max = j;
					tmp_max = p;
				}
			}
			
			// Insert Correlated features to the list.				
			if (tmp_max>threshold) {
				float[]  f2 = ts.getArray(features[tmp_index_max]);
				Line l = StatLib.linear_reg_floats(f1,f2);
				cf.add(new CorrelatedFeatures(features[i], features[tmp_index_max], tmp_max, l, threshold));
				max.put(features[i], maxDev(f1,f2,l));
				max.put(features[tmp_index_max], maxDev(f1,f2,l));
				max_line.put(features[i], l);
				max_line.put(features[tmp_index_max], l);
				alreadyin.add(features[i]);
				alreadyin.add(features[tmp_index_max]);
			}
		}
	}

	// This method is used to detect anommaly given a new dataset based on the privous
	@Override
	public List<AnomalyReport> detect(TimeSeries ts) {
		List<AnomalyReport> ar = new ArrayList<AnomalyReport>();
		float[] f1;
		float[] f2;
		float temp_dev;
		if (this.cf == null)
			this.learnNormal(ts);
		else {
			for (CorrelatedFeatures c: this.cf) {
				f1 = ts.getArray(c.feature1);
				f2 = ts.getArray(c.feature2);
				//Line l = StatLib.linear_reg_floats(f1,f2);
				Line l = max_line.get(c.feature1);
				for (int i=0; i<f1.length;i++) {
					temp_dev = Math.abs(StatLib.dev(new Point(f1[i],f2[i]), l));
					if (temp_dev > this.max.get(c.feature1)*1.1) {
						String description = String.format("%s-%s", c.feature1, c.feature2);
						ar.add(new AnomalyReport(description,(long)i+1 , temp_dev));
					}
				}				
			}
		}
		return ar;
	}
	
	//getters and setters
	public List<CorrelatedFeatures> getNormalModel(){
		return this.cf;
	}

	
	public List<CorrelatedFeatures> getCf() {
		return cf;
	}

	public void setCf(List<CorrelatedFeatures> cf) {
		this.cf = cf;
	}
	
	public boolean featureExsist(String feature, List<CorrelatedFeatures> cf){
		for (CorrelatedFeatures c: this.cf) {
			if (c.feature1.equals(feature)) return true;
			else if(c.feature2.equals(feature)) return true;
		}
		return false;
	}
	
	//used to return the maximum dev
	public float maxDev(float[] x, float[] y,Line l) {
		float temp_max = 0F; 
		float temp_dev=0F;
		for (int i=0;i<x.length;i++) {
			temp_dev = Math.abs(StatLib.dev(new Point(x[i],y[i]),l));
			if (temp_dev > temp_max){
				temp_max = temp_dev;
			}
		}
		return temp_max;
	}
	
}
