package commit.backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class VisitCountDao {
	private Connection getConnection() throws Exception {

		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/dbcp");

		return ds.getConnection();

	}


	public void setVisitTotalCount() throws Exception {
		String sql = "INSERT INTO VISIT (V_DATE) VALUES (sysdate)";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);	
				){
			int result = pstat.executeUpdate();
		}
	}
	
	public int getVisitTotalCount() throws Exception{
		String sql = "select count(*) from visit";
		try(Connection con = this.getConnection();
			PreparedStatement pstat = con.prepareStatement(sql);	
			ResultSet rs = pstat.executeQuery();
			){
			
			rs.next();
			return rs.getInt(1);
		}
	}
	
	
	public int getVisitTodayCount() throws Exception{
		String sql = "select count(*) from visit where to_char(v_date,'yy/MM/dd') = to_char(sysdate,'yy/MM/dd')";
		try(Connection con = this.getConnection();
			PreparedStatement pstat = con.prepareStatement(sql);	
			ResultSet rs = pstat.executeQuery();){
			rs.next();
			return rs.getInt(1);
		}
	}

}
