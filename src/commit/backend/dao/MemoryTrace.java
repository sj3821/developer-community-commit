package commit.backend.dao;

public class MemoryTrace {
	public static void trace() { 
		System.out.println("Total Memory : " + Runtime.getRuntime().totalMemory()); 
		System.out.println("Free Memory : " + Runtime.getRuntime().freeMemory()); 
		System.out.println("Used Memory : " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())); 
		}

}
