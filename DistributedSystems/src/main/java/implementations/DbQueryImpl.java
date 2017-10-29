package implementations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.evasler.clientapp.ClientRequest;
import com.evasler.clientapp.DbResult;

import interfaces.CreateQueryInt;

public class DbQueryImpl implements CreateQueryInt{

	private static Connection connection;
	private static void init() {
		try {
			Class.forName(DB_CLASS);
			connection = DriverManager.getConnection(DB_URL);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	public Stream<DbResult> createQuery(ClientRequest cr) throws SQLException {
		if (connection == null) {
			init();
		}			
		String query= new StringBuilder("Select * from checkins where checkins.longitude > ")
				.append(Double.toString(cr.getX1())).append(" and checkins.latitude > ")
				.append(Double.toString(cr.getY1())).append(" and checkins.longitude < ")
				.append(Double.toString(cr.getX2())).append(" and checkins.latitude < ")
				.append(Double.toString(cr.getY2())).append(" and checkins.time > '")
				.append(cr.getDateFrom()).append("' and checkins.time < '")
				.append(cr.getDateTo()).append("'").toString();
		Statement stm = connection.createStatement();
		ResultSet rs = stm.executeQuery(query);
		List<DbResult> dbResults = new ArrayList<>();
		while (rs.next()) {
			dbResults.add(new DbResult(rs));
		}
		stm.close();
		rs.close();
		return dbResults.parallelStream();
	}
	

}
