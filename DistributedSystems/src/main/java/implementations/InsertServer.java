package implementations;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;

import com.evasler.clientapp.ClientRequest;
import com.evasler.clientapp.ClientRequestInsert;

public class InsertServer  extends Server{

	@Override
	public void initialize() {
		System.out.println("Insert Server Initialization...");
		
	}

	@Override
	public void handleConnection(Socket connection) {
		new Thread(() -> {
			try {
				ObjectOutputStream out= new ObjectOutputStream(connection.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
				Object obj = in.readObject();
				ClientRequestInsert cr = (ClientRequestInsert) obj;
				System.out.println("Got Request from client");
				(new DbQueryInsertImpl()).createQuery(cr);
			} catch (IOException | ClassNotFoundException | SQLException e) {
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
