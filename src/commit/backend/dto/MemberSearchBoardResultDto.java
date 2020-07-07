package commit.backend.dto;

import java.sql.Date;

public class MemberSearchBoardResultDto {
	private String name;
	private String id;
	private Date start_date;
	private Date end_date;
	private String email;
	private String language;
	private String career;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getCareer() {
		return career;
	}
	public void setCareer(String career) {
		this.career = career;
	}
	public MemberSearchBoardResultDto(String id, String name,  Date start_date, Date end_date, String email,
			String language, String career) {
		super();
		this.name = name;
		this.id = id;
		this.start_date = start_date;
		this.end_date = end_date;
		this.email = email;
		this.language = language;
		this.career = career;
	}
	
	public MemberSearchBoardResultDto(String id, String name,  Date start_date, Date end_date, String email,
			String career) {
		super();
		this.name = name;
		this.id = id;
		this.start_date = start_date;
		this.end_date = end_date;
		this.email = email;
		this.career = career;
	}
	public MemberSearchBoardResultDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
}
