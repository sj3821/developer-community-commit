package commit.backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import commit.backend.dto.NoticeCommentDto;
import commit.backend.dto.QnaCommentDto;

public class CommentDao {
	
	private Connection getConnection() throws Exception {

		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/dbcp");

		return ds.getConnection();

	}
	
	//태훈씨 파트 시작
	public List<QnaCommentDto> selectQnaComment(int parent_seq) throws Exception{
		String sql = "select * from qnaboard_comment where parent_seq=? order by seq asc";
		
		List<QnaCommentDto> result = new ArrayList<>();
		
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setInt(1, parent_seq);
			try(ResultSet rs = pstat.executeQuery();){
				while(rs.next()) {
					int seq = rs.getInt("seq");
					String contents = rs.getString("contents");
					String writer = rs.getString("writer");
					Timestamp write_date = rs.getTimestamp("write_date");
					int report_count = rs.getInt("report_count");
					result.add(new QnaCommentDto(seq,contents,writer,write_date,parent_seq,report_count));
				}
			}
		}return result;
	}
	
	public int writeQnaComment(int parent_seq , String writer, String contents) throws Exception{
		String sql = "insert into qnaboard_comment values(qnacomment_seq.nextval,?,?,sysdate,?,0)";
		
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, contents);
			pstat.setString(2, writer);
			pstat.setInt(3, parent_seq);
			
			int result = pstat.executeUpdate();
			//con.commit();
			return result;
		}
		
	}
	
	public int deleteQnaComment(int parent_seq, int seq) throws Exception {
		String sql = "delete from qnaboard_comment where parent_seq=? and seq=?";
		
		try(Connection con =this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			
			pstat.setInt(1,parent_seq);
			pstat.setInt(2,seq);
			int result = pstat.executeUpdate();
			//con.commit();
			return result;
		}
	}
	
	public int reportQnaComment(int parent_seq, int seq) throws Exception {
		String sql = "update qnaboard_comment set report_count = report_count + 1 where parent_seq=? and seq=?";
		
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setInt(1, parent_seq);
			pstat.setInt(2, seq);
			int result = pstat.executeUpdate();
			//con.commit();
			return result;
			
		}
	}
	
	public int editQnaComment(int parent_seq, int seq, String contents) throws Exception {
		String sql = "update qnaboard_comment set contents = ? where parent_seq=? and seq=?";
		
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, contents);
			pstat.setInt(2, parent_seq);
			pstat.setInt(3, seq);
			int result = pstat.executeUpdate();
			//con.commit();
			return result;
			
		}
	}
	
	//태훈씨 파트 끝
	//지은씨 파트 시작
	public List<NoticeCommentDto> selectNoticeComment(int parent_seq) throws Exception{
		String sql = "select * from noticeboard_comment where parent_seq=? order by seq asc";

		List<NoticeCommentDto> result = new ArrayList<>();

		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setInt(1, parent_seq);
			try(ResultSet rs = pstat.executeQuery();){
				while(rs.next()) {
					int seq = rs.getInt("seq");
					String contents = rs.getString("contents");
					String writer = rs.getString("writer");
					Timestamp write_date = rs.getTimestamp("write_date");
					int report_count = rs.getInt("report_count");
					result.add(new NoticeCommentDto(seq,contents,writer,write_date,parent_seq,report_count));
				}
			}
		}return result;
	}

	public int writeNoticeComment(int parent_seq , String writer, String contents) throws Exception{
		String sql = "insert into noticeboard_comment values(noticecomment_seq.nextval,?,?,sysdate,?,0)";

		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, contents);
			pstat.setString(2, writer);
			pstat.setInt(3, parent_seq);

			int result = pstat.executeUpdate();
			con.commit();
			return result;
		}

	}

	public int deleteNoticeComment(int parent_seq, int seq) throws Exception {
		String sql = "delete from noticeboard_comment where parent_seq=? and seq=?";

		try(Connection con =this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){

			pstat.setInt(1,parent_seq);
			pstat.setInt(2,seq);
			int result = pstat.executeUpdate();
			con.commit();
			return result;
		}
	}

	public int reportNoticeComment(int parent_seq, int seq) throws Exception {
		String sql = "update qnaboard_comment set report_count = report_count + 1 where parent_seq=? and seq=?";

		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setInt(1, parent_seq);
			pstat.setInt(2, seq);
			int result = pstat.executeUpdate();
			con.commit();
			return result;

		}
	}

	public int editNoticeComment(int parent_seq, int seq, String contents) throws Exception {
		String sql = "update noticeboard_comment set contents = ? where parent_seq=? and seq=?";

		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setString(1, contents);
			pstat.setInt(2, parent_seq);
			pstat.setInt(3, seq);
			int result = pstat.executeUpdate();
			con.commit();
			return result;

		}
	}
	
	
	
	
}
