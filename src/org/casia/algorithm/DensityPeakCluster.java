package org.casia.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.casia.datainput.DataReader;
import org.casia.datainput.Sample;

public class DensityPeakCluster {
	private ArrayList<Sample> samples;
	/**<index, sample >Map*/
	//private HashMap<Integer, Sample> sampleIndexMap;
	/**局部密度Map ：<index,densitycount>*/
	private HashMap<Integer, Integer> densityCountMap;
	/**由大到小排序的Density list*/
	private ArrayList<Map.Entry<Integer, Integer>> sortedDensityList;
	/**deltaMap:<index, delta>*/
	private HashMap<Integer, Double> deltaMap;
	/**每个样本的最近邻：<sampleIndex, nearestNeighborIndex>*/
	private HashMap<Integer, Integer> nearestNeighborMap;
	/**样本对距离：<"index1 index2", distance>*/
	private HashMap<String, Double> pairDistanceMap;
	/**最大样本距离*/
	private double maxDistance;
	/**最小样本距离*/
	private double minDistance;
	/**选取的簇中心*/
	private ArrayList<Integer> centerList;
	/**划分的聚类结果<sampleIndex, clusterIndex>*/
	private HashMap<Integer, Integer> clusterMap;
	public DensityPeakCluster(ArrayList<Sample> samples) {
		this.samples = samples;
//		sampleIndexMap = new HashMap<Integer, Sample>(samples.size());
//		int count = 0;
//		for(Sample s : samples) {
//			sampleIndexMap.put(count++, s);
//		}
	}
	public void clustering(double deltaThreshold, double rhoThreshold) {
		centerList = new ArrayList<Integer>();
		clusterMap = new HashMap<Integer, Integer>();
		//get centers
		for(Map.Entry<Integer, Double> deltaEntry : deltaMap.entrySet()) {
			if(deltaEntry.getValue() >= deltaThreshold && 
					densityCountMap.get(deltaEntry.getKey()) >= rhoThreshold) {
				centerList.add(deltaEntry.getKey());
				clusterMap.put(deltaEntry.getKey(), deltaEntry.getKey());
			}
		}
		// calculate clusters，注意：一定要按照密度由大到小逐个划分簇（从高局部密度到低局部密度）
		for(Map.Entry<Integer, Integer> candidate : sortedDensityList) {
			if(!centerList.contains(candidate.getKey())) {
				//将最近邻居的类别索引作为该样本的类别索引
				if(clusterMap.containsKey(nearestNeighborMap.get(candidate.getKey()))) {
					clusterMap.put(candidate.getKey(), clusterMap.get(nearestNeighborMap.get(candidate.getKey())));
				} else {
					clusterMap.put(candidate.getKey(), -1);
				}
			}
		}
	}
	public void calDelta() {
		//局部密度由大到小排序
		sortedDensityList = new ArrayList<Map.Entry<Integer,Integer>>(densityCountMap.entrySet());
		Collections.sort(sortedDensityList, new Comparator<Map.Entry<Integer, Integer>>() {

			@Override
			public int compare(Entry<Integer, Integer> o1,
					Entry<Integer, Integer> o2) {
				if(o1.getValue() > o2.getValue()) return -1;
				else if (o1.getValue() < o2.getValue()) {
					return 1;
				}
				return 0;
			}
		});
		nearestNeighborMap = new HashMap<Integer, Integer>(samples.size());
		deltaMap = new HashMap<Integer, Double>(samples.size());
		for(int i = 0; i < sortedDensityList.size(); i++) {
			if(i == 0) {
				nearestNeighborMap.put(sortedDensityList.get(i).getKey(), -1);
				deltaMap.put(sortedDensityList.get(i).getKey(), maxDistance);
			} else {
				double minDij = Double.MAX_VALUE;
				int index = 0;
				for(int j = 0; j < i; j++) {
					double dis = getDistanceFromIndex(sortedDensityList.get(i).getKey(), sortedDensityList.get(j).getKey());
					if(dis < minDij)  {
						index = j;
						minDij = dis;
					}
				}
				nearestNeighborMap.put(sortedDensityList.get(i).getKey(), sortedDensityList.get(index).getKey());
				deltaMap.put(sortedDensityList.get(i).getKey(), minDij);
			}
		}
		
		//输出样本索引+样本局部密度+最近邻索引+delta值
		System.out.println("输出样本索引  样本局部密度  最近邻索引  delta值");
		for(Map.Entry<Integer, Integer> entry : sortedDensityList) {
			System.out.println(entry.getKey()+" "+entry.getValue()+" "+
		nearestNeighborMap.get(entry.getKey())+" "+deltaMap.get(entry.getKey()));
		}
	}
	/**
	 * 根据索引获得两个样本间距离
	 * @param index1
	 * @param index2
	 * @return
	 */
	private double getDistanceFromIndex(int index1, int index2) {
		if(pairDistanceMap.containsKey(index1+" "+index2)) {
			return pairDistanceMap.get(index1+" "+index2);
		} else {
			return pairDistanceMap.get(index2+" "+index1);
		}
	}
	/**
	 * 计算局部密度
	 */
	public void calRho(double dcThreshold) {
		densityCountMap = new HashMap<Integer, Integer>(samples.size());
		//初始化为0
		for(int i= 0; i < samples.size(); i++) {
			densityCountMap.put(i, 0);
		}
		for(Map.Entry<String, Double> diss : pairDistanceMap.entrySet()) {
			if(diss.getValue() < dcThreshold) {
				String[] segs = diss.getKey().split(" ");
				int[] indexs = new int[2];
				indexs[0] = Integer.parseInt(segs[0]);
				indexs[1] = Integer.parseInt(segs[1]);
				for(int i = 0; i < indexs.length; i++) {
					densityCountMap.put(indexs[i], densityCountMap.get(indexs[i]) + 1);
				}
			}
		}
	}
	/**
	 * 计算所有样本每两个样本点的距离
	 */
	public void calPairDistance() {
		pairDistanceMap = new HashMap<String, Double>();
		maxDistance = Double.MIN_VALUE;
		minDistance = Double.MAX_VALUE;
		for(int i = 0; i < samples.size() - 1; i++) {
			for(int j = i+1; j < samples.size(); j++) {
				double dis = twoSampleDistance(samples.get(i), samples.get(j));
				pairDistanceMap.put(i+" "+j, dis);
				if(dis > maxDistance) maxDistance = dis;
				if(dis < minDistance) minDistance = dis;
			}
		}
	}
	/**
	 * 计算截断距离
	 * @return
	 */
	public double findDC(){
		double tmpMax = maxDistance;
		double tmpMin = minDistance;
		double dc = 0.5 * (tmpMax + tmpMin);
		for(int iteration = 0; iteration < 100; iteration ++) {
			int neighbourNum = 0;
			for(Map.Entry<String, Double> dis : pairDistanceMap.entrySet()) {
				if(dis.getValue() < dc) neighbourNum += 2;
			}
			double neighborPercentage = neighbourNum / Math.pow(samples.size(), 2);
			if(neighborPercentage >= 0.01 && neighborPercentage <= 0.02) break;
			if(neighborPercentage > 0.02) {
				tmpMax = dc;
				dc = 0.5 * (tmpMax + tmpMin);
			}
			if(neighborPercentage < 0.01) {
				tmpMin = dc;
				dc = 0.5 * (tmpMax + tmpMin);
			}
			
		}
		return dc;
	}
	/**
	 * 计算两个样本的高斯距离
	 * @param a
	 * @param b
	 * @return
	 */
	private double twoSampleDistance(Sample a, Sample b){
		double[] aData = a.getAttributes();
		double[] bData = b.getAttributes();
		double distance = 0.0;
		for(int i = 0; i < aData.length; i++) {
			distance += Math.pow(aData[i] - bData[i], 2);
		}
		return 1 - Math.exp(distance * (-0.5));
	}
	public ArrayList<Integer> getCenterList() {
		return centerList;
	}
	
	public void predictLabel() {
		for(int i = 0; i < samples.size(); i++) {
			//System.out.println(clusterMap.get(i));
			if(clusterMap.get(i) != -1)
				samples.get(i).setPredictLabel(samples.get(clusterMap.get(i)).getLabel());
		}
	}
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
		cluster.clustering(0.38, 1);
		System.out.println(cluster.getCenterList());
	}
}
