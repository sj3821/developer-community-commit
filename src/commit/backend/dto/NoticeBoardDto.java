package commit.backend.dto;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class NoticeBoardDto {
	private int seq;
	private String language;
	private String title;
	private String contents;
	private String img_photo;
	private Timestamp write_date;
	private int view_count;
	private int report_count;
	private String writer;
	private Date start_date;
	private Date end_date;
	private String sDate;
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	
	
	public String getsDate() {
		long write_date = this.write_date.getTime(); //글이 쓰인 시점
		long current_date = System.currentTimeMillis(); //메서드 콜되는 현재시점

		long gapTime = (current_date  - write_date)/1000; //millisecond 이므로
		
		if(gapTime < 60) {
			return "방금 전";
		}else if(gapTime < 300) {
			return "5분 이내";
		}else if(gapTime < 3600) {
			return "1시간 이내";
		}else if(gapTime < 86400) {
			return "24시간 이내";
		}else {
			return sDate;
		}
	}
	public void setsDate(String sDate) {
		this.sDate = sDate;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getImg_photo() {
		return img_photo;
	}
	public void setImg_photo(String img_photo) {
		this.img_photo = img_photo;
	}
	public Timestamp getWrite_date() {
		return write_date;
	}
	public void setWrite_date(Timestamp write_date) {
		this.write_date = write_date;
	}
	public int getView_count() {
		return view_count;
	}
	public void setView_count(int view_count) {
		this.view_count = view_count;
	}
	public int getReport_count() {
		return report_count;
	}
	public void setReport_count(int report_count) {
		this.report_count = report_count;
	}
	public String getWriter() {
		return writer;
	}
	public void setWriter(String writer) {
		this.writer = writer;
	}
	public Date getStart_date() {
		return start_date;
	}
	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}
	public Date getEnd_date() {
		return end_date;
	}
	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}
	
	public NoticeBoardDto(int seq, String language, String title, String contents, String img_photo, Timestamp write_date,
			int view_count, int report_count, String writer, Date start_date, Date end_date) {
		super();
		this.seq = seq;
		this.language = language;
		this.title = title;
		this.contents = contents;
		this.img_photo = img_photo;
		this.write_date = write_date;
		this.view_count = view_count;
		this.report_count = report_count;
		this.writer = writer;
		this.start_date = start_date;
		this.end_date = end_date;
		if(write_date == null) {
			this.sDate = null;
		}else {
			this.sDate = new SimpleDateFormat("YYYY-MM-dd").format(write_date);
		}
	}
	public NoticeBoardDto() {
		super();
	}
	
	
	
	

}
