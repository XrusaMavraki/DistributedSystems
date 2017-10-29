package implementations;

import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.SQLException;
import java.sql.Statement;


import com.evasler.clientapp.ClientRequestInsert;


import interfaces.CreateQueryInt;

public class DbQueryInsertImpl implements CreateQueryInt {

	
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
	
	public void createQuery(ClientRequestInsert cr) throws SQLException {
		if (connection == null) {
			init();
			
		}	
		
		if(cr.getPOI_category()==null||cr.getPOI_category().isEmpty()){
			cr.setPOI_category("not Specified");
		}
		if (cr.getPOI_name()==null||cr.getPOI_name().isEmpty()){
			cr.setPOI_name("not Specified");
		}
		
		String query=new StringBuilder("INSERT  INTO checkins(POI,POI_name,POI_category,POI_category_id,latitude,longitude,`time`,photos) VALUES(  '")
			
				.append(cr.getPOI()).append("', '")
				.append(cr.getPOI_name()).append("', '")
				.append(cr.getPOI_category()).append("', ")
				.append(Integer.toString(cr.getPOI_category_id())).append(", ")
				.append(Double.toString(cr.getLatitude())).append(", ")
				.append(Double.toString(cr.getLongitutde())).append(", ")
				.append("CURDATE(), '")
				.append(cr.getPhotos()).append("')").toString();
		Statement stm = connection.createStatement();
		stm.executeUpdate(query);
//		List<DbResult> dbResults = new ArrayList<>();
//		while (rs.next()) {
//			dbResults.add(new DbResult(rs));
//		}
		stm.close();
	
		
	}
	
	
}
