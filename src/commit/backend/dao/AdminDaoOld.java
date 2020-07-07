package commit.backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import commit.backend.dto.AdminBoardDto;
import commit.backend.dto.AdminMemberDto;
import commit.backend.statics.Configuration;


public class AdminDaoOld {
	
	
	//상빈씨 파트 시작
	
	String[] boardSortArr = {"type desc , seq " , "report_count" , "writer" , "write_date"};
	   String[] memberSortArr = {"id " , "report_count" , "name" , "regist_date"};
	   //연결
	   private Connection getConnection() throws Exception {
	      Context ctx = new InitialContext();
	      DataSource dsa = (DataSource)ctx.lookup("java:comp/env/dbcp");
	      return dsa.getConnection();

	   }
	   //게시판 글 개수 총 출력
	   public int getBoardCount(int i) throws Exception {
	      String sql = "select count(*) from ";
	      if(i == 0) {
	         sql += "(select seq from noticeboard union all select seq from qnaboard)";
	      }else {
	         sql += "member";
	      }
	      try(Connection con =this.getConnection();
	            PreparedStatement pstat = con.prepareStatement(sql);
	            ResultSet rs = pstat.executeQuery();){
	         rs.next();
	         return rs.getInt(1);
	      }
	   }

	   // 게시글 삭제 시 회원의 신고수 증가
	   public void addMemberReportCount(String table, List<String> seq) throws Exception {
	      String sql ="update member set report_count=report_count+1 "
	            + "where id=(select writer from "+table+" where seq=?)";
	      try (Connection con = this.getConnection();){
	         try(PreparedStatement pstat = con.prepareStatement(sql);){
	            for(int i = 0 ; i < seq.size() ; i++) {
	               pstat.setString(1, seq.get(i));
	               pstat.executeUpdate();
	               pstat.clearParameters();
	            }
	            con.close();
	         }
	      }
	   }
	   
	   //게시판 글 삭제
	   public void deleteBoard(String table , List<String> seq) throws Exception {
	      String sql ="delete from "+table+" where ";
	      if(table.equals("member")) {
	         sql+="id=?";
	      }else {
	         sql+="seq=?";
	      }
	      try (Connection con = this.getConnection();){
	         try(PreparedStatement pstat = con.prepareStatement(sql);){
	            for(int i = 0 ; i < seq.size() ; i++) {
	               pstat.setString(1, seq.get(i));
	               pstat.executeUpdate();
	               pstat.clearParameters();
	            }
	            con.close();
	         }
	      }

	   }


	   //게시글 관리 네비 가져오기(상세검색 포함)
	   public String getNavi(int currentPage , int sort , String domain) throws Exception{

	      int recordTotalCount; 
	      
	      if(domain.equals("toBoard.admin")) {
	         recordTotalCount = this.getBoardCount(0);
	      }else {
	         recordTotalCount = this.getBoardCount(1);         
	      }


	      int pageTotalCount = 0;

	      if(recordTotalCount % Configuration.recordCountPerPage > 0) {
	         pageTotalCount = recordTotalCount / Configuration.recordCountPerPage + 1;
	      }
	      else {
	         pageTotalCount = recordTotalCount / Configuration.recordCountPerPage;
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
	         sb.append("<a class=\"page-link\" href=\""+domain+"?sort="+sort+"&page=" + (startNavi-1) + "\">Previous</a>"); 
	         sb.append("</li>");
	      }else {
	         sb.append("<li class=\"page-item disabled\">");
	         sb.append("<a class=\"page-link\">Previous</a>"); 
	         sb.append("</li>");
	      }

	      for(int i= startNavi; i <=endNavi; i++) {
	         if(currentPage==i) {
	            sb.append("<li class=\"page-item active\" aria-current=\"page\">");
	            sb.append("<span class=\"page-link\">" + i + "<span class=\"sr-only\">(current)</span></span></li>");

	         }else {
	            sb.append("<li class=\"page-item\">");
	            sb.append("<a class=\"page-link\" href=\""+domain+"?sort="+sort+"&page="+ i +"\">" + i + "</a></li>");
	         }
	      }

	      if(needNext) {
	         sb.append("<li class=\"page-item\">");
	         sb.append("<a class=\"page-link\" href=\""+domain+"?sort="+sort+"&page=" + (endNavi+1) + "\">Next</a>"); 
	         sb.append("</li>");
	      }else {
	         sb.append("<li class=\"page-item disabled\">");
	         sb.append("<a class=\"page-link\">Next</a>"); 
	         sb.append("</li>");
	      }

	      return sb.toString();
	   }

	   // 모든 멤버 가져오기
	   public List<AdminMemberDto> getAllMember(int page , int memberSort) throws Exception{
	      int currentPage = page;
	      int recordTotalCount = this.getBoardCount(1);


	      int pageTotalCount = 0;
	      if(recordTotalCount % Configuration.recordCountPerPage > 0) {
	         pageTotalCount = recordTotalCount/Configuration.recordCountPerPage+1;
	      }else {
	         pageTotalCount = recordTotalCount/Configuration.recordCountPerPage;
	      }

	      if(currentPage < 1) {
	         currentPage = 1;
	      }else if(currentPage > pageTotalCount) {
	         currentPage=pageTotalCount;
	      }

	      int start = currentPage*Configuration.recordCountPerPage - 
	            (Configuration.recordCountPerPage-1);
	      int end = start + (Configuration.recordCountPerPage-1);


	      String sql ="select * from "
	            + "(select member.*, row_number() "
	            + "over(order by member."+memberSortArr[memberSort]+" desc) rnum "
	            + "from member) where rnum between ? and ?";
	      try (Connection con = this.getConnection();
	            PreparedStatement pstat = con.prepareStatement(sql);){
	         pstat.setInt(1, start);
	         pstat.setInt(2, end);
	         try(ResultSet rs = pstat.executeQuery();){
	            List<AdminMemberDto> list = new ArrayList<>();
	            while(rs.next()) {
	               AdminMemberDto dto = new AdminMemberDto(rs.getString("id"),rs.getString("name")
	                     ,rs.getString("phone"),rs.getString("email")
	                     ,rs.getDate("regist_date"),rs.getInt("report_count"));
	               list.add(dto);
	            }
	            con.close();
	            return list;
	         }         
	      }
	   }

	   // 모든 게시판 글 가져오기
	   public List<AdminBoardDto> getAllBoard(int page , int boardSort) throws Exception{
	      int currentPage = page;
	      int recordTotalCount = this.getBoardCount(0);


	      int pageTotalCount = 0;
	      if(recordTotalCount % Configuration.recordCountPerPage > 0) {
	         pageTotalCount = recordTotalCount/Configuration.recordCountPerPage+1;
	      }else {
	         pageTotalCount = recordTotalCount/Configuration.recordCountPerPage;
	      }

	      if(currentPage < 1) {
	         currentPage = 1;
	      }else if(currentPage > pageTotalCount) {
	         currentPage=pageTotalCount;
	      }

	      int start = currentPage*Configuration.recordCountPerPage - 
	            (Configuration.recordCountPerPage-1);
	      int end = start + (Configuration.recordCountPerPage-1);
	      System.out.println(start);
	System.out.println(end);
	      String sql ="select * "
	            + "from ("
	            + "select t.*, row_number() over(order by t."+boardSortArr[boardSort]+" desc) rnum "
	            + "from ("
	            + "select '공고' as type, seq , title , report_count , writer , write_date "
	            + "from noticeboard "
	            + "union all "
	            + "select '질문' as type, seq , title , report_count , writer , write_date "
	            + "from qnaboard) t) "
	            + "where rnum between ? and ?";
	      try (Connection con = this.getConnection();
	            PreparedStatement pstat = con.prepareStatement(sql);){
	         pstat.setInt(1, start);
	         pstat.setInt(2, end);
	         try(ResultSet rs = pstat.executeQuery();){
	            List<AdminBoardDto> list = new ArrayList<>();
	            while(rs.next()) {
	               AdminBoardDto dto = new AdminBoardDto(rs.getString("type"),rs.getInt("seq"),rs.getString("title"),
	                     rs.getInt("report_count"),
	                     rs.getString("writer"),rs.getDate("write_date"));
	               list.add(dto);
	            }
	            con.close();
	            return list;
	         }         
	      }
	   }


	   // 공모전 가져오기
	   public void insertSite() throws Exception{
		      this.deleteSite();
		      Thread wevityThread1 = new Thread() {
		         public void run() {
		            try {new AdminDaoOld().wevitySite("https://www.wevity.com/?c=find&s=1&gub=1&cidx=21");}
		            catch(Exception e) {e.printStackTrace();}
		         }      
		      };
		      Thread wevityThread2 = new Thread() {
		         public void run() {
		            try {new AdminDaoOld().wevitySite("https://www.wevity.com/?c=find&s=1&gub=1&cidx=20");}
		            catch(Exception e) {}
		         }
		      };
		      Thread thinkThread = new Thread() {
		         public void run() {
		            try {new AdminDaoOld().thinkSite();}
		            catch(Exception e) {}
		         }
		      };
		      long start = System.currentTimeMillis();
		      wevityThread1.start();
		      wevityThread2.start();thinkThread.start();

		      while(wevityThread1.isAlive() 
		            || wevityThread2.isAlive()|| thinkThread.isAlive()) {
		         
		         Thread.sleep(300);
		      }
		      long end= System.currentTimeMillis();
		      System.out.println(end-start);

		   }

	   //   //공모전 사이트 데이터 지우기
	   private void deleteSite() throws Exception{
	      String sql = "delete from crawlingsite";
	      try(Connection con = this.getConnection();
	            PreparedStatement pstat = con.prepareStatement(sql);){
	         pstat.executeUpdate();
	      }
	      System.out.println("delete done");
	   }


	   //   //위비티 사이트 크롤링
	   private void wevitySite(String siteName) throws Exception{
	      System.out.println(siteName + " run");
	      String sql = "insert into CrawlingSite values(?,?,?,?,?,?,?)";

	      Document doc = Jsoup.connect(siteName).get();
	      //기본 페이지 도큐먼트 입력
	      Elements linkTag = doc.select("UL.list>LI>div>a");
	      Elements date = doc.select("UL.list>LI>div>span");

	      List<Document> link = new ArrayList<>();
	      for(int i = 0 ; i < linkTag.size();i++) {
	         if(!date.get(i).text().equals("마감")) {
	            String st = "https://www.wevity.com/" +linkTag.get(i).attr("href");
	            link.add(Jsoup.connect(st).get());
	            //페이지당 도큐먼트 입력
	         }else {break;}
	      }

	      try (Connection con = this.getConnection();
	            PreparedStatement pstat = con.prepareStatement(sql);){

	         for(int i = 0 ; i < link.size();i++) {
	            Element fieldEle = link.get(i).selectFirst("ul.cd-info-list>li");
	            pstat.setString(7 ,  fieldEle.text().replace("분야 ", ""));


	            Element imgEle = link.get(i).selectFirst("div.thumb>img");
	            pstat.setString(1 ,  ("https://www.wevity.com"+imgEle.attr("src")));

	            Element titleEle = link.get(i).selectFirst("h6.tit");
	            pstat.setString(2 ,  titleEle.text());

	            Elements organizeEle = link.get(i).select("UL.cd-info-list>li");
	            pstat.setString(3 ,  organizeEle.get(2).text().replace("주최/주관 ", ""));

	            Element periodEle = link.get(i).selectFirst("LI.dday-area");
	            String[] periods = periodEle.text().split("~");
	            Pattern p = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
	            Matcher m1 = p.matcher(periods[0]); m1.find();
	            Matcher m2 = p.matcher(periods[1]); m2.find();
	            String st1 = m1.group();String st2 = m2.group();
	            pstat.setString(4 ,  st1);
	            pstat.setString(5 ,  st2);
	            pstat.setString(6 ,  organizeEle.get(7).select("a").text());

	            pstat.executeUpdate();
	            pstat.clearParameters();
	         }
//	         con.commit();
	         
	         con.close();
	      }
	      System.out.println(siteName + " done");
	   }

	   //씽굿 사이트 크롤링
	   private void thinkSite() throws Exception{
	      System.out.println("think run");
	      String sql = "insert into CrawlingSite values(?,?,?,?,?,?,?)";


	      Document doc = Jsoup.connect("https://www.thinkcontest.com/Contest/CateField.html?c=12").get();
	      //기본 페이지 도큐먼트 입력
	      Elements linkTag = doc.select("TBODY>TR>TD>DIV>A");
	      Elements date = doc.select("TBODY>TR>TD>SPAN");

	      List<Document> link = new ArrayList<>();
	      for(int i = 0 ; i < linkTag.size();i++) {
	         if(!date.get(i).text().equals("마감")) {
	            String st = "https://www.thinkcontest.com" +linkTag.get(i).attr("href");
	            link.add(Jsoup.connect(st).get());
	            //페이지당 도큐먼트 입력
	         }else {break;}
	      }

	      try (Connection con = this.getConnection();
	            PreparedStatement pstat = con.prepareStatement(sql);){
	         for(int i = 0 ; i < link.size();i++) {
	            Element imgEle = link.get(i).selectFirst("div.poster-holder>img");
	            pstat.setString(1 ,  ("https://www.thinkcontest.com"+imgEle.attr("src")));

	            Element titleEle = link.get(i).selectFirst("span.title");
	            pstat.setString(2 ,  titleEle.text());

	            Elements headEle = link.get(i).select("TBODY>TR>TH");
	            Elements indexEle = link.get(i).select("TBODY>TR>TD");
	            String ori = "";

	            for(int j = 0 ; j < headEle.size() ; j++) {
	               String el = headEle.get(j).text();         
	               if(el.contentEquals("주최")) {
	                  ori += indexEle.get(j).text();                  
	               }else if(el.contentEquals("주관")) {
	                  ori += "," + indexEle.get(j).text();               
	               }else if(el.contentEquals("접수기간")) {
	                  String[] periods = indexEle.get(j).text().split("~");
	                  Pattern p = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
	                  Matcher m1 = p.matcher(periods[0]); m1.find();
	                  Matcher m2 = p.matcher(periods[1]); m2.find();
	                  String st1 = m1.group();String st2 = m2.group();
	                  pstat.setString(4 ,  st1);
	                  pstat.setString(5 ,  st2);
	               }else if(el.contentEquals("홈페이지")) {
	                  pstat.setString(6 ,  indexEle.get(j).select("A").attr("href"));
	               }else if(el.contentEquals("응모분야")) {
	                  pstat.setString(7 ,  indexEle.get(j).text());
	               }

	            }
	            pstat.setString(3 ,  ori);

	            pstat.executeUpdate();
	            pstat.clearParameters();
	         }
//	         con.commit();
	         con.close();
	      }
	      System.out.println("think done");
	   }
	   // 공모전 가져오기


	
	//상빈씨 파트 끝
	
		
		
		
		
	// 이수지 파트 시작 
	
	public int countTotalMember() throws Exception{
		
		String sql ="select count(*) from member";
		try(Connection con =this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				ResultSet rs = pstat.executeQuery();){
			rs.next();
			return rs.getInt(1);

		}
	}
	
	public int totalNoticeCount() throws Exception{
		String sql = "select count(*) from noticeboard";
		try(Connection con = this.getConnection();
			PreparedStatement pstat = con.prepareStatement(sql);	
			ResultSet rs = pstat.executeQuery();){
			rs.next();
			return rs.getInt(1);
		}
	
	}
	
	
	public Map<String, Integer> noticeByLang() throws Exception{
		Map<String, Integer> notice = new HashMap<>();
		String[] lang = {"java", "C/C++", "C#","Python", "기타"};
		String sql ="select count(*) from noticeboard where language=?";
		try(Connection con =this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
			){
			for(int i=0;i<lang.length;i++) {
			pstat.setString(1, lang[i]);
			System.out.println(lang[i]);
			ResultSet rs = pstat.executeQuery();
			rs.next();
			System.out.println(rs.getInt(1));
			notice.put(lang[i],rs.getInt(1));
			}
		}
		
		return notice;
	}
	
	//0521업뎃
	
	public Map<String, Integer> memberByLang() throws Exception{
		Map<String, Integer> member = new HashMap<>();
		String[] lang = {"java", "C/C++", "C#","python","etc"};
		String sql ="select count(*) from language where language=?";
		try(Connection con =this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
			){
			for(int i=0;i<lang.length-1;i++) {
			pstat.setString(1, lang[i]);
			System.out.println(lang[i]);
			ResultSet rs = pstat.executeQuery();
			rs.next();
			System.out.println(rs.getInt(1));
			member.put(lang[i],rs.getInt(1));
			}
		}
		sql ="select count(*) from language where language not in ('java','C/C++','C#','python')";
		try(Connection con =this.getConnection();
				PreparedStatement pstat1 = con.prepareStatement(sql);
			){
			ResultSet rs = pstat1.executeQuery();
			rs.next();
			member.put(lang[4], rs.getInt(1));
		}
		
		return member;
	}
	
	//0522업뎃
	public int totalContestCount() throws Exception{
		String sql = "select count(*) from crawlingsite";
		try(Connection con = this.getConnection();
			PreparedStatement pstat = con.prepareStatement(sql);	
			ResultSet rs = pstat.executeQuery();){
			rs.next();
			return rs.getInt(1);
		}
	
	}
	
	public Map<String, Integer> contestByFields() throws Exception{
		Map<String, Integer> contest = new HashMap<>();
		String[] field = {"웹/모바일/플래시", "게임/소프트웨어", "광고/마케팅","과학/공학", "영상/UCC/사진"};
		String sql ="select count(*) from crawlingsite where content_field like ?";
		try(Connection con =this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
			){
			for(int i=0;i<field.length;i++) {
			pstat.setString(1, "%" + field[i] + "%");
			System.out.println("%" + field[i] + "%");
			ResultSet rs = pstat.executeQuery();
			rs.next();
			System.out.println(rs.getInt(1));
			contest.put(field[i],rs.getInt(1));
			}
		}
		
		return contest;
	}
	
	public int NewMemberThanYesterday() throws Exception{
		String sql = "select (select count(*) from member where to_char(regist_date,'dd/mm/yy')=to_char(sysdate,'dd/mm/yy'))-(select count(*) from member where to_char(regist_date,'dd/mm/yy')=to_char(sysdate-1,'dd/mm/yy')) as result from dual";
		try(Connection con = this.getConnection();
			PreparedStatement pstat = con.prepareStatement(sql);	
			ResultSet rs = pstat.executeQuery();){
			rs.next();
			return rs.getInt(1);
		}
	
	}
	
	public int NewNoticeThanYesterday() throws Exception{
		String sql = "select (select count(*) from noticeboard where to_char(write_date,'dd/mm/yy')=to_char(sysdate,'dd/mm/yy'))-(select count(*) from noticeboard where to_char(write_date,'dd/mm/yy')=to_char(sysdate-1,'dd/mm/yy')) as result from dual";
		try(Connection con = this.getConnection();
			PreparedStatement pstat = con.prepareStatement(sql);	
			ResultSet rs = pstat.executeQuery();){
			rs.next();
			return rs.getInt(1);
		}
	
	}
	
	public int NewContestThanYesterday() throws Exception{
		String sql = "select (select count(*) from crawlingsite where to_char(regist_date,'dd/mm/yy')=to_char(sysdate,'dd/mm/yy'))-(select count(*) from crawlingsite where to_char(regist_date,'dd/mm/yy')=to_char(sysdate-1,'dd/mm/yy')) as result from dual";
		try(Connection con = this.getConnection();
			PreparedStatement pstat = con.prepareStatement(sql);	
			ResultSet rs = pstat.executeQuery();){
			rs.next();
			return rs.getInt(1);
		}
	
	}
	
	public Map<String, Integer> qnaByLang() throws Exception{
		Map<String, Integer> qna = new HashMap<>();
		String[] lang = {"Java", "C/C++", "C#","Python", "etc"};
		String sql ="select count(*) from qnaboard where language=?";
		try(Connection con =this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
			){
			for(int i=0;i<lang.length;i++) {
			pstat.setString(1, lang[i]);
			System.out.println(lang[i]);
			ResultSet rs = pstat.executeQuery();
			rs.next();
			System.out.println(rs.getInt(1));
			qna.put(lang[i],rs.getInt(1));
			}
		}
		
		return qna;
	}
	
	
	
	//이수지 파트 끝
}
