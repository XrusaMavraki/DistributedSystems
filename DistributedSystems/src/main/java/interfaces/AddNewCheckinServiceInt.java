package interfaces;

public interface AddNewCheckinServiceInt { 

	void initialize();
	void waitForNewCheckinsThread();
	void insertCheckingToDatabase(Object x);
	void askToClient();  
}

