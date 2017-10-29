package implementations;

import interfaces.ReduceWorkerInt;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.evasler.clientapp.ClientResult;

public class ReduceServer extends Server implements ReduceWorkerInt {

	Map<UUID,Map<String, ClientResult>> inputs;

	@Override
	public void initialize() {
		System.out.println("Reduce Server Initialization...");

		// we make the inputs list synchronized to handle threads adding to the
		// list at the same time.
		inputs = Collections.synchronizedMap(new HashMap<>());

	}

	@Override
	public Set<ClientResult> reduce(List<Map<String, ClientResult>> listOfMaps, int kTop) {

		Optional<Map<String, ClientResult>> res = listOfMaps.parallelStream().reduce((list1, list2) -> {
			list2.forEach((k, v) -> {
				list1.merge(k, v, (cr1, cr2) -> {
					cr1.addCheckins(cr2.getCheckins());
					cr1.addPictures(cr2.getPictureUrls());
					return cr1;
				});
			});
			return list1;
		});
		
		if (!res.isPresent()) {
			return null;
		}
		Map<String, ClientResult> results = res.get();

		Comparator<ClientResult> cp = Comparator.comparingInt(ClientResult::getCheckins).reversed();
		Supplier<HashSet<ClientResult>> supl = () -> new LinkedHashSet<ClientResult>();
		Set<ClientResult> r = results.values().parallelStream().sorted(cp).limit(kTop).collect(Collectors.toCollection(supl));
		return r;
	}

	@Override
	public void sendResults(Set<ClientResult> set, ObjectOutputStream out) {

		try {
			out.writeObject(set);
			out.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void handleConnection(Socket connection) {
		new Thread(() -> {
		try {
			ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
			Object obj = in.readObject();

			if (obj instanceof List<?>) {
				// we received a connection from the client
				// the int the client will send will be the k top he requires
				List<UUID> clientIds=(List<UUID>)obj;
				List<Map<String,ClientResult>> currentClients =new ArrayList<>();
				for( UUID o : clientIds){
					currentClients.add(inputs.remove(o)); // pairnei kai aferei
					
				}
				int kTop=(int)in.readObject();
				Set<ClientResult> reduced = reduce(currentClients, kTop);
				sendResults(reduced, out);
				System.out.println("Sent them to client ,"+currentClients +kTop + reduced);
				in.read();
			}
			// edo to allo connection an einai sosto auto p kano
			else if (obj instanceof Map<?, ?>) {
				UUID clientId=(UUID)in.readObject();
				inputs.put(clientId, (Map<String,ClientResult>)obj);
				out.writeObject(new Integer(0));
				System.out.println("Got Results from mapper!");
				// if not integer then we put it in the input list of maps
			} else {
				System.out.println("PROBLEM");
			}

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
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