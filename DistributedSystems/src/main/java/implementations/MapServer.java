package implementations;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.evasler.clientapp.*;

import interfaces.MapWorkerInt;


public class MapServer extends Server implements MapWorkerInt {
	
	
	@Override
	public void initialize() {
		System.out.println("Map Server Initialization...");
		
	}

	@Override
	public Map<String, ClientResult> map(Stream<DbResult> stream) {
		// we map each DbResult to the constructor of the Client Result class.
		Collector<DbResult, ClientResult, ClientResult> collector = Collector.of(
				ClientResult::new, //supplier , the constructor of ClientResult creates an instance of the accumulator 
				(cr, dbr) -> cr.setFromDbResult(dbr), // this is how the accumulator will react to a dbResult from the stream
				(cr1, cr2) -> {cr1.addCheckins(cr2.getCheckins()); cr1.addPictures(cr2.getPictureUrls()); return cr1;});  // this is essentially a combiner-reducer, we combine all the accumulators to create a single instance of ClientResult
		Map<DbResult, ClientResult> results = stream.collect(Collectors.groupingBy(Function.identity(), collector)); //function.identity() tells us that the key of the map should be the object itself (DbResult)
		return results.entrySet().parallelStream().map(entry -> entry.getValue())
				.collect(Collectors.toConcurrentMap(ClientResult::getPoi, Function.identity())); // we convert it to a Map<String,ClientResult>
	}
	
	
	
	@Override
	/**
	 * this notifies the client, when all mappers are done client sends request to reducer
	 * We just send a 0 to notify the client that the work of this map server is completed.
	 */
	public void notifyMaster(ObjectOutputStream out,UUID currentId) {
		try {
			out.writeObject(currentId);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	//hardcoded 0 because now we only have 1 reducer
	public void sendToReduce(Map<String,ClientResult> topResults,UUID currentId) {
		String [] ipAndPort=reducer.split(":");
		try (Socket socket = new Socket(ipAndPort[0],Integer.parseInt(ipAndPort[1]))) {

			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			socket.getInputStream();
			oos.writeObject(topResults);
			oos.writeObject(currentId);
			oos.flush();
			in.readObject(); 
			//oos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void handleConnection(Socket connection) {
		//Lambda expression to start a new thread
		new Thread(() -> {
		try {
			ObjectOutputStream out= new ObjectOutputStream(connection.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
			UUID currentId= UUID.randomUUID();
			System.out.println("UUID: "+ currentId.toString());
			Object obj = in.readObject();
			ClientRequest cr = (ClientRequest) obj;
			System.out.println("Got Request from client");
			Stream<DbResult> results = (new DbQueryImpl()).createQuery(cr);
			System.out.println("Got Reesults from db");
			Map<String,ClientResult> topResults = map(results);
			System.out.println("Generated top results");
			sendToReduce(topResults,currentId);
			System.out.println("Sent to reducer!");
			notifyMaster(out,currentId);
			System.out.println("Notified Master");
		}
		catch (IOException | ClassNotFoundException | SQLException e) 
		{
			e.printStackTrace();
		}
		finally{
		try {
			connection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		}).start();
		
	}
	
	

}
