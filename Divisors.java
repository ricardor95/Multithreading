public class Divisors { 

	int a = 1;
	int b = 100;
	int k = 3;
	int SIZE = b - a + 1;
	int numberOfThreads = 8;
	public volatile int numbers[] = new int[SIZE];
	public volatile boolean numbersInit = false;
	public volatile int numberOfDivisors[] = new int[SIZE];
	public volatile int currentNumber = 0;
	public volatile boolean numberTested[] = new boolean[SIZE];
	public volatile boolean finished = false;
	public volatile int topK[] = new int[k];
	public volatile boolean topKInit = false;
	public volatile boolean topKComplete = false;

	// Thread used to initiate arrays
	class T1 implements Runnable 
	{
		public synchronized void populateNumberArrays()
		{
			if (numbersInit == false)
			{	
				numbersInit = true;

				int j = 0;
			
				for (int i = a; i <= b; i++) {
					numbers[j] = i;
					numberTested[j] = false;
					j++;
				}
			}
		}
		public synchronized void initialteTopKArray()
		{
			if (topKInit == false)
			{	
				topKInit = true;
				for (int i = 0; i < k; i++) {
					topK[i] = -1;
				}
				
			}
			
			
			
		}
		@Override
		public void run()
		{						
			populateNumberArrays();
			initialteTopKArray();
		}
	}	
	
    // Thread used to determinate number of divisors
	class T2 implements Runnable 
	{
	
		public synchronized void checkNumber()
		{
				if (numberTested[currentNumber] == false)
				{
					numberTested[currentNumber] = true;	
					numberOfDivisors[currentNumber] = findDivisors(numbers[currentNumber]);

					if (currentNumber == SIZE - 1)
					{
						finished = true;
					} else
					{
						currentNumber++;
					}
				}
			
		}
		@Override
		public void run()
		{			
			while (finished == false) {
				checkNumber();
				
				try {
					Thread.sleep(0, 1);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
			}
		}
	}
	
    // Thread used to determinate Top K numbers that have the largest number of divisors
	class T3 implements Runnable 
	{
		public synchronized void findTopK() 
		{

			if (finished && !topKComplete) {

				int MaxNumberAccepted= 81; 	 // Initiated to max possible value
				int IndexOfMax = -1;
				for (int j = 0; j < k; j++) {
					int MaxNumberOfDivisors = 0; // Initiated to the smallest possible value
					for (int i = 0; i < numberOfDivisors.length; i++) {
						
						if (MaxNumberOfDivisors < numberOfDivisors[i] && MaxNumberAccepted > numberOfDivisors[i]) {
							MaxNumberOfDivisors = numberOfDivisors[i];
							IndexOfMax = i;
						}
						
					}
					topK[j] = IndexOfMax;
					MaxNumberAccepted = numberOfDivisors[IndexOfMax];
				}
				
				topKComplete = true;
				
				System.out.print("Top k numbers that have the largest number of divisors: ");

				for (int j = 0; j < topK.length; j++) {
					System.out.print(numbers[topK[j]] + " ");
				}
				System.out.println(" ");

				
				System.out.print("Respective number of divisors for Top K numbers: ");
				
				for (int j = 0; j < topK.length; j++) {
					System.out.print(numberOfDivisors[topK[j]] + " ");
				}
				System.out.println(" ");


			}
			/*
			else
			{
				System.out.println("Denied access to FindTopK -" + " id: " + Thread.currentThread().getId());
			}
			*/
		}
		
		@Override
		public void run()
		{			
			while ( !topKComplete ) {
				findTopK();
			}	
		} 
	}
	
	// method to find divisors 
    static int findDivisors(int n) 
    { 
    	int NumDivisors = 0;
        for (int i=1;i<=n;i++) 
            if (n%i==0) 
                NumDivisors++;
        return NumDivisors;
    } 
    
    private void test() 
    {
    	// Thread used to initiate arrays
        T1 InitArrays = new T1();
        Thread object1 = new Thread(InitArrays); 
        object1.start();
        
        // One or multiple Threads to determinate number of divisors
        T2 CheckNumbers = new T2();
        for (int i=0; i<numberOfThreads; i++) 
        { 
            Thread object = new Thread(CheckNumbers); 
            object.start();
        } 
        
        // Thread used to determinate Top K numbers that have the largest number of divisors
        T3 FindTopK = new T3();
        Thread object3 = new Thread(FindTopK); 
        object3.start();
          
    }
    
	public static void main(String args[]) throws InterruptedException  {
		
	    long startTime = System.nanoTime();

		try {        
			Divisors div = new Divisors();
			div.test();						// Use to create and start threads
			
		} catch (Exception  e) {
				System.out.println("Main thread Interrupted");
		}
		System.out.println("Main thread exiting.");
		
		long stopTime = System.nanoTime();
		long elapsedTime = stopTime - startTime;
		System.out.println("Elapsed time: " + elapsedTime + " nanoseconds.");
	}
	
}
	
	


