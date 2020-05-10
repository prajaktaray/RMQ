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
    int blocks;
    float[] origElems;
    int[] origIndices;
    float[] blockMinima;
    
    private int logBase2(int num) {
        return (int)(Math.log(num) / Math.log(2));
    }
    /**
     * Creates a new HybridRMQ structure to answer queries about the
     * array given by elems.
     *
     * @elems The array over which RMQ should be computed.
     */
    public HybridRMQ(float[] elems) {
        origElems = elems;
        int arrLength = elems.length;
        if ( arrLength <= 1) {
            return;
        }
        
        blockSize = (int)(Math.log(arrLength) / Math.log(2)); 
        blocks = (int) Math.ceil((double)(arrLength)/blockSize);
        
        initializeBlockMinArr();       	
        
        blockMinRMQ = new SparseTableRMQ(blockMinima);
    }
    
    
    
    private void initializeBlockMinArr() {
    	
    	blockMinima =new float[blocks];
    	origIndices = new int[blocks];
        int block = 0;
        float min = origElems[0];
        int minIndex = 0;
        for (int i = 1; i < origElems.length; i++) {
            if (i % blockSize == 0) {
            	blockMinima[block] = min;
                origIndices[block] = minIndex;
                block++;
                min = origElems[i];
                minIndex = i;
            }
            
            minIndex = minIndex(i,minIndex);
            min = origElems[minIndex];
        }
        blockMinima[block] = min;
        origIndices[block] = minIndex;
		return;
		
	}
    
    private int minIndex(int index1, int index2) {
		if (origElems[index1] <= origElems[index2])
			return index1;
		else
			return index2;
		
	}    
    
	private int linearSearch(int i, int j) {
        int minIndex = i;
        float min = origElems[i];
        for (int k = i + 1; k <= j; k++) {
            if (origElems[k] < min) {
                min = origElems[k];
                minIndex = k;
            }
        }
        return minIndex;
    }

    /**
     * Evaluates RMQ(i, j) over the array stored by the constructor, returning
     * the index of the minimum value in that range.
     */
    @Override
    public int rmq(int i, int j) {
        if (i == j) {
            return i;
        }
        
        // Use integer division to round down
        int iBlock = i / blockSize;
        int jBlock = j / blockSize;
        
        // If i and j are in the same block or adjacent blocks
        if ((jBlock - iBlock) <= 1) {
            return linearSearch(i, j);
        }
        else {
            int iBlockEnd = ((iBlock + 1) * blockSize) - 1;
            int jBlockStart = jBlock * blockSize;
            int iInd = linearSearch(i, iBlockEnd);
            int jInd = linearSearch(jBlockStart, j);
            
            float min;
            int minIndex;
            
            minIndex = minIndex(iInd,jInd);
            min = origElems[minIndex];
            
            int blockMinIndex = minBlock(iBlock + 1, jBlock - 1);
            return minIndex(minIndex,origIndices[blockMinIndex]);
            
        }
    }
    
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
