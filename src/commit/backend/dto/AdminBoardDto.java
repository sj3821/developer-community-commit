package commit.backend.dto;

import java.sql.Date;

public class AdminBoardDto {
	private String type;
	private int seq;
	private String title;
	private int report_count;
	private String writer;
	private Date write_date;
	
	public AdminBoardDto(String type, int seq, String title, int report_count, String writer,
			Date write_date) {
		this.type = type;
		this.seq = seq;
		this.title = title;
		this.report_count = report_count;
		this.writer = writer;
		this.write_date = write_date;
	}

	public String getType() {
		return type;
	}

	public int getSeq() {
		return seq;
	}

	public String getTitle() {
		return title;
	}


	public int getReport_count() {
		return report_count;
	}

	public String getWriter() {
		return writer;
	}

	public Date getWrite_date() {
		return write_date;
	}
	
	
	
	
}
