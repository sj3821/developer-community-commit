package commit.backend.dto;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class QnaBoardDto {
	private int seq;
	private String title;
	private String contents;
	private String writer;
	private Timestamp write_date;
	private int view_count;
	private String ip_addr;
	private String language;
	private int report_count;
	private String img_photo;
	private String sdate;
	
	public QnaBoardDto(int seq, String title, String contents, String writer, Timestamp write_date, int view_count,
			String ip_addr, String language, int report_count, String img_photo) {
		super();
		this.seq = seq;
		this.title = title;
		this.contents = contents;
		this.writer = writer;
		this.write_date = write_date;
		this.view_count = view_count;
		this.ip_addr = ip_addr;
		this.language = language;
		this.report_count = report_count;
		this.img_photo = img_photo;
		if(write_date == null) {
			this.sdate = null;
		}else {
			this.sdate = new SimpleDateFormat("YYYY-MM-dd").format(write_date);
		}
		
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

	public int getView_count() {
		return view_count;
	}

	public void setView_count(int view_count) {
		this.view_count = view_count;
	}

	public String getIp_addr() {
		return ip_addr;
	}

	public void setIp_addr(String ip_addr) {
		this.ip_addr = ip_addr;
	}
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getReport_count() {
		return report_count;
	}

	public void setReport_count(int report_count) {
		this.report_count = report_count;
	}

	public String getImg_photo() {
		return img_photo;
	}

	public void setImg_photo(String img_photo) {
		this.img_photo = img_photo;
	}
	
	public String getSdate() {
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
			return sdate;
		}
	}
	public void setSdate(String sdate) {
		this.sdate = sdate;
	}
	
}
