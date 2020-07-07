package commit.backend.dto;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class NoticeCommentDto {
	private int seq;
	private String contents;
	private String writer;
	private Timestamp write_date;
	private int parent_seq;
	private int report_count;
	private String sdate;
	
	public NoticeCommentDto(int seq, String contents, String writer, Timestamp write_date, int parent_seq,
			int report_count) {
		super();
		this.seq = seq;
		this.contents = contents;
		this.writer = writer;
		this.write_date = write_date;
		this.parent_seq = parent_seq;
		this.report_count = report_count;
		this.sdate = new SimpleDateFormat("yyyy.MM.dd hh:mm").format(this.write_date);
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public Timestamp getWrite_date() {
		return write_date;
	}

	public void setWrite_date(Timestamp write_date) {
		this.write_date = write_date;
	}

	public int getParent_seq() {
		return parent_seq;
	}

	public void setParent_seq(int parent_seq) {
		this.parent_seq = parent_seq;
	}

	public int getReport_count() {
		return report_count;
	}

	public void setReport_count(int report_count) {
		this.report_count = report_count;
	}
	
	public String getSdate() {
		
		long writed_date = this.write_date.getTime();
		long current_date = System.currentTimeMillis();
		
		long gapTime = (current_date - writed_date)/1000;
		if(gapTime<60) {
			return "방금 전";
		}
		else if(gapTime <300){
			return "5분 이내";
		}
		else if(gapTime <3600){
			return "1시간 이내";
		}
		else if(gapTime <86400){
			return "24시간 이내";
		}
		else {
			return sdate;
		}
	}
	
	public void setSdate(String sdate) {
		this.sdate = sdate;
	}
	
}
