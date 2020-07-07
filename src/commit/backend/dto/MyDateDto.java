package commit.backend.dto;

public class MyDateDto {
	private String start_date;
	private String end_date;
	public MyDateDto(String start_date, String end_date) {
		super();
		this.start_date = start_date;
		this.end_date = end_date;
	}
	public MyDateDto() {
		// TODO Auto-generated constructor stub
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	
	
}
