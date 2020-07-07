package commit.backend.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import commit.backend.dao.AdminDao;
import commit.backend.dao.VisitCountDao;
import commit.backend.dto.AdminBoardDto;
import commit.backend.dto.AdminMemberDto;

@WebServlet("*.admin")
public class AdminController extends HttpServlet {
	public static int boardSort = 0;
	public static int memberSort = 0;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf8");
		String contextPath = request.getContextPath();
		String requestURI = request.getRequestURI();
		String cmd = requestURI.substring(contextPath.length());
		System.out.println("command : " + cmd);
		AdminDao adao = new AdminDao();
		try {
			//이수지 파트 시작
			if(cmd.contentEquals("/admin/dashboard.admin")) {


				int totalMemberCount = adao.countTotalMember();
				int totalNoticeCount = adao.totalNoticeCount();
				
				Map<String, Integer> notice = new HashMap<>();
				notice=adao.noticeByLang();
		
				
				
				System.out.println(notice.get("java"));
				System.out.println(notice.get("C/C++"));
				System.out.println(notice.get("C#"));
				System.out.println(notice.get("python"));
				System.out.println(notice.get("기타"));
				
				
				request.setAttribute("totalMemberCount", totalMemberCount);
				request.setAttribute("notice", notice);
				request.setAttribute("totalNoticeCount", totalNoticeCount);
				
				//0521 추가
				Map<String, Integer> member = new HashMap<>();
				member=adao.memberByLang();
				request.setAttribute("member", member);
				//0521 추가 끝
				
				//0522 추가
				int totalContestCount = adao.totalContestCount();
				request.setAttribute("totalContestCount", totalContestCount);
				
				Map<String,Integer> contest = new HashMap<>();
				contest = adao.contestByFields();
				request.setAttribute("contest", contest);
				
				int NewMemberThanYesterday = adao.NewMemberThanYesterday();
				request.setAttribute("NewMemberThanYesterday", NewMemberThanYesterday);
				
				int NewNoticeThanYesterday = adao.NewNoticeThanYesterday();
				request.setAttribute("NewNoticeThanYesterday", NewNoticeThanYesterday);
				
//				int NewContestThanYesterday = adao.NewContestThanYesterday();
//				request.setAttribute("NewContestThanYesterday", NewContestThanYesterday);
				//0522 추가 끝
				
				System.out.println("totalMemberCount : " + totalMemberCount);
				System.out.println("totalNoticeCount : " + totalNoticeCount);
				
				System.out.println("notice : " + notice);
				System.out.println("member : " + member);
				
			
				Map<String, Integer> qna = new HashMap<>();
				qna=adao.qnaByLang();
				request.setAttribute("qna", qna);
				
				
				VisitCountDao vcdao = new VisitCountDao();
				
				int todayVisitCount=vcdao.getVisitTodayCount();
				int totalVisitCount = vcdao.getVisitTotalCount();
				
				request.setAttribute("todayVisitCount", todayVisitCount);
				request.setAttribute("totalVisitCount", totalVisitCount);
				
				RequestDispatcher rd = request.getRequestDispatcher("/admin/admin_main.jsp");
				rd.forward(request, response);

			}
			//이수지 파트 끝
			
			
			
			//상빈씨 파트 시작
			// 공모전 가져오기
			else if(cmd.contentEquals("/admin/toContest.admin")) {
				try {
					adao.insertSite();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			// 게시글 관리 
			else if(cmd.contentEquals("/admin/toBoard.admin")) {
				int page=1;
				String[] delete = {};
				try {
					page = Integer.parseInt(request.getParameter("page"));
				}catch(Exception e) {
					page = 1;
				}
				try {
					int s = Integer.parseInt(request.getParameter("sort"));
					if(s > -1 && s < 4) {
						boardSort = s;
					}
				}catch(Exception e) {}
				
				try {
					delete = request.getParameterValues("delete");
					if(delete != null) {
						List<String> qna = new ArrayList<>(), notice = new ArrayList<>();
						for(String s : delete) {
							String[] arr = s.split(" ");
							if(arr[0].equals("질문")) {
								qna.add(arr[1]);
							}else {
								notice.add(arr[1]);
							}
						}
						adao.addMemberReportCount("QNABOARD" , qna);
						adao.addMemberReportCount("NOTICEBOARD" , notice);
						adao.deleteBoard("QNABOARD" , qna);
						adao.deleteBoard("NOTICEBOARD" , notice);
					}
				}catch(Exception e) {e.printStackTrace();}

				List<AdminBoardDto> list = null;
				try {
					list = adao.getAllBoard(page , boardSort);
					request.setAttribute("admin_board" , list);
					request.setAttribute("page" , page);
					request.setAttribute("sort" , boardSort);
					String navi = adao.getNavi(page , boardSort , "toBoard.admin");
					request.setAttribute("navi" , navi);
					RequestDispatcher rd = request.getRequestDispatcher("admin_board.jsp");
					rd.forward(request, response);				
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			// 멤버관리
			if(cmd.contentEquals("/admin/toMember.admin")) {
				int page=1;
				String[] delete = {};
				try {
					page = Integer.parseInt(request.getParameter("page"));
				}catch(Exception e) {
					page = 1;
				}
				try {
					int s = Integer.parseInt(request.getParameter("sort"));
					if(s > -1 && s < 4) {
						memberSort = s;
					}
				}catch(Exception e) {}
				
				try {
					delete = request.getParameterValues("delete");
					if(delete != null) {
						List<String> memeber = new ArrayList<>();
						for(String s : delete) {
							memeber.add(s);
						}
						adao.deleteBoard("member" , memeber);
					}
				}catch(Exception e) {e.printStackTrace();}
				
				List<AdminMemberDto> list = null;
				try {
					list = adao.getAllMember(page , memberSort);
					request.setAttribute("admin_member" , list);
					request.setAttribute("page" , page);
					request.setAttribute("sort" , memberSort);
					String navi = adao.getNavi(page , memberSort , "toMember.admin");
					request.setAttribute("navi" , navi);
					RequestDispatcher rd = request.getRequestDispatcher("admin_member.jsp");
					rd.forward(request, response);				
				}catch(Exception e) {
					e.printStackTrace();
				}
				
			}
					
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
