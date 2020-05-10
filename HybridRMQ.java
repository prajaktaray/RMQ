package pxr180025;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import pxr180025.Timer;


public class HybridRMQ implements RMQ{

	SparseTableRMQ blockMinRMQ;
    int blockSize;		
    int blocks;					// number of blocks
    float[] originalArray;		// original input array
    int[] originalIndices;		// original indices of elements of input array
    float[] blockMinima;		// array of block minima
    
    
   
    /**
     * Creates a new HybridRMQ structure to answer queries about the given array
     * @param arr		array over which RMQ is computed
     */
    public HybridRMQ(float[] arr) {
    	//originalArray = arr;
    	
        int arrLength = arr.length;
        if ( arrLength <= 1) {
            return;
        }
        System.arraycopy(arr, 0, originalArray, 0, arrLength);
        
        blockSize = (int)(Math.log(arrLength) / Math.log(2)); 
        blocks = (int) Math.ceil((double)(arrLength)/blockSize);
        
        initializeBlockMinArr();       	
        
        blockMinRMQ = new SparseTableRMQ(blockMinima);
    }
    
    
    /**
     * Initializes blockMinima array with minimum of each block
     */
    private void initializeBlockMinArr() {
    	
    	blockMinima =new float[blocks];
    	originalIndices = new int[blocks];
    	
        int block = 0;
        float min = originalArray[0];
        int minIndex = 0;
        
        for (int i = 1; i < originalArray.length; i++) {
            if (i % blockSize == 0) {
            	blockMinima[block] = min;
            	originalIndices[block] = minIndex;
                block++;
                min = originalArray[i];
                minIndex = i;
            }
            
            minIndex = minIndex(i,minIndex);
            min = originalArray[minIndex];
        }
        blockMinima[block] = min;
        originalIndices[block] = minIndex;
		return;
		
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
     * Perform Linear search within the given two indices
     * @param i		starting index 
     * @param j		end index
     * @return		index of the element with minimum value between the indices
     */    
	private int linearSearch(int i, int j) {
        int minIndex = i;
        float min = originalArray[i];
        for (int k = i + 1; k <= j; k++) {
            if (originalArray[k] < min) {
                min = originalArray[k];
                minIndex = k;
            }
        }
        return minIndex;
    }

    /**
     * Computes RMQ(i, j) over the input array and returns
     * the index of the minimum value in that range
     */
    @Override
    public int rmq(int i, int j) {
        if (i == j) {
            return i;
        }
        
        int iBlock = i / blockSize;
        int jBlock = j / blockSize;
        
        // If i and j are in the same block or adjacent blocks
        if ((jBlock - iBlock) <= 1) {
            return linearSearch(i, j);
        }
        else {
        	
            int iBlockEnd = ((iBlock + 1) * blockSize) - 1;
            int jBlockStart = jBlock * blockSize;
            int iIndex = linearSearch(i, iBlockEnd);
            int jIndex = linearSearch(jBlockStart, j);
            
            float min;
            int minIndex;
            
            minIndex = minIndex(iIndex,jIndex);
          //  min = originalArray[minIndex];
            
            int blockMinIndex = minBlock(iBlock + 1, jBlock - 1);
            
            return minIndex(minIndex,originalIndices[blockMinIndex]);
            
        }
    }
    
    /**
     * Helper function to find the block index of the minimum value 
     * for the given range of block indices  
     * @param i		starting block index
     * @param j		last block index
     * @return		block index of the minimum value between the given range
     */
    private int minBlock(int i, int j) {
		int k = blockMinRMQ.logs[j-i];
		int twotok = blockMinRMQ.powers.get(k);
		int min = minIndex(blockMinRMQ.sparseTable[i][k], blockMinRMQ.sparseTable[j-twotok+1][k]);
		return min;
	
	}
    

    
	public static void main(String args[]) throws FileNotFoundException
	{
		Scanner in;
		Random rand = new Random(); 
		float[] arr;
		int size;
		int minIndex ;
		
		if (args.length == 0 ) {
			arr = new float[]{1,24,32,58,1,6,7,6,94,86,16,20,0 }; 
			size = arr.length;
			
		} else {
			File inputFile = new File(args[0]);
			in = new Scanner(inputFile);
		
		
		size = in.nextInt();
		arr = new float[size];
		
		for(int i =0; i< size;i++) {
			arr[i] = in.nextFloat();
		}
		}

		System.out.println("Time for Preprocessing:");
		Timer timer = new Timer();
		HybridRMQ hrmq = new HybridRMQ(arr);
		System.out.println(timer.end());
		
		System.out.println("");
		int i = rand.nextInt(size);
		int j = rand.nextInt(size);
		int result = rand.nextInt(size-i) + i;
		
		System.out.println("Time for Query:");
		Timer timer2 = new Timer();
		minIndex = hrmq.rmq(i,result);
		/*
		 * if(i<j) { //System.out.println("Range : "+ i +" - " + j); timer2 = new
		 * Timer(); minIndex = hrmq.rmq(i,j); } else { //System.out.println("Range : "+
		 * j +" - " + i); timer2 = new Timer(); minIndex = hrmq.rmq(j,i); }
		 */		
		
		System.out.println(timer2.end());
		
		System.out.println("\n");
		System.out.println("Range : "+ i +" - " + result);
		System.out.println("Index of minimum value : " + minIndex);
    	System.out.println("Minimum value : " + arr[minIndex]);
		
	}
}
