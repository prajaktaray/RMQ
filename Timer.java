package pxr180025;
/**
 * 
 * Short Project SP08: Hashing 
 * @author Harshita Rastogi (hxr190001) 
 * 		   Prajakta Ray (pxr180025)
 * 
 * 
 */

/**
 * Timer class for roughly calculating running time of programs
 *
 */

public class Timer {
	long startTime, endTime, elapsedTime, memAvailable, memUsed;

	public Timer() {
		startTime = System.currentTimeMillis();
	}

	public void start() {
		startTime = System.currentTimeMillis();
	}

	public Timer end() {
		endTime = System.currentTimeMillis();
		elapsedTime = endTime - startTime;
		memAvailable = Runtime.getRuntime().totalMemory();
		memUsed = memAvailable - Runtime.getRuntime().freeMemory();
		return this;
	}

	public String toString() {
		return "Time: " + elapsedTime + " msec.\n" + "Memory: " + (memUsed / 1048576) + " MB / "
				+ (memAvailable / 1048576) + " MB.";
	}

}
