package commit.backend.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import commit.backend.dto.MemberSearchBoardDto;
import commit.backend.dto.findLangDto;

public class MemberSearchBoardDao {
	private Connection getConnection() throws Exception {
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/dbcp");
		return ds.getConnection();
	}


	public List<findLangDto> findlang() throws Exception {
		List<findLangDto> list = new ArrayList();

		String sql = "select user_id, listagg(language,',') WITHIN GROUP(ORDER BY language) lang from language group by user_id";
		try(
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				){
			ResultSet rs = pstat.executeQuery();
			while(rs.next()) {
				String user_id = rs.getString("user_id");
				String lang = rs.getString("lang");
				list.add(new findLangDto(user_id,lang));
			}
		}
		return list;
	}


	public List<MemberSearchBoardDto> select(String name, String[] languages, String etcLanguage, String career, String start_date, String end_date) throws Exception{
		List<MemberSearchBoardDto> list = new ArrayList();
		String sql;
		System.out.println(name);
		if((languages==null) && (etcLanguage.contentEquals("")) && (name.contentEquals("")) && (career.contentEquals("")) && (start_date.contentEquals(""))) {
			sql = "select distinct m.id, m.name, m.email, m.career, a.start_date, a.end_date from member M left outer join language L on M.id=L.user_id left outer join member_activity A on M.id=A.user_id where M.id not in ('sysop')";
		}else {
			int num = 0;
			sql = "select distinct m.id, m.name, m.email, m.career, a.start_date, a.end_date from member M left outer join language L on M.id=L.user_id left outer join member_activity A on M.id=A.user_id where ";

			String tmp="";
			if(!(languages==null)) {
				tmp=" L.language like '%" + languages[0] + "%'";
				for(int i = 1 ; i < languages.length ; i++) {
					tmp += " or L.language like '%" + languages[i] +"%'";
				}
			}
			if(!(etcLanguage.contentEquals(""))) {
				String str = "'%" + etcLanguage + "%'";
				if(tmp.contentEquals("")) {
					tmp+= " L.language like " + str;
				}else {
					tmp+= "or L.language like " + str;
				}
			}
			if(!tmp.contentEquals("")) {
				tmp="("+tmp+")";
			}
			sql+=tmp;

			String tmp1="";
			if(!(career.contentEquals(""))) {
				if(tmp.contentEquals("")) {
					String str = "'%" + career + "%'";
					tmp1 = " m.career = " + str;
					sql+= tmp1;
				}else {
					String str = "'%" + career + "%'";
					tmp1 = "and m.career = " + str;
					sql+= tmp1;
				}
				String str = "'%" + career + "%'";
				tmp1 = " m.career = " + str;
				sql+= tmp1;
			}
			String tmp2="";
			if(!(start_date.contentEquals(""))){
				if(tmp.contentEquals("") && tmp1.contentEquals("")) {
					tmp2 = " (a.start_date >=to_date('" + start_date+ "')and a.end_date <=to_date('"+end_date +"'))";
					sql+= tmp2;
				}else {
					tmp2 = " and  (a.start_date >=to_date('" + start_date+ "') and a.end_date <=to_date('"+end_date +"'))";
					sql+= tmp2;
				}

			}
			String tmp3="";
			if(!(name.contentEquals(""))){
				System.out.println("ho~~~");
				if(tmp.contentEquals("") && tmp1.contentEquals("") &&tmp2.contentEquals("")) {
					System.out.println("wow!");
					tmp3 = "m.name like '%"+name +"%'";
					sql += tmp3;
				}else {
					System.out.println("wowow!");
					tmp3 = "and m.name like '%"+name+"%'";
					sql += tmp3;
				}
			}
			String notin = "";
			if(tmp.contentEquals("") && tmp1.contentEquals("") && tmp2.contentEquals("") && tmp3.contentEquals("")) {
				notin = "M.id not in ('sysop')";
			}else {
				notin = "and M.id not in ('sysop')";
			}

			sql+=notin;
		}


		System.out.println(sql);

		try(
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				){


			ResultSet rs = pstat.executeQuery();

			while(rs.next()) {
				String id = rs.getString("id");
				String namers = rs.getString("name");
				String email = rs.getString("email");

				String scareer = rs.getString("career");
				Date sstart_date = rs.getDate("start_date");
				Date send_date = rs.getDate("end_date");
				list.add(new MemberSearchBoardDto(id,namers,email,scareer,sstart_date,send_date));
			}
		}

		return list;
	}




}