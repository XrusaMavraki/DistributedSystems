package implementations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import interfaces.WorkerInt;

public abstract class Server implements WorkerInt {

	protected String reducer;
	protected int portno;
	/**
	 * first argument (mandatory): "m" for map server, or "r" for reduce server
	 * second argument (mandatory): path to configuration file
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main (String[] args) throws NumberFormatException, IOException {
		if (args.length < 1) {
			System.out.println("Please enter m for map server, or r for reduce server");
			System.exit(1);
		}
		Server server = null;
		if (args[0].equals("m")) {
			
			server = new MapServer();
			server.parseConfig(args[1]);
		} else if (args[0].equals("r")) {
			server = new ReduceServer();
			server.parseConfig(args[1]);
		} else if (args[0].equals("i")){
			server= new InsertServer();
			server.parseConfig(args[1]);
		}
		else {
			System.out.println("Please enter m for map server, or r for reduce server");
			System.exit(2);
		}
		
		
		server.initialize();
		server.waitForTasksThread();
	}
	public void parseConfig(String filepath) throws NumberFormatException, IOException{
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] items = line.split("=");
			String cat = items[0];
			switch(cat){
			case "reduceIps": {
				reducer = items[1];
				break;
			}
			case "port" : portno = Integer.parseInt(items[1]);
			}
		}
		br.close();
	}
	
	public void waitForTasksThread() {
		ServerSocket providerSocket = null;
		try{
			System.out.println("Listenning to : " + portno);
			providerSocket= new ServerSocket(portno);
			while (true){
				//waits for Client
				Socket connection= providerSocket.accept();
				handleConnection(connection);
				

			}
		}catch(IOException ioException) { 
			ioException.printStackTrace();
		} finally {//clossing connection
			try{ 
				providerSocket.close();
			}catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}
}
