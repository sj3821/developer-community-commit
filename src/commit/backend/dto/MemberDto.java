package commit.backend.dto;


public class MemberDto {
	private String id;
	private String pw;
	private String name;
	private String phone;
	private String email;
	private String zipcode;
	private String address1;
	private String address2;
	private String birthday;
	private String career;
	private String agreement;
	private int hintQnum;
	private String hintA;
	private String regist_date;
	public MemberDto(String id, String pw, String name, String phone, String email, String zipcode, String address1,
			String address2, String birthday, String career, String agreement, int hintQnum, String hintA,
			String regist_date) {
		super();
		this.id = id;
		this.pw = pw;
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.zipcode = zipcode;
		this.address1 = address1;
		this.address2 = address2;
		this.birthday = birthday;
		this.career = career;
		this.agreement = agreement;
		this.hintQnum = hintQnum;
		this.hintA = hintA;
		this.regist_date = regist_date;
	}
	public MemberDto() {
		// TODO Auto-generated constructor stub
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getCareer() {
		return career;
	}
	public void setCareer(String career) {
		this.career = career;
	}
	public String getAgreement() {
		return agreement;
	}
	public void setAgreement(String agreement) {
		this.agreement = agreement;
	}
	public int getHintQnum() {
		return hintQnum;
	}
	public void setHintQnum(int hintQnum) {
		this.hintQnum = hintQnum;
	}
	public String getHintA() {
		return hintA;
	}
	public void setHintA(String hintA) {
		this.hintA = hintA;
	}
	public String getRegist_date() {
		return regist_date;
	}
	public void setRegist_date(String regist_date) {
		this.regist_date = regist_date;
	}
	
	

	
	
	
	
	
	
	
}
