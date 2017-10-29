package interfaces;

public interface AndroidClientInt {

	void distributeToMappers();
	void waitForMappers();
	void ackToReducers();
	void collectDataFromReducers();
	
}
