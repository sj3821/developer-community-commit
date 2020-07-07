package commit.backend.dto;

import java.sql.Date;
import java.sql.Timestamp;

public class ContestDto {
	private String img_poster;
	private String title;
	private String origainze;
	private Date start_date;
	private Date end_date;
	private String direct_url;
	private String[] content_field;
	
	public ContestDto(String img_poster, String title, String origainze, Date start_date, Date end_date,
			String direct_url, String[] content_field) {
		this.img_poster = img_poster;
		this.title = title;
		this.origainze = origainze;
		this.start_date = start_date;
		this.end_date = end_date;
		this.direct_url = direct_url;
		this.content_field = content_field;
	}

	public String getImg_poster() {
		return img_poster;
	}

	public String getTitle() {
		return title;
	}

	public String getOrigainze() {
		return origainze;
	}

	public Date getStart_date() {
		return start_date;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public String getDirect_url() {
		return direct_url;
	}

	public String[] getContent_field() {
		return content_field;
	}
		
}
