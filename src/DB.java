import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.fabric.xmlrpc.base.Array;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;


public class DB {
	private Connection conn;
	private Statement st;
	private ResultSet rs;
	
	public DB(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/sexshops_domains", "root", "1234");
			st = (Statement) conn.createStatement();
		} catch (Exception e) {
			System.out.println("Db Error");
		}
	}
	
	
	public ArrayList<String> getData() throws SQLException{
		ArrayList<String> domains = new ArrayList<String>();
		String query = "select domain from domain_info";
		rs = st.executeQuery(query);
		while(rs.next()){
			String domain = rs.getString("domain");
			domains.add(domain);			
		}
		
		return domains;
	}
	
	public void record(ArrayList<String> domains) throws SQLException{
		for(String domain : domains){
			// Check if exists 
			String check_query = "select domain from domain_info where domain='"+domain+"'";
			rs = st.executeQuery(check_query);
			int num_rows = rs.getRow();
			if(num_rows == 0){
				// Adding domain
				String query = "insert into domain_info (domain, info) VALUES ('"+domain+"', 'none')";
				st.executeUpdate(query);
			}
		}
	}
}
