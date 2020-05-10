package hxr190001;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class HybridRMQ1 {
	int blockSize;		
    int blocks;					// number of blocks
    float[] originalArray;		// original input array
    int[] originalIndices;		// original indices of elements of input array
    float[] blockMinima;
    int [][] sparseTable;
    int len;

    private int minIndex(int index1, int index2) {
		if (originalArray[index1] <= originalArray[index2])
			return index1;
		else
			return index2;
		
	} 
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
    
    void SparseTableRMQ(){
    	if (blockMinima.length==0) return; 
		int len = blockMinima.length;
		originalArray = new float[len];
		System.arraycopy(blockMinima, 0, originalArray, 0, len);
		
//		// Calculate logs and powers up front
//		CalculateLogs(len);
//		powerCalculator(len);
		
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
        
        SparseTableRMQ();
    }

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
            
            int minIndex;
            
            minIndex = minIndex(iIndex,jIndex);
            
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
    	int k = (int)Math.floor(Math.log(j-i+1)/Math.log(2));
        int power_of_2 = (int)Math.pow(k,2);
        int min = minIndex(sparseTable[i][k],sparseTable[j-power_of_2+1][k]);
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
		HybridRMQ1 hrmq = new HybridRMQ1(arr);
		System.out.println(timer.end());
		
		System.out.println("");
		int i = rand.nextInt(size);
		//int j = rand.nextInt(size);
		int j = rand.nextInt(size-i) + i;
		
		System.out.println("Time for Query:");
		Timer timer2 = new Timer();
		minIndex = hrmq.rmq(2,4);
		
		System.out.println(timer2.end());
		
		System.out.println("\n");
		System.out.println("Range : "+ i +" - " + j);
		System.out.println("Index of minimum value : " + minIndex);
    	System.out.println("Minimum value : " + arr[minIndex]);
		
	}
}
