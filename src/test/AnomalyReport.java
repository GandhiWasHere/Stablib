package test;

public class AnomalyReport {
	public final String description;
	public final  long timeStep;
	public final  float shimi;
	public AnomalyReport(String description, long timeStep, float shimi){
		this.description=description;
		this.timeStep=timeStep;
		this.shimi=shimi;
	}
}
