package org.casia.main;

import java.util.ArrayList;

import org.casia.algorithm.DensityPeakCluster;
import org.casia.datainput.DataReader;
import org.casia.datainput.Sample;
import org.casia.evaluate.Evaluation;

public class DensityPeakClusteringTest {
	public static void main(String[] args) {
		DataReader reader = new DataReader();
		reader.readData();
		ArrayList<Sample> samples = reader.getSamples();
		DensityPeakCluster cluster = new DensityPeakCluster(samples);
		cluster.calPairDistance();
		double dc = cluster.findDC();
		System.out.println(dc);
		cluster.calRho(dc);
		cluster.calDelta();
		cluster.clustering(0.3, 1);
		System.out.println("cluster center index list is "+cluster.getCenterList());
		cluster.predictLabel();
		
		Evaluation evaluation = new Evaluation(samples);
		evaluation.precision();
	}
}
