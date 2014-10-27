package org.casia.datainput;

public class Sample {
	private double[] attributes;
	private String label;
	private String predictLabel;
	public Sample(double[] attributes, String label) {
		this.attributes = attributes;
		this.label = label;
	}
	public double[] getAttributes() {
		return attributes;
	}
	public String getLabel() {
		return label;
	}
	public String getPredictLabel() {
		return predictLabel;
	}
	public void setPredictLabel(String predictLabel) {
		this.predictLabel = predictLabel;
	}
	
}
