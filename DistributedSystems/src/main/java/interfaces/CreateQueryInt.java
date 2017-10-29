package interfaces;

import java.sql.SQLException;
import java.util.stream.Stream;

import com.evasler.clientapp.ClientRequest;
import com.evasler.clientapp.DbResult;

/**
 * This interface 
 * @author xrusa
 *
 */
public interface CreateQueryInt {

	static String SERVER_IP = "83.212.117.76";
	static int PORT = 3306;
	static String DB_URL = "jdbc:mysql://" + SERVER_IP + ":" + PORT + "/ds_systems_2016_omada21?user=omada21&password=omada21db";
	static String DB_CLASS = "com.mysql.jdbc.Driver";
	//Stream<DbResult> createQuery(ClientRequest cr) throws SQLException;
}
