package interfaces;

import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import com.evasler.clientapp.ClientResult;
import com.evasler.clientapp.DbResult;

public interface MapWorkerInt extends WorkerInt{
	Map<String,ClientResult> map(Stream<DbResult> stream);
	/**
	 * We take a socket as input for the Bonus1 in order to have multiple clients.
	 * @param socket
	 */
    void notifyMaster(ObjectOutputStream out,UUID currentId);
    void sendToReduce(Map<String,ClientResult> topResults,UUID currentId);
}
