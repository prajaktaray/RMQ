package hxr190001;

/**
@author Harshita Rastogi
@author Prajakta Ray
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;


/** Hybrid implementation of the RMQ - Approach 1 */
public class HybridRMQ1 {
	int blockSize;		
    int blocks;					// number of blocks
    float[] originalArray;		// original input array
    int[] originalIndices;		// original indices of elements of input array
    float[] blockMinima;
    int [][] sparseTable;       //creating a sparse table 
    int len;                    //size of array

    
    /**
	 * Helper function to get the index with the minimum value
	 * in the given range
	 * @param i the start index
	 * @param j the end index
	 * @return the index that represents the minimum value 
	 */
    public int minIndex(int i, int j) {
		if (originalArray[i] <= originalArray[j])
			return i;
		else
			return j;
		
	} 
    
    /**
	 * Initializes block_minima of blocks with minimum of each block.
	 * @return
	 */
    void initializeBlockMinArr(){
    	blockMinima =new float[blocks];
    	originalIndices = new int[blocks];
    	
        int block = 0;
        float min = originalArray[0];
        int minIndex = 0;
        
        for (int i =1 ; i < originalArray.length; i++) {
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
	 * Builds sparse table using the input array
	 * @param inputArr the input array
	 */
    void SparseTableRMQ(float []inputArr){
    	if (inputArr.length==0) return; 
		int len = inputArr.length;
		float[] originalSparseArr = new float[len];
		System.arraycopy(inputArr, 0, originalSparseArr, 0, len);
		
		int logColumn = (int)(Math.log(len) / Math.log(2));
		
		sparseTable = new int[len][logColumn+1];
		for (int i = 0; i < len; i++) {
			sparseTable[i][0] = i; 
		}
				
		for (int k = 1; k < logColumn + 1; k++) {
			int powerVal = (int)Math.pow(2,k);
			for (int rowIndex1 = 0; rowIndex1 < len - powerVal + 1; rowIndex1++) {
				int rowIndex2 = rowIndex1 + powerVal/2;
				sparseTable[rowIndex1][k] = minIndex(sparseTable[rowIndex1][k-1], sparseTable[rowIndex2][k-1]);
			}
		} 
    }
    
    
    /**
     * Uses HybridRMQ to compute the answer to the query
     * @param array The array over which RMQ is computed.
     */
    public HybridRMQ1(float[] array){
    	
    	int arrLength = array.length;
        if ( arrLength <= 1) {
            return;
        }
        originalArray= new float[arrLength];
        System.arraycopy(array, 0, originalArray, 0, arrLength);
        
        blockSize = (int)(Math.log(arrLength) / Math.log(2)); 
        blocks = (int) Math.ceil((double)(arrLength)/blockSize);
        
        initializeBlockMinArr();       	
        SparseTableRMQ(blockMinima);
    }

    /**
     * Helper method to find out the minimum index
     * @param i the starting index
     * @param j the ending index
     * @return minIndex the minimum index in the range
     */
    public int linearSearch(int i, int j) {
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
     * Computes RMQ(i, j) over the array - query
     * @param i staring index
     * @param j ending index
     * @return index of minimum value in that range
     */
    public int rmq(int i, int j) {
    	//if starting and ending index is same
        if (i == j) {
            return i;
        }
        
        int iBlock = i / blockSize;
        int jBlock = j / blockSize;
        
        // if i and j are in the same block or adjacent blocks
        if ((jBlock - iBlock) <= 1) {
            return linearSearch(i, j);
        }
        else {
        	
            int iBlockEnd = ((iBlock + 1) * blockSize) - 1;
            int jBlockStart = jBlock * blockSize;
            int iIndex = linearSearch(i, iBlockEnd);
            int jIndex = linearSearch(jBlockStart, j);
            
            int minIndex;
            minIndex = minIndex(iIndex,jIndex);
            int blockMinIndex = minBlock(iBlock + 1, jBlock - 1);
            return minIndex(minIndex,originalIndices[blockMinIndex]);
        }
    }
    
    /**
     * Helper function to find the block index of the minimum value 
     * for the given range of block indices  
     * @param i	starting block index
     * @param j	ending block index
     * @return block index of the minimum value between the range
     */
    public int minBlock(int i, int j) {
    	int k = (int)Math.floor(Math.log(j-i+1)/Math.log(2));
        int power_of_2 = (int)Math.pow(k,2);
        int min = minIndex(sparseTable[i][k],sparseTable[j-power_of_2+1][k]);
        return min;

    }
    
    /**
     * The Main function  
     * Prints the time processing and query time 
     * Along with the memory utilized on the console
     * @param file - the input file 
     * @throws FileNotFoundException 
     */
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
		HybridRMQ1 hrmq = new HybridRMQ1(arr);
		System.out.println(timer.end());
		
		System.out.println("");
		int i = rand.nextInt(size);
		int j = rand.nextInt(size-i) + i;
		
		System.out.println("Time for Query:");
		Timer timer2 = new Timer();
		minIndex = hrmq.rmq(i,j);
		
		System.out.println(timer2.end());
		
		System.out.println("\n");
		System.out.println("Range : "+ i +" - " + j);
		System.out.println("Index of minimum value : " + minIndex);
    	System.out.println("Minimum value : " + arr[minIndex]);
		
	}
}
