package jbinvis.backend;

public class LabelProbability {
	private double prob;
	private String labelName;
	
	public LabelProbability(double p, String l) {
		prob = p;
		labelName = l;
	}
	
	public double getProbability() { return prob;}
	public String getLabel() { return labelName;}
	
	void setProbability(double p) {prob = p;}
}
