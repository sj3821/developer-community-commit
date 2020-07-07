package commit.backend.dto;

public class findLangDto {
	private String user_id;
	private String language;
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public findLangDto(String user_id, String language) {
		super();
		this.user_id = user_id;
		this.language = language;
	}
	public findLangDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
