package commit.backend.dto;

public class NewContestDto {
		
	private String title;
	private String direct_url;
	public NewContestDto(String title, String direct_url) {
		super();
		this.title = title;
		this.direct_url = direct_url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDirect_url() {
		return direct_url;
	}
	public void setDirect_url(String direct_url) {
		this.direct_url = direct_url;
	}
		
	
}
