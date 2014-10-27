package org.casia.evaluate;

import java.util.ArrayList;

import org.casia.datainput.Sample;

public class Evaluation {
	private ArrayList<Sample> samples;
	
	public Evaluation(ArrayList<Sample> samples){
		this.samples = samples;
	}
	
	public void precision(){
		int count = 0;
		for(Sample sample : samples) {
			if(sample.getPredictLabel() != null && 
					sample.getLabel().equals(sample.getPredictLabel())) {
				count ++;
			}
		}
		System.out.println("precison is "+count * 1.0 / samples.size());
	}
}
