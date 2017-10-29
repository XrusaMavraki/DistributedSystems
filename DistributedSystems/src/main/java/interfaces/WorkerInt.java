package interfaces;

import java.net.Socket;

public interface WorkerInt {
	void initialize();
	void waitForTasksThread();
	/**
	 * Gives Connection to MapServer and ReduceServer classes 
	 */
	void handleConnection(Socket connection);
}
