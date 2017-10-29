package interfaces;

import java.util.Map;
import java.util.Set;

import com.evasler.clientapp.ClientResult;

import java.io.ObjectOutputStream;
import java.util.List;

public interface ReduceWorkerInt extends WorkerInt{
    Set<ClientResult> reduce(List<Map<String,ClientResult>> listOfMaps, int k);
    void sendResults(Set<ClientResult> set, ObjectOutputStream out);
}