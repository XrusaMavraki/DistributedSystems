package implementations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import com.evasler.clientapp.ClientRequest;
import com.evasler.clientapp.ClientResult;

public class TestMain {
	public static List<String> MapIps= new ArrayList<String>();
	public static List<String> ReduceIps= new ArrayList<String>();
	public static int port;
	public static List<UUID> myIds =new ArrayList<UUID>();
	public static void main(String [] args) throws ParseException, SQLException, UnknownHostException, IOException, ClassNotFoundException{
		double x1;
		double y1;
		double x2;
		double y2;
		String dateFrom;
		String dateTo;
	
		
		int kTop;
		if (args.length == 8) {
			x1 = Double.parseDouble(args[0]);
			y1 = Double.parseDouble(args[1]);
			x2 = Double.parseDouble(args[2]);
			y2 = Double.parseDouble(args[3]);
			dateFrom = args[4];
			dateTo = args[5];
			kTop = Integer.parseInt(args[6]);
			String file= args[7];
			parseConfig(file);
			double division = Math.abs(y2 - y1) / MapIps.size();
			ClientRequest[] crArray = new ClientRequest[MapIps.size()];
			for(int i=0;i<MapIps.size();i++){
				crArray[i]= new ClientRequest(x1, y1+i*division, x2,y2-(MapIps.size()-i -1)*division,dateFrom,dateTo);
			}
			
			IntStream.range(0, MapIps.size()).parallel().forEach((i) -> {
				try {
					String[] ipAndPort = MapIps.get(i).split(":");
					initSocketMap(ipAndPort[0], Integer.parseInt(ipAndPort[1]), crArray[i]);
				} catch (Exception e) {

					e.printStackTrace();
				}
			});
			//hardcoded get0 because right now we only have one reducer
			String[] ipAndPort = ReduceIps.get(0).split(":");
			Set<ClientResult> topResults = initSocketReduce(ipAndPort[0], Integer.parseInt(ipAndPort[1]), kTop);

			for (ClientResult clientResult : topResults) {
				System.out.println(clientResult);
			}
		} else {
			System.out.println("Wrong number of arguments.");
		}
		System.exit(0);
		
	}
	public static void parseConfig(String configpath) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(configpath));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] items = line.split("=");
			String cat = items[0];
			String[] values = items[1].split(",");
			switch(cat){
			case "mapIPs": {
				for(String value : values){
					MapIps.add(value);
				}
				break;
			}
			case "reduceIps": {
				for(int i=1; i<items.length;i++){
					ReduceIps.add(items[i]);
				}
				break;
			}
			case "port" : port = Integer.parseInt(items[1]);
			}
			
		}
		br.close();
	}
	
	public static void initSocketMap(String IP,int port,ClientRequest cr) throws UnknownHostException, IOException 	{
		Socket socket = new Socket(IP,port);
		ObjectOutputStream out=null;
		ObjectInputStream in = null;
		try {
			
			out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(cr);
			in = new ObjectInputStream(socket.getInputStream());
			myIds.add((UUID)in.readObject());
		}
		catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e)	{
				e.printStackTrace();
			}
		}
	}
	
	public static Set<ClientResult> initSocketReduce(String IP, int port, int kTop)	throws UnknownHostException, IOException, ClassNotFoundException {

		Socket reducerSocket = new Socket(IP, port); // reducer
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		Set<ClientResult> reduced = null;
		try {
			out = new ObjectOutputStream(reducerSocket.getOutputStream());
			in = new ObjectInputStream(reducerSocket.getInputStream());
			out.writeObject(myIds);
			out.writeObject(new Integer(kTop));
			reduced = (Set<ClientResult>) in.readObject();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reducerSocket.getOutputStream().flush();
				
				reducerSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return reduced;
	}
}