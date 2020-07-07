package commit.backend.dto;

import java.sql.Date;

public class AdminMemberDto {
	private String id;
	private String name;
	private String phone;
	private String email;
	private Date regist_date;
	private int report_count;
	
	public AdminMemberDto(String id, String name, String phone, String email, Date regist_date, int report_count) {
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.regist_date = regist_date;
		this.report_count = report_count;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public Date getRegist_date() {
		return regist_date;
	}

	public int getReport_count() {
		return report_count;
	}	
}
