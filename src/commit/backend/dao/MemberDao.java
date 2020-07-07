package commit.backend.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import commit.backend.dto.MemberDto;
import commit.backend.dto.MyBoardDto;
import commit.backend.dto.MyDateDto;
import commit.backend.dto.NewContestDto;
import commit.backend.statics.Configuration;



public class MemberDao {

	private Connection getConnection() throws Exception{
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/dbcp");
		return ds.getConnection();
	}

	public boolean isIdAvailable(String id) throws Exception{
		String sql = "select id from member where id=?";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, id);
			try(ResultSet rs = pstat.executeQuery();){
				return !rs.next();
			}

		}
	}

	public int addMember(MemberDto mem) throws Exception{

		String sql = "insert into member values(?,?,?,?,?,?,?,?,?,?,?,?,?,default,0)";

		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, mem.getId());
			pstat.setString(2, mem.getPw());
			pstat.setString(3, mem.getName());
			pstat.setString(4, mem.getPhone());
			pstat.setString(5, mem.getEmail());
			pstat.setString(6, mem.getZipcode());
			pstat.setString(7, mem.getAddress1());
			pstat.setString(8, mem.getAddress2());
			pstat.setString(9, mem.getBirthday());
			pstat.setString(10, mem.getCareer());
			pstat.setString(11, mem.getAgreement());
			pstat.setInt(12, mem.getHintQnum());
			pstat.setString(13, mem.getHintA());

			int result = pstat.executeUpdate();
			con.commit();
			return result;
		}
	}

	public int addLanguage(String id , String language) throws Exception{
		String sql = "insert into language values(?,?)";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, id);
			pstat.setString(2, language);

			int result = pstat.executeUpdate();
			con.commit();
			return result;
		}
	}

	public void addStartDate(String id) throws Exception{
		String sql = "insert into member_activity values(?,default,default)";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, id);
			pstat.executeUpdate();
			con.commit();
		}
	}

	public boolean removeMember(String id) throws Exception{
		String sql = "delete member where id=?";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, id);
			try(ResultSet rs=pstat.executeQuery();){
				con.commit();
				return rs.next();
			}
		}
	}

	public List<MyBoardDto> getMyboard(String id,int cpage) throws Exception{
		String sql = "select * from(select row_number() over(order by write_date desc) rnum, '질문' as category,seq , title , writer , write_date, view_count from qnaboard where writer=? union all select row_number() over(order by write_date desc) rnum, '공고' as category,seq , title , writer , write_date, view_count from noticeboard where writer=?) where rnum between ? and ?";
		int start = 
				cpage*Configuration.recordCountPerPage-(Configuration.recordCountPerPage-1);
		int end = start + (Configuration.recordCountPerPage-1);

		List<MyBoardDto> list = new ArrayList<>();
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, id);
			pstat.setString(2, id);
			pstat.setInt(3, start);
			pstat.setInt(4, end);
			try(ResultSet rs = pstat.executeQuery();){
				while(rs.next()) {
					String category = rs.getString("category");
					int seq = rs.getInt("seq");
					String title = rs.getString("title");
					String writer = rs.getString("writer");
					Date write_date = rs.getDate("write_date");
					int view_count = rs.getInt("view_count");
					String write_date2=new SimpleDateFormat("yyyy-MM-dd").format(write_date);
					list.add(new MyBoardDto(category,seq,title,writer,write_date2,view_count));		
				}
			}
		}
		return list;
	}


	public int getArticleCount(String id) throws Exception {
		String sql = "select count(cnt) from(select count(*) cnt from qnaboard where writer=? union all select count(*) cnt from noticeboard where writer=?)";
		try(Connection con =this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, id);
			pstat.setString(2, id);
			try(ResultSet rs = pstat.executeQuery();){
				rs.next();


				return rs.getInt(1);
			}
		}

	}
	


	public String getPageNavi(String id,int currentPage) throws Exception{

		int recordTotalCount = this.getArticleCount(id);



		int pageTotalCount = 0;

		if((recordTotalCount % Configuration.recordCountPerPage) > 0) {
			pageTotalCount = recordTotalCount / Configuration.recordCountPerPage + 1;
		}else {
			pageTotalCount = recordTotalCount / Configuration.recordCountPerPage;
		}


		if(currentPage<1) {
			currentPage = 1;
		}else if(currentPage > pageTotalCount) {
			currentPage = pageTotalCount;
		}

		int startNavi = (currentPage - 1) / Configuration.naviCountPerPage * Configuration.naviCountPerPage + 1;

		int endNavi = startNavi+Configuration.naviCountPerPage-1;
		if(endNavi>pageTotalCount) {
			endNavi = pageTotalCount;
		}

		boolean needPrev = true;
		boolean needNext = true;

		if(startNavi == 1) {needPrev=false;}
		if(endNavi == pageTotalCount) {needNext=false;}


		StringBuilder sb = new StringBuilder();


		if(needPrev) {
			sb.append("<a href='mywrite.mem?cpage="+(startNavi-1)+"'>< </a>");
		}
		for(int i = startNavi;i<=endNavi;i++) {
			sb.append("<a href='mywrite.mem?cpage="+i+"' class='navi'>"+i+"</a>");
		}
		if(needNext) {
			sb.append("<a href='mywrite.mem?cpage="+(endNavi+1)+"'> ></a>");
		}

		return sb.toString();
	}
	



	public boolean login(String id, String pw) throws Exception {
		String sql = "select * from member where id=? and pw=?";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql)) {
			pstat.setString(1, id);
			pstat.setString(2, pw);

			try(ResultSet rs = pstat.executeQuery();){
				return rs.next();
			}

		}
	}

	public MemberDto mypage(String id) throws Exception {
		String sql = "select id, pw, name, phone, email, zipcode, address1, address2, birthday, career, agreement, hintQnum, hintA, regist_date from member where id = ?";
		MemberDto dto = new MemberDto();
		try (Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){      
			pstat.setString(1, id);
			try(ResultSet rs = pstat.executeQuery();){
				while(rs.next()) {
					String mid = rs.getString(1);   
					String pw = rs.getString(2);
					String name = rs.getString(3);
					String phone = rs.getString(4);
					String email = rs.getString(5);
					String zipcode = rs.getString(6);
					String address1 = rs.getString(7);
					String address2 = rs.getString(8);
					Date birthday2 = rs.getDate(9);
					String career = rs.getString(10);
					String agreement = rs.getString(11);
					int hintQnum = rs.getInt(12);
					String hintA = rs.getString(13);
					String birthday=new SimpleDateFormat("yyyy-MM-dd").format(birthday2);
					dto = new MemberDto(mid,pw,name,phone,email,zipcode,address1,address2,birthday,career,agreement,hintQnum,hintA,null);

				}
			}
			return dto;
		}
	}

	public MyDateDto mydate(String id)throws Exception{
		String sql = "select start_date,end_date from member_activity where user_id = ?";
		MyDateDto date = new MyDateDto();
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, id);
			try(ResultSet rs = pstat.executeQuery();){
				while(rs.next()) {
					Date start_date2 = rs.getDate("start_date");
					Date end_date2 = rs.getDate("end_date");
					String start_date = new SimpleDateFormat("yyyy-MM-dd").format(start_date2);
					String end_date = new SimpleDateFormat("yyyy-MM-dd").format(end_date2);
					date = new MyDateDto(start_date,end_date);
				}

				return date; 
			}
		}
	}

	public List<String> mylanguage(String id) throws Exception{
		String sql = "select language from language where user_id=?";
		List<String> languages = new ArrayList<>();
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, id);
			try(ResultSet rs = pstat.executeQuery();){
				while(rs.next()) {
					String language = rs.getString("language");
					languages.add(language);
				}
				return languages;
			}
		}
	}



	public boolean isAbleMail(String mail) throws Exception{
		String sql = "select email from member where email=?";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, mail);
			try(ResultSet rs = pstat.executeQuery();){
				return !rs.next();
			}
		}
	}

	public int updateDate(String id,String start,String end) throws Exception{
		String sql = "update member_activity SET start_date=TO_DATE(?,'YYYY-MM-DD'),end_date=TO_DATE(?,'YYYY-MM-DD') where user_id=?";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, start);
			pstat.setString(2, end);
			pstat.setString(3, id);
			int result = pstat.executeUpdate();
			con.commit();
			return result;
		}
	}


	public String findid(String findname, String findemail) throws Exception {
		String sql ="select id from member where name=?and email=?";

		String id = "";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql)) {
			pstat.setString(1, findname);
			pstat.setString(2, findemail);
			try(ResultSet rs = pstat.executeQuery();){
				while(rs.next()) {
					id = rs.getString("id");
				}
			}
		}
		return id;
	}

	public boolean findpw(String findpwid,String hintqnum, String hintanswer) throws Exception {
		String sql ="select *from member where id=? and hintqnum=? and hinta=?";

		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);) {
			pstat.setString(1, findpwid);
			pstat.setString(2, hintqnum);
			pstat.setString(3, hintanswer);
			try(ResultSet rs = pstat.executeQuery();){
				return rs.next();

			}
		}   
	}
	public int changepw(String id, String pw2) throws Exception {
		String sql ="update member set pw=? where id=?";
		try (Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){   
			pstat.setString(1, pw2);
			pstat.setString(2, id);
			int result = pstat.executeUpdate();
			con.commit();
			return result;
		}
	}

	public int changeinfo(String id,String name,String hintQnum, String hintA,String zipcode,String address1,String address2,String phone,String birthday,String career,String agreement) throws Exception{
		String sql = "update member set name=?,hintQnum=?,hintA=?,zipcode=?,address1=?,address2=?,phone=?,birthday=?,career=?,agreement=? where id=?";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, name);
			pstat.setString(2, hintQnum);
			pstat.setString(3, hintA);
			pstat.setString(4, zipcode);
			pstat.setString(5, address1);
			pstat.setString(6, address2);
			pstat.setString(7, phone);
			pstat.setString(8, birthday);
			pstat.setString(9, career);
			pstat.setString(10, agreement);
			pstat.setString(11, id);

			int result = pstat.executeUpdate();
			con.commit();

			return result;
		}
	}

	public void deletelanguage(String id) throws Exception{
		String sql = "delete language where user_id=?";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, id);
			Thread.sleep(2000);
			pstat.executeUpdate();

			con.commit();
		}
	}

	public void deletedate(String id) throws Exception{
		String sql = "delete member_activity where user_id=?";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, id);
			Thread.sleep(2000);
			pstat.executeUpdate();

			con.commit();
		}
	}

	
	public void deleteQna(int seq) throws Exception{
		String sql = "delete qnaboard where seq=?";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setInt(1, seq);
			
			pstat.executeUpdate();
			con.commit();
		}
	}
	
	public void deleteNotice(int seq) throws Exception{
		String sql = "delete noticeboard where seq=?";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setInt(1, seq);
			
			pstat.executeUpdate();
			con.commit();
		}
	}
	
	public List<MyBoardDto> newboard() throws Exception{
		String sql = "select * from(select '질문' as category,seq, title , writer , write_date, view_count from qnaboard union all select '공고' as category,seq,title , writer , write_date, view_count from noticeboard) where rownum<=10 order by write_date desc";
		List<MyBoardDto> board = new ArrayList<>();
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				ResultSet rs = pstat.executeQuery();){
			while(rs.next()) {
				String category = rs.getString("category");
				int seq = rs.getInt("seq");
				String title = rs.getString("title");
				String writer = rs.getString("writer");
				Date write_date = rs.getDate("write_date");
				int view_count = rs.getInt("view_count");
				String write_date2=new SimpleDateFormat("yyyy-MM-dd").format(write_date);
				board.add(new MyBoardDto(category,seq,title,writer,write_date2,view_count));
			}
		}
		return board;
	}

	
	public List<MyBoardDto> popularboard() throws Exception{
		String sql = "select * from(select '질문' as category,seq, title , writer , write_date, view_count from qnaboard union all select '공고' as category,seq,title , writer , write_date, view_count from noticeboard) where rownum<=10 order by view_count desc";
		List<MyBoardDto> popboard = new ArrayList<>();
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				ResultSet rs = pstat.executeQuery();){
			while(rs.next()) {
				String category = rs.getString("category");
				int seq = rs.getInt("seq");
				String title = rs.getString("title");
				String writer = rs.getString("writer");
				Date write_date = rs.getDate("write_date");
				int view_count = rs.getInt("view_count");
				String write_date2=new SimpleDateFormat("yyyy-MM-dd").format(write_date);
				popboard.add(new MyBoardDto(category,seq,title,writer,write_date2,view_count));
			}
		}
		return popboard;
	}
	
	public List<MemberDto> selectNewMember() throws Exception{
		String sql = "select * from member where rownum<=8 order by regist_date desc";
		List<MemberDto> members = new ArrayList<>();
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				ResultSet rs = pstat.executeQuery();){
			while(rs.next()) {
				String id = rs.getString("id");
				String name = rs.getString("name");
				Date regist_date = rs.getDate("regist_date");
				String regist_date2=new SimpleDateFormat("yyyy-MM-dd").format(regist_date);
				members.add(new MemberDto(id,name,"","","","","","","","","",0,"",regist_date2));
			}
		}
		return members;
	}
	
	public List<NewContestDto> selectcontest() throws Exception{
		String sql = "select title,direct_url from crawlingsite where rownum<=10 order by start_date desc";
		List<NewContestDto> newcontest = new ArrayList<>();
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			try(ResultSet rs = pstat.executeQuery();){
				while(rs.next()) {
					String title = rs.getString("title");
					String url = rs.getString("direct_url");
					newcontest.add(new NewContestDto(title,url));
				}
			}
		}return newcontest;
	}
	
}
