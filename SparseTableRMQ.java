
package pxr180025;

import java.util.ArrayList;

interface RMQ {
    public int rmq(int i, int j);
}

public class SparseTableRMQ implements RMQ {
	protected int[][] sparseTable;
	protected int[] logs;
	protected ArrayList<Integer> powers;
	private float[] elements;  // Store the array

	/**
	 * Constructs an array that at each index i holds the largest value k such
	 * that 2^k is less than or equal to i + 1.
	 * 
	 * @n the number of indices i to calculate this up to 
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
	 * Constructs an array that at each index i holds 2^i. After this is
	 * computed, raising 2 to a power can be done in constant time.
	 * 
	 * @n the max that 2^i can go up to 
	 */
	private void powerCalculator(int length) {
		powers = new ArrayList<Integer>();		
		for( int i = 1; i<= length; i *= 2) {
			powers.add(i);
		}
	}

	/**
	 * Helper function for getting which index has the minimum value in 
	 * the array
	 * @param index1 the first index in question
	 * @param index2 the second
	 * @return the index that represents the min
	 */
	private int minIndex(int index1, int index2) {
		if (elements[index1] <= elements[index2])
			return index1;
		else
			return index2;
		
	}
	
	/**
	 * Creates a new SparseTableRMQ structure to answer queries about the
	 * array given by elems.
	 *
	 * @elems The array over which RMQ should be computed.
	 */
	public SparseTableRMQ(float[] input) {
		
		if (input.length==0) return; 
		int len = input.length;
		elements = new float[len];
		System.arraycopy(input, 0, elements, 0, len);
		
		// Calculate logs and powers up front
		CalculateLogs(len);
		powerCalculator(len);
		// Construct sparse table
		
		int column_log = (int)(Math.log(len) / Math.log(2));
		
		sparseTable = new int[len][column_log+1];
		for (int i = 0; i < len; i++) {
			sparseTable[i][0] = i; 
		}
				
		for (int k = 1; k < column_log + 1; k++) {
			int powerVal = powers.get(k);
			for (int rowIndex1 = 0; rowIndex1 < len - powerVal + 1; rowIndex1++) {
				int rowIndex2 = rowIndex1 + powerVal/2;
				sparseTable[rowIndex1][k] = minIndex(sparseTable[rowIndex1][k-1], sparseTable[rowIndex2][k-1]);
			}
		} 
		
		/*
		 * for (int i =0; i< len;i++) { for (int j = 0; j< column_log+1;j++) {
		 * System.out.print(i + " " + j +"\t");
		 * System.out.print(elements[sparseTable[i][j]]+"\t"+sparseTable[i][j]+"\t"); }
		 * System.out.println("\n"); } System.out.println("\n");
		 */
	}

	/**
	 * Evaluates RMQ(i, j) over the array stored by the constructor, returning
	 * the index of the minimum value in that range.
	 */
	@Override
	public int rmq(int i, int j) {
		int k = logs[j-i];
		int twotok = powers.get(k);
		return minIndex(sparseTable[i][k], sparseTable[j-twotok+1][k]);
	}
}
