package commit.backend.dto;

public class MyBoardDto {
	private String category;
	private int seq;
	private String title;
	private String writer;
	private String write_date;
	private int view_count;
	public MyBoardDto(String category, int seq, String title, String writer, String write_date, int view_count) {
		super();
		this.category = category;
		this.seq = seq;
		this.title = title;
		this.writer = writer;
		this.write_date = write_date;
		this.view_count = view_count;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
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
	public String getWriter() {
		return writer;
	}
	public void setWriter(String writer) {
		this.writer = writer;
	}
	public String getWrite_date() {
		return write_date;
	}
	public void setWrite_date(String write_date) {
		this.write_date = write_date;
	}
	public int getView_count() {
		return view_count;
	}
	public void setView_count(int view_count) {
		this.view_count = view_count;
	}

	
}
