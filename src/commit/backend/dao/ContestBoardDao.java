package commit.backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import commit.backend.dto.ContestDto;
import commit.backend.statics.Configuration;


public class ContestBoardDao {
	//Ŀ�ؼ�
	private Connection getConnection() throws Exception {
		Context ctx = new InitialContext();
		DataSource dsa = (DataSource)ctx.lookup("java:comp/env/dbcp");
		return dsa.getConnection();

	}


	//get방식으로 가져올 공모전 추가 도메인
	private String getContestDomainOption(String[] field , String name) {
		String getted = "";
		if(field != null &&(!field[0].equals(""))) {
			getted += "(content_field like '%" + field[0] + "%'";
			for(int i = 1 ; i < field.length ; i++) {
				if(!field[i].equals("")) {
					getted += " or content_field like '%" + field[i] + "%'";
				}	
			}
			getted += ")";
		}
		if(name != null && (!name.equals(""))) {
			if(!getted.equals("")) {
				getted += " and ";
			}
			getted += "title like '%" + name + "%'";
		}
		if(!getted.equals("")) {
			getted = " where " + getted;
		}
		return getted;
	}

	// 공모전 개수 탐색(상세검색 포함)
	public int getContestCount(String getted) throws Exception {
		String sql = "select count(*) from CrawlingSite"+getted;
		try(Connection con =this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				ResultSet rs = pstat.executeQuery();){
			rs.next();
			return rs.getInt(1);
		}
	}

	//공모전 게시판 불러오기(상세검색 포함)
	public List<ContestDto> selectAllContest(int page , String[] field , String name) throws Exception{
		String getted = "";
		if(field != null &&(!field[0].equals(""))) {
			getted += "(content_field like '%" + field[0] + "%'";
			for(int i = 1 ; i < field.length ; i++) {
				if(!field[i].equals("")) {
					getted += " or content_field like '%" + field[i] + "%'";
				}	
			}
			getted += ")";
		}
		if(name != null && (!name.equals(""))) {
			if(!getted.equals("")) {
				getted += " and ";
			}
			getted += "title like '%" + name + "%'";
		}
		if(!getted.equals("")) {
			getted = " where " + getted;
		}
		System.out.println(getted);

		int currentPage = page;
		int recordTotalCount = this.getContestCount(this.getContestDomainOption(field , name));


		int pageTotalCount = 0;
		if(recordTotalCount % Configuration.ContestrecordCountPerPage > 0) {
			pageTotalCount = recordTotalCount/Configuration.ContestrecordCountPerPage+1;
		}else {
			pageTotalCount = recordTotalCount/Configuration.ContestrecordCountPerPage;
		}

		if(currentPage < 1) {
			currentPage = 1;
		}else if(currentPage > pageTotalCount) {
			currentPage=pageTotalCount;
		}

		int start = currentPage*Configuration.ContestrecordCountPerPage - 
				(Configuration.ContestrecordCountPerPage-1);
		int end = start + (Configuration.ContestrecordCountPerPage-1);

		String sql ="select * from "
				+ "(select CrawlingSite.*, row_number() "
				+ "over(order by CrawlingSite.start_date desc) rnum "
				+ "from CrawlingSite"+getted+") where rnum between ? and ?";
		try (Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setInt(1, start);
			pstat.setInt(2, end);
			try(ResultSet rs = pstat.executeQuery();){
				List<ContestDto> list = new ArrayList<>();
				while(rs.next()) {
					ContestDto dto = new ContestDto(rs.getString("img_poster"),rs.getString("title"),rs.getString("organize"),
							rs.getDate("start_date"),rs.getDate("end_date"),
							rs.getString("direct_url"),rs.getString("content_field").split("/|,| "));
					list.add(dto);
				}
				return list;
			}			
		}
	}




	//공모전 네비 가져오기(상세검색 포함)
	public String getContestNavi(int currentPage ,String[] field , String name) throws Exception{

		String getted = "";
		if(field != null &&(!field[0].equals(""))) {
			getted += "field=" + field[0];
			for(int i = 1 ; i < field.length ; i++) {
				if(!field[i].equals("")) {
					getted += "&field=" + field[i];
				}	
			}
			getted += "&";
		}
		if(name != null && (!name.equals(""))) {
			getted += "name="+ name+"&";
		}

		int recordTotalCount = this.getContestCount(this.getContestDomainOption(field , name)); 


		int pageTotalCount = 0;

		if(recordTotalCount % Configuration.ContestrecordCountPerPage > 0) {
			pageTotalCount = recordTotalCount / Configuration.ContestrecordCountPerPage + 1;
		}
		else {
			pageTotalCount = recordTotalCount / Configuration.ContestrecordCountPerPage;
		}

		if(currentPage<1) {
			currentPage=1;
		}else if(currentPage>pageTotalCount) {
			currentPage = pageTotalCount;
		}

		int startNavi = (currentPage-1)/Configuration.naviCountPerPage*Configuration.naviCountPerPage + 1;	
		int endNavi = startNavi+(Configuration.naviCountPerPage-1);

		if(endNavi > pageTotalCount) {
			endNavi = pageTotalCount;
		}

		boolean needPrev = true;
		boolean needNext = true;

		StringBuilder sb = new StringBuilder();


		if(startNavi == 1) {
			needPrev = false;
		}

		if(endNavi == pageTotalCount){
			needNext = false;
		}

		if(needPrev) {
			sb.append("<li class=\"page-item\">");
			sb.append("<a class=\"page-link\" href=\"contest.board?"+getted+"page=" + (startNavi-1) + "\">Previous</a>"); 
			sb.append("</li>");
		}else {
			sb.append("<li class=\"page-item disabled\">");
			sb.append("<a class=\"page-link\">Previous</a>"); 
			sb.append("</li>");
		}

		for(int i= startNavi; i <= endNavi; i++) {
			if(currentPage==i) {
				sb.append("<li class=\"page-item active\" aria-current=\"page\">");
				sb.append("<span class=\"page-link\">" + i + "<span class=\"sr-only\">(current)</span></span></li>");

			}else {
				sb.append("<li class=\"page-item\">");
				// �Խ��� ȣ�� dao �� �Ѿ��
				sb.append("<a class=\"page-link\" href=\"contest.board?"+getted+"page="+ i +"\">" + i + "</a></li>");
			}
		}

		if(needNext) {
			sb.append("<li class=\"page-item\">");
			// �Խ��� ȣ�� dao �� �Ѿ��
			sb.append("<a class=\"page-link\" href=\"contest.board?"+getted+"page=" + (endNavi+1) + "\">Next</a>"); 
			sb.append("</li>");
		}else {
			sb.append("<li class=\"page-item disabled\">");
			sb.append("<a class=\"page-link\">Next</a>"); 
			sb.append("</li>");
		}

		return sb.toString();
	}






}
