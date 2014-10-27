package org.casia.datainput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataReader {
	private ArrayList<Sample> samples;
	
	public void readData() {
		File f = new File("./data/iris.data");
		samples = new ArrayList<Sample>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = null;
			while((line = br.readLine())!=null ) {
				double[] atts = new double[4];
				if(line.length() ==0) continue;
				String[] seg = line.split(",");
				for(int i = 0; i< seg.length - 1; i++) {
					atts[i] = Double.parseDouble(seg[i]);
				}
				samples.add(new Sample(atts, seg[4]));
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Sample> getSamples() {
		return samples;
	}
	public static void main(String[] args) {
		DataReader reader = new DataReader();
		reader.readData();
		System.out.println(reader.getSamples());
	}
}
