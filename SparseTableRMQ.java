package pxr180025;

import java.util.ArrayList;

interface RMQ {
    public int rmq(int i, int j);
}

public class SparseTableRMQ implements RMQ {
	protected int[][] sparseTable;
	protected int[] logs;
	protected ArrayList<Integer> powers;
	private float[] originalArray;  // Store the array
	
	/**
	 * Creates a new SparseTableRMQ structure for the input array
	 * @param input		array over which RMQ is computed
	 */
	public SparseTableRMQ(float[] input) {
		
		if (input.length==0) return; 
		int len = input.length;
		originalArray = new float[len];
		System.arraycopy(input, 0, originalArray, 0, len);
		
		// Calculate logs and powers up front
		CalculateLogs(len);
		powerCalculator(len);
		
		int logColumn = (int)(Math.log(len) / Math.log(2));
		
		sparseTable = new int[len][logColumn+1];
		for (int i = 0; i < len; i++) {
			sparseTable[i][0] = i; 
		}
				
		for (int k = 1; k < logColumn + 1; k++) {
			int powerVal = powers.get(k);
			for (int rowIndex1 = 0; rowIndex1 < len - powerVal + 1; rowIndex1++) {
				int rowIndex2 = rowIndex1 + powerVal/2;
				sparseTable[rowIndex1][k] = minIndex(sparseTable[rowIndex1][k-1], sparseTable[rowIndex2][k-1]);
			}
		} 
		
	}
	
	/**
	 * Helper function that creates an array which holds the largest value k 
	 * such that 2^k is less than or equal to i + 1
	 * @param n		length of the array to be created
	 */
	private void CalculateLogs(int n) {
		logs = new int[n];
		int k = 0;
		int twotok = 1;
		for (int i=0; i < n; i++) {
			// Check if we can bump k up
			if (twotok*2 <= i + 1) {
				k++;
				twotok *= 2;
			}
			logs[i] = k;
		}
	}

	
	/**
	 * Helper function that creates an array which holds value 2^i at index i 
	 * @param length	length of the array to be created
	 */
	private void powerCalculator(int length) {
		powers = new ArrayList<Integer>();		
		for( int i = 1; i<= length; i *= 2) {
			powers.add(i);
		}
	}

	/**
     * Helper function to find the index of the minimum value among 2 elements 
     * @param index1	index of element1
     * @param index2	index of element2
     * @return			index of the element with minimum value
     */
	private int minIndex(int index1, int index2) {
		if (originalArray[index1] <= originalArray[index2])
			return index1;
		else
			return index2;
		
	}

	/**
     * Computes RMQ(i, j) over the input array and returns
     * the index of the minimum value in that range
     */
	@Override
	public int rmq(int i, int j) {
		int k = logs[j-i];
		int twotok = powers.get(k);
		return minIndex(sparseTable[i][k], sparseTable[j-twotok+1][k]);
	}
}
