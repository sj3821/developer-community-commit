package commit.backend.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import commit.backend.dto.NoticeBoardDto;
import commit.backend.dto.QnaBoardDto;
import commit.backend.statics.Configuration;

public class BoardDao {

	private Connection getConnection() throws Exception {

		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/dbcp");

		return ds.getConnection();

	}
	
	
	//태훈씨 파트 시작
	

	public int qnaArticleCount() throws Exception {
		//////////////////////////////////////////////////////////게시판 맞게 보드명 수정 필요 //////////////////////////////////
		String sql = "select count(*) from qnaboard";
		try(Connection con =this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				ResultSet rs = pstat.executeQuery();){
			rs.next();
			return rs.getInt(1);

		}

	}

	public List<QnaBoardDto> selectQnaList(int cpage) throws Exception{
		int start = cpage*Configuration.recordCountPerPage-(Configuration.recordCountPerPage-1);
		int end = start + (Configuration.recordCountPerPage-1);

		String sql = "select * from (select qnaboard.*, row_number() over(order by seq desc) "
				+ "rnum from qnaboard) where rnum between ? and ?";

		List<QnaBoardDto> result = new ArrayList<>(); 

		try(Connection con =this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setInt(1, start);
			pstat.setInt(2, end);

			try(ResultSet rs = pstat.executeQuery();){
				while(rs.next()) {
					int seq = rs.getInt("seq");
					String title = rs.getString("title");
					String contents = rs.getString("contents");
					String writer = rs.getString("writer");
					Timestamp write_date = rs.getTimestamp("write_date");
					int view_count = rs.getInt("view_count");
					String ip_addr = rs.getString("ip_addr");
					String language = rs.getString("language");
					int report_count = rs.getInt("report_count");
					String img_photo = rs.getString("img_photo");	
					result.add(new QnaBoardDto(seq,title,contents,writer,write_date,view_count,ip_addr, language,report_count,img_photo));
				}
			}
		}
		return result;
	}

	public String qnaPageNavi(int currentPage) throws Exception{

		int recordTotalCount = this.qnaArticleCount(); 


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

		System.out.println("모든게시글 : " + recordTotalCount);
		System.out.println("총페이지수" + pageTotalCount);
		System.out.println("현재 페이지 :" +  currentPage);
		System.out.println("네비게시션 시작 : " + startNavi);
		System.out.println("네비게인션 끝 : " + endNavi);

		////////////////////////////////////////////////////////// 게시판 맞게 게시판 목록 호출 페이지 수정 필요 //////////////////////////////////
		if(needPrev) {
			sb.append("<li class=\"page-item\">");
			// 게시판 호출 dao 로 넘어간다
			sb.append("<a class=\"page-link\" href=\"selectQnaList.board?cpage=" + (startNavi-1) + "\">Previous</a>"); 
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
				// 게시판 호출 dao 로 넘어간다
				sb.append("<a class=\"page-link\" href=\"selectQnaList.board?cpage="+ i +"\">" + i + "</a></li>");
			}
		}

		if(needNext) {
			sb.append("<li class=\"page-item\">");
			// 게시판 호출 dao 로 넘어간다
			sb.append("<a class=\"page-link\" href=\"selectQnaList.board?cpage=" + (endNavi+1) + "\">Next</a>"); 
			sb.append("</li>");
		}else {
			sb.append("<li class=\"page-item disabled\">");
			sb.append("<a class=\"page-link\">Next</a>"); 
			sb.append("</li>");
		}

		return sb.toString();
	}

	public QnaBoardDto selectQnaDetail(int readseq, String contentwriter, String loginid) throws Exception {

		QnaBoardDto result= null;
		System.out.println("fhradf + " + loginid);
		System.out.println(contentwriter);
		int countUp=0;
		boolean dd = true;

		if(contentwriter.contentEquals(loginid)) {
			dd = false;
		}
		else {
			String sql = "update qnaboard set view_count = view_count+1 where seq=?";
			try(Connection con = this.getConnection();
					PreparedStatement pstat = con.prepareStatement(sql);){
				pstat.setInt(1,readseq);
				countUp = pstat.executeUpdate();
				//con.commit();
			}
		}

		if(countUp>0 || dd==false) {
			String sql2 = "select * from qnaboard where seq=?";
			try(Connection con = this.getConnection();
					PreparedStatement pstat = con.prepareStatement(sql2);){
				pstat.setInt(1,readseq);	
				try(ResultSet rs = pstat.executeQuery();){
					rs.next();
					int seq = rs.getInt("seq");
					String title = rs.getString("title");
					String contents = rs.getString("contents");
					String writer = rs.getString("writer");
					Timestamp write_date = rs.getTimestamp("write_date");
					int view_count = rs.getInt("view_count");
					String ip_addr = rs.getString("ip_addr");
					String language = rs.getString("language");
					int report_count = rs.getInt("report_count");
					String img_photo = rs.getString("img_photo");
					result = new QnaBoardDto(seq,title,contents,writer,write_date,view_count,ip_addr, language,report_count,img_photo);

				}
			}
		}
		return result;

	}

	public int writeQna(QnaBoardDto qbdto) throws Exception {
		System.out.println("dao 도착");
		String sql = "insert into qnaboard values(qnaboard_seq.nextval,?,?,?,sysdate,0,?,?,0,?)";

		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, qbdto.getTitle());
			pstat.setString(2, qbdto.getContents());
			pstat.setString(3, qbdto.getWriter());
			pstat.setString(4, qbdto.getIp_addr());
			pstat.setString(5, qbdto.getLanguage());
			pstat.setString(6, qbdto.getImg_photo());
			int result = pstat.executeUpdate();
			//con.commit();
			return result;
		}
	}

	public int deleteQna(int seq) throws Exception {
		String sql = "delete from qnaboard where seq=?";

		try(Connection con =this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setInt(1,seq);
			int result = pstat.executeUpdate();
			//con.commit();
			return result;
		}
	}

	public int editQna(int seq , String title, String contents , String img_photo) throws Exception {
		String sql = "update qnaboard set title=? , contents=? , img_photo=? where seq=?";

		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1,title);
			pstat.setString(2, contents);
			pstat.setString(3, img_photo);
			pstat.setInt(4, seq);
			int result = pstat.executeUpdate();
			//con.commit();
			return result;
		}

	}

	public List<QnaBoardDto> searchQnaByKeywordAndLanguage(String qna_keyword, String[] languages) throws Exception{
		List <QnaBoardDto> list = new ArrayList<>();

		String sql = "select * from qnaboard where (contents like ? or title like ? or writer like ?) and";

		String tmp= "(language ='" + languages[0] + "'";

		for(int i = 1 ; i < languages.length ; i++) {
			tmp += " or language ='" + languages[i] + "')" ;
		}

		tmp+=" order by 1 asc";
		sql+=tmp;
		System.out.println(sql);

		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, '%' + qna_keyword + '%');
			pstat.setString(2, '%' + qna_keyword + '%');
			pstat.setString(3, '%' + qna_keyword + '%');

			try(ResultSet rs = pstat.executeQuery();){
				while(rs.next()) {
					int seq = rs.getInt("seq");
					String title = rs.getString("title");
					String contents = rs.getString("contents");
					String writer = rs.getString("writer");
					Timestamp write_date = rs.getTimestamp("write_date");
					int view_count = rs.getInt("view_count");
					String ip_addr = rs.getString("ip_addr");
					String language = rs.getString("language");
					int report_count = rs.getInt("report_count");
					String img_photo = rs.getString("img_photo");

					list.add(new QnaBoardDto(seq, title, contents, writer, write_date, view_count, ip_addr, language, report_count, img_photo));
				}
			}
		}return list;
	}

	public List<QnaBoardDto> searchQnaByLanguages(String[] languages) throws Exception {
		List<QnaBoardDto> list = new ArrayList<>();
		String sql = "select * from qnaboard where";

		String tmp= " language ='" + languages[0] + "'";
		for(int i = 1 ; i < languages.length ; i++) {
			tmp += " or language ='" + languages[i] + "'" ;
		}
		tmp+=" order by 1 asc";
		sql+=tmp;
		System.out.println(sql);
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			try(ResultSet rs = pstat.executeQuery();){
				while(rs.next()) {
					int seq = rs.getInt("seq");
					String title = rs.getString("title");
					String contents = rs.getString("contents");
					String writer = rs.getString("writer");
					Timestamp write_date = rs.getTimestamp("write_date");
					int view_count = rs.getInt("view_count");
					String ip_addr = rs.getString("ip_addr");
					String language = rs.getString("language");
					int report_count = rs.getInt("report_count");
					String img_photo = rs.getString("img_photo");

					list.add(new QnaBoardDto(seq, title, contents, writer, write_date, view_count, ip_addr, language, report_count, img_photo));
				}
			}

		}
		return list;
	}

	public List<QnaBoardDto> searchQnaByKeyword(String qna_keyword) throws Exception{
		List<QnaBoardDto> list = new ArrayList<>();

		String sql = "select * from qnaboard where contents like ? or title like ? or writer like? order by 1 asc";

		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, '%' + qna_keyword + '%');
			pstat.setString(2, '%' + qna_keyword + '%');
			pstat.setString(3, '%' + qna_keyword + '%');

			try(ResultSet rs = pstat.executeQuery();){
				while(rs.next()) {
					int seq = rs.getInt("seq");
					String title = rs.getString("title");
					String contents = rs.getString("contents");
					String writer = rs.getString("writer");
					Timestamp write_date = rs.getTimestamp("write_date");
					int view_count = rs.getInt("view_count");
					String ip_addr = rs.getString("ip_addr");
					String language = rs.getString("language");
					int report_count = rs.getInt("report_count");
					String img_photo = rs.getString("img_photo");

					list.add(new QnaBoardDto(seq, title, contents, writer, write_date, view_count, ip_addr, language, report_count, img_photo));

				}
			}
			return list;
		}
	}

	public int reportQna(int seq) throws Exception {
		String sql = "update qnaboard set report_count = report_count + 1 where seq=?";

		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setInt(1,seq);
			int result = pstat.executeUpdate();
			//con.commit();
			return result;

		}
	}
	
	// 태훈씨 파트 끝
	
	// 지은씨 파트 시작 
	public int NoticeViewCountUp(int seq) throws Exception {

		String sql1 = "update noticeboard set view_count=view_count+1 where seq=?";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql1);){
			pstat.setInt(1, seq);
			int result = pstat.executeUpdate();
			
			return result;
		}
	}
	
	public NoticeBoardDto selectNoticeDetail(int seq) throws Exception {
		NoticeBoardDto nbdto = new NoticeBoardDto();
		
		String sql2 = "select * from noticeboard where seq=?";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql2);){
			pstat.setInt(1, seq);
			try(ResultSet rs = pstat.executeQuery();){
				while(rs.next()) {
					seq = rs.getInt("seq");
					String language = rs.getString("language");
					String title = rs.getString("title");
					String contents = rs.getString("contents");
					String img_photo = rs.getString("img_photo");
					Timestamp write_date = rs.getTimestamp("write_date");
					int view_count = rs.getInt("view_count");
					int report_count = rs.getInt("report_count");
					String writer = rs.getString("writer");
					Date start_date = rs.getDate("start_date");
					Date end_date = rs.getDate("end_date");

					nbdto = new NoticeBoardDto(seq, language, title, contents, img_photo, write_date, view_count, report_count, writer, start_date, end_date);

				}
				return nbdto;
			}
		}

	}

	public int writeNotice(String language, String title, String contents, String img_photo, int view_count, int report_count, String writer, Date start_date, Date end_date)throws Exception {
		String sql = "insert into noticeboard values(noticeboard_seq.nextval, ?, ?, ?, ?, sysdate, ?, ?, ?, ?, ?)";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				){
			pstat.setString(1, language);
			pstat.setString(2, title);
			pstat.setString(3, contents);
			pstat.setString(4, img_photo);
			pstat.setInt(5, view_count);
			pstat.setInt(6, report_count);
			pstat.setString(7, writer);
			pstat.setDate(8, start_date);
			pstat.setDate(9, end_date);

			int result = pstat.executeUpdate();
			
			return result;
		}
	}

	public int editNotice(int seq, Date start_date, Date end_date, String language, String title, String contents) throws Exception{
		String sql = "update noticeboard set language=?, title=?, contents=?, start_date=to_date(?), end_date=to_date(?) where seq=?";

		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, language);
			pstat.setString(2, title);
			pstat.setString(3, contents);
			pstat.setDate(4, start_date);
			pstat.setDate(5, end_date);
			pstat.setInt(6, seq);

			int result = pstat.executeUpdate();
			
			return result;
		}
	}

	public int deleteNotice(int seq) throws Exception {
		String sql = "delete noticeboard where seq=?";

		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setInt(1, seq);
			int result = pstat.executeUpdate();
			

			return result;
		}
	}

	public int getArticleCount() throws Exception {
		String sql = "select count(*) from noticeboard";
		try(Connection con =this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				ResultSet rs = pstat.executeQuery();){
			rs.next();
			return rs.getInt(1);
		}
	}

	public List<NoticeBoardDto> selectNoticeList(int cpage) throws Exception{
		int start = cpage*Configuration.recordCountPerPage-(Configuration.recordCountPerPage-1);
		int end = start + (Configuration.recordCountPerPage-1);

		String sql = "select * from (select noticeboard.*, row_number() over(order by seq desc) "
				+ "rnum from noticeboard) where rnum between ? and ?";

		List<NoticeBoardDto> list = new ArrayList<>(); 
		NoticeBoardDto nbdto = new NoticeBoardDto();

		try(Connection con =this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setInt(1, start);
			pstat.setInt(2, end);

			try(ResultSet rs = pstat.executeQuery();){
				while(rs.next()) {
					int seq = rs.getInt("seq");
					String language = rs.getString("language");
					String title = rs.getString("title");
					String contents = rs.getString("contents");
					String img_photo = rs.getString("img_photo");
					String writer = rs.getString("writer");
					Timestamp write_date = rs.getTimestamp("write_date");
					int view_count = rs.getInt("view_count");
					int report_count = rs.getInt("report_count");
					Date start_date = rs.getDate("start_date");
					Date end_date = rs.getDate("end_date");

					list.add(new NoticeBoardDto(seq, language, title, contents,img_photo, write_date,
							view_count,report_count,writer,start_date, end_date));
				}
			}
		}
		return list;
	}

	public String getPageNavi(int currentPage) throws Exception{

		int recordTotalCount = this.getArticleCount(); 


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

		System.out.println("모든게시글 : " + recordTotalCount);
		System.out.println("총페이지수" + pageTotalCount);
		System.out.println("현재 페이지 :" +  currentPage);
		System.out.println("네비게시션 시작 : " + startNavi);
		System.out.println("네비게인션 끝 : " + endNavi);

		////////////////////////////////////////////////////////// 게시판 맞게 게시판 목록 호출 페이지 수정 필요 //////////////////////////////////
		if(needPrev) {
			sb.append("<li class=\"page-item\">");
			// 게시판 호출 dao 로 넘어간다
			sb.append("<a class=\"page-link\" href=\"selectNoticeList.board?cpage=" + (startNavi-1) + "\">Previous</a>"); 
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
				// 게시판 호출 dao 로 넘어간다
				sb.append("<a class=\"page-link\" href=\"selectNoticeList.board?cpage="+ i +"\">" + i + "</a></li>");
			}
		}

		if(needNext) {
			sb.append("<li class=\"page-item\">");
			// 게시판 호출 dao 로 넘어간다
			sb.append("<a class=\"page-link\" href=\"selectNoticeList.board?cpage=" + (endNavi+1) + "\">Next</a>"); 
			sb.append("</li>");
		}else {
			sb.append("<li class=\"page-item disabled\">");
			sb.append("<a class=\"page-link\">Next</a>"); 
			sb.append("</li>");
		}

		return sb.toString();
	}

	public List<NoticeBoardDto> searchNoticeByKeyword(String notice_keyword) throws Exception{
		List<NoticeBoardDto> list = new ArrayList<>();

		String sql = "select * from noticeboard where contents like ? or title like ? or writer like? order by 1 asc";

		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, '%' + notice_keyword + '%');
			pstat.setString(2, '%' + notice_keyword + '%');
			pstat.setString(3, '%' + notice_keyword + '%');

			try(ResultSet rs = pstat.executeQuery();){
				while(rs.next()) {
					int seq = rs.getInt("seq");
					String language = rs.getString("language");
					String title = rs.getString("title");
					String contents = rs.getString("contents");
					String img_photo = rs.getString("img_photo");
					String writer = rs.getString("writer");
					Timestamp write_date = rs.getTimestamp("write_date");
					int view_count = rs.getInt("view_count");
					int report_count = rs.getInt("report_count");
					Date start_date = rs.getDate("start_date");
					Date end_date = rs.getDate("end_date");

					list.add(new NoticeBoardDto(seq, language, title, contents,img_photo, write_date,
							view_count,report_count,writer,start_date, end_date));

				}
			}
			return list;
		}
	}

	public List<NoticeBoardDto> searchNoticeByDate(String search_start_date, String search_end_date)throws Exception{
		List<NoticeBoardDto> list = new ArrayList<>();

		System.out.println(search_start_date + "+" + search_end_date);

		String sql = "select * from noticeboard where start_date >= to_date(?) and end_date <= to_date(?) order by 1 asc";

		try(
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				){
			pstat.setString(1, search_start_date);
			pstat.setString(2, search_end_date); try(
					ResultSet rs = pstat.executeQuery();){

				while(rs.next()) {
					int seq = rs.getInt("seq");
					String language = rs.getString("language");
					String title = rs.getString("title");
					String contents = rs.getString("contents");
					String img_photo = rs.getString("img_photo");
					String writer = rs.getString("writer");
					Timestamp write_date = rs.getTimestamp("write_date");
					int view_count = rs.getInt("view_count");
					int report_count = rs.getInt("report_count");
					Date start_date = rs.getDate("start_date");
					Date end_date = rs.getDate("end_date");

					list.add(new NoticeBoardDto(seq, language, title, contents,img_photo, write_date,
							view_count,report_count,writer,start_date, end_date));
				}

			}
		}return list;
	}

	public List<NoticeBoardDto> searchNoticeByLanguages(String[] languages) throws Exception {
		List<NoticeBoardDto> list = new ArrayList<>();
		String sql = "select * from noticeboard where";

		String tmp= " language ='" + languages[0] + "'";
		for(int i = 1 ; i < languages.length ; i++) {
			tmp += " or language ='" + languages[i] + "'" ;
		}
		tmp+=" order by 1 asc";
		sql+=tmp;
		System.out.println(sql);
		try(
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				){

			try(ResultSet rs = pstat.executeQuery();){


				while(rs.next()) {
					int seq = rs.getInt("seq");
					String language = rs.getString("language");
					String title = rs.getString("title");
					String contents = rs.getString("contents");
					String img_photo = rs.getString("img_photo");
					String writer = rs.getString("writer");
					Timestamp write_date = rs.getTimestamp("write_date");
					int view_count = rs.getInt("view_count");
					int report_count = rs.getInt("report_count");
					Date start_date = rs.getDate("start_date");
					Date end_date = rs.getDate("end_date");

					list.add(new NoticeBoardDto(seq, language, title, contents,img_photo, write_date,
							view_count,report_count,writer,start_date, end_date));
				}
			}

		}
		return list;
	}
	public List<NoticeBoardDto> searchByKeywordAndDate(String notice_keyword, String search_start_date, String search_end_date) throws Exception{
		List <NoticeBoardDto> list = new ArrayList<>();

		String sql = "select * from noticeboard where (contents like ? or title like ? or writer like?) and (start_date >= to_date(?) and end_date <= to_date(?)) order by 1 asc";

		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, '%' + notice_keyword + '%');
			pstat.setString(2, '%' + notice_keyword + '%');
			pstat.setString(3, '%' + notice_keyword + '%');
			pstat.setString(4, search_start_date);
			pstat.setString(5, search_end_date);

			try(ResultSet rs = pstat.executeQuery();){
				while(rs.next()) {
					int seq = rs.getInt("seq");
					String language = rs.getString("language");
					String title = rs.getString("title");
					String contents = rs.getString("contents");
					String img_photo = rs.getString("img_photo");
					String writer = rs.getString("writer");
					Timestamp write_date = rs.getTimestamp("write_date");
					int view_count = rs.getInt("view_count");
					int report_count = rs.getInt("report_count");
					Date start_date = rs.getDate("start_date");
					Date end_date = rs.getDate("end_date");

					list.add(new NoticeBoardDto(seq, language, title, contents,img_photo, write_date,
							view_count,report_count,writer,start_date, end_date));
				}

			}

		} return list;

	}

	public List<NoticeBoardDto> searchByKeywordAndLanguage(String notice_keyword, String[] languages) throws Exception{
		List <NoticeBoardDto> list = new ArrayList<>();

		String sql = "select * from noticeboard where (contents like ? or title like ? or writer like ?) and";

		String tmp= "(language ='" + languages[0] + "'";

		for(int i = 1 ; i < languages.length ; i++) {
			tmp += " or language ='" + languages[i] + "')" ;
		}

		tmp+=" order by 1 asc";
		sql+=tmp;
		System.out.println(sql);

		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, '%' + notice_keyword + '%');
			pstat.setString(2, '%' + notice_keyword + '%');
			pstat.setString(3, '%' + notice_keyword + '%');

			try(ResultSet rs = pstat.executeQuery();){
				while(rs.next()) {
					int seq = rs.getInt("seq");
					String language = rs.getString("language");
					String title = rs.getString("title");
					String contents = rs.getString("contents");
					String img_photo = rs.getString("img_photo");
					String writer = rs.getString("writer");
					Timestamp write_date = rs.getTimestamp("write_date");
					int view_count = rs.getInt("view_count");
					int report_count = rs.getInt("report_count");
					Date start_date = rs.getDate("start_date");
					Date end_date = rs.getDate("end_date");

					list.add(new NoticeBoardDto(seq, language, title, contents,img_photo, write_date,
							view_count,report_count,writer,start_date, end_date));
				}
			}
		}return list;
	}

	public List<NoticeBoardDto> searchByDateAndLanguage(String search_start_date, String search_end_date, String[] languages) throws Exception{
		List<NoticeBoardDto> list = new ArrayList<>();

		String sql = "select * from noticeboard where (start_date >= to_date(?) and end_date <= to_date(?)) and"; 

		String tmp= " (language ='" + languages[0] + "'";
		for(int i = 1 ; i < languages.length ; i++) {
			tmp += " or language ='" + languages[i] + "')" ;
		}

		tmp+=" order by 1 asc";
		sql+=tmp;
		System.out.println(sql);

		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, search_start_date);
			pstat.setString(2, search_end_date);

			try(ResultSet rs = pstat.executeQuery();){
				while(rs.next()) {
					int seq = rs.getInt("seq");
					String language = rs.getString("language");
					String title = rs.getString("title");
					String contents = rs.getString("contents");
					String img_photo = rs.getString("img_photo");
					String writer = rs.getString("writer");
					Timestamp write_date = rs.getTimestamp("write_date");
					int view_count = rs.getInt("view_count");
					int report_count = rs.getInt("report_count");
					Date start_date = rs.getDate("start_date");
					Date end_date = rs.getDate("end_date");

					list.add(new NoticeBoardDto(seq, language, title, contents,img_photo, write_date,
							view_count,report_count,writer,start_date, end_date));
				}
			}
		}
		return list;
	}


	public List<NoticeBoardDto> searchByKeywordAndDateAndLanguage(String notice_keyword, String search_start_date, String search_end_date, String[] languages) throws Exception{
		List<NoticeBoardDto> list = new ArrayList<>();

		String sql = "select * from noticeboard where (contents like ? or title like ? or writer like ?) and (start_date >= to_date(?) and end_date <= to_date(?)) and";

		String tmp= " (language ='" + languages[0] + "'";
		for(int i = 1 ; i < languages.length ; i++) {
			tmp += " or language ='" + languages[i] + "')" ;
		}

		tmp+=" order by 1 asc";
		sql+=tmp;
		System.out.println(sql);

		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, '%' + notice_keyword + '%');
			pstat.setString(2, '%' + notice_keyword + '%');
			pstat.setString(3, '%' + notice_keyword + '%');
			pstat.setString(4, search_start_date);
			pstat.setString(5, search_end_date);

			try(ResultSet rs = pstat.executeQuery();){
				while(rs.next()) {
					int seq = rs.getInt("seq");
					String language = rs.getString("language");
					String title = rs.getString("title");
					String contents = rs.getString("contents");
					String img_photo = rs.getString("img_photo");
					String writer = rs.getString("writer");
					Timestamp write_date = rs.getTimestamp("write_date");
					int view_count = rs.getInt("view_count");
					int report_count = rs.getInt("report_count");
					Date start_date = rs.getDate("start_date");
					Date end_date = rs.getDate("end_date");

					list.add(new NoticeBoardDto(seq, language, title, contents,img_photo, write_date,
							view_count,report_count,writer,start_date, end_date));
				}

			}

		}return list;
	}
	
	public int reportNotice(int seq) throws Exception {
		String sql = "update noticeboard set report_count = report_count + 1 where seq=?";
		
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setInt(1,seq);
			int result = pstat.executeUpdate();
			
			return result;
			
		}
	}
	
	
	//지은씨 파트 끝
	
	

	
	
}
