package commit.backend.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import commit.backend.dao.BoardDao;
import commit.backend.dao.ContestBoardDao;
import commit.backend.dao.MemberSearchBoardDao;
import commit.backend.dto.ContestDto;
import commit.backend.dto.MemberDto;
import commit.backend.dto.MemberSearchBoardDto;
import commit.backend.dto.MemberSearchBoardResultDto;
import commit.backend.dto.NoticeBoardDto;
import commit.backend.dto.QnaBoardDto;
import commit.backend.dto.findLangDto;
import commit.backend.statics.Configuration;


@WebServlet("*.board")
public class BoardController extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf8");
		response.setCharacterEncoding("utf8");
		String contextPath = request.getContextPath();
		System.out.println(contextPath);
		String requestURI = request.getRequestURI();
		System.out.println(requestURI);
		String cmd = requestURI.substring(contextPath.length());
		System.out.println("command : " + cmd);

		BoardDao dao = new BoardDao();

		try {
			
			//태훈씨 파트 시작 
			if(cmd.contentEquals("/selectQnaList.board")){
				System.out.println("도착");
				int cpage=1;
				try {
					cpage = Integer.parseInt(request.getParameter("cpage"));
				}catch(Exception e) {

				}
				
				int totalCount = dao.qnaArticleCount();
				System.out.println(totalCount);
				if(cpage>1) {
					totalCount = totalCount - (cpage-1)*Configuration.recordCountPerPage; 
				}
				request.getSession().setAttribute("totalCount",totalCount);

				List<QnaBoardDto> qnaList = dao.selectQnaList(cpage);
				String qnaNavi = dao.qnaPageNavi(cpage);
				request.setAttribute("qnaList", qnaList);                     
				request.setAttribute("qnaNavi", qnaNavi);
				RequestDispatcher rd = request.getRequestDispatcher("/board/qna_board.jsp");
				rd.forward(request, response);
			}
			else if(cmd.contentEquals("/writeQna.board")) {
				MemberDto key = (MemberDto)request.getSession().getAttribute("mem");
				System.out.println("key :" + key.getId());
				
				String title = request.getParameter("title");
				String contents = request.getParameter("contents");
				String writer = key.getId();
				String ip =  request.getRemoteAddr();
				String language = request.getParameter("language");
				String img_photo = request.getParameter("img_photo");
			
				int result = dao.writeQna( new QnaBoardDto(0,title,contents,writer,null,0,ip,language,0,img_photo));

				if(result > 0) {
					response.sendRedirect("selectQnaList.board");
					System.out.println("와우!");
				}
				else {
					response.sendRedirect("/index.jsp");
				}

			}
			else if(cmd.contentEquals("/selectQnaDetail.board")) {
				System.out.println("도착");
				
				int seq = Integer.parseInt(request.getParameter("seq"));
				String writer = request.getParameter("writer");
				MemberDto key = (MemberDto)request.getSession().getAttribute("mem");
				String loginid = key.getId();
				System.out.println(loginid);
				
				QnaBoardDto qbdto = dao.selectQnaDetail(seq,writer,loginid);
				
				request.setAttribute("qbdto", qbdto);
				request.setAttribute("sdate", qbdto.getSdate());
				if(request.getParameter("success") != null) {
					request.setAttribute("success", "success");
				}
				RequestDispatcher rd = request.getRequestDispatcher("/board/qna_contents.jsp");
				rd.forward(request, response);
			}
			else if(cmd.contentEquals("/deleteQna.board")) {
				int seq = Integer.parseInt(request.getParameter("seq"));
				
				int result = dao.deleteQna(seq);
				if(result>0) {
					response.sendRedirect("selectQnaList.board");
				}
				else {
					response.sendRedirect("index.jsp");
				}
			
			}
			else if(cmd.contentEquals("/editQna.board")) {
				int seq = Integer.parseInt(request.getParameter("seq"));
				String writer = request.getParameter("writer");
				String title = request.getParameter("title");
				String contents = request.getParameter("contents");
				String img_photo = request.getParameter("img_photo");
				int result = dao.editQna(seq, title, contents, img_photo);
				
				if(result>0) {
					response.sendRedirect("selectQnaDetail.board?seq=" + seq + "&&writer="+writer);
				}else {
					response.sendRedirect("error.jsp");
				}
			}
			
			else if(cmd.contentEquals("/searchQna.board")) {
				String qna_keyword = request.getParameter("qna_keyword");
				String[] languages = request.getParameterValues("language");

				System.out.println("================================");
				//BoardDao dao = new BoardDao();
				List <QnaBoardDto> list = new ArrayList<>();


				if(!(qna_keyword.contentEquals("")) && languages.length!=1) {
					System.out.println("searchByKeywordAndLanguage");
					// 키워드 값의 존재 o, 언어 값 o인 경우
					list = dao.searchQnaByKeywordAndLanguage(qna_keyword, languages);

					RequestDispatcher rd = request.getRequestDispatcher("/board/qna_board.jsp");
					request.setAttribute("qnaList", list);
					System.out.println(list.size());
					rd.forward(request, response);

				}else if(qna_keyword.contentEquals("") && languages.length!=1) {
					// 키워드 값의 존재 x, 언어 값 o인 경우
					System.out.println("searchQnaByLanguages");
					list = dao.searchQnaByLanguages(languages);
					
					RequestDispatcher rd = request.getRequestDispatcher("/board/qna_board.jsp");
					request.setAttribute("qnaList", list);
					System.out.println(list.size());
					rd.forward(request, response);

				}else if(!(qna_keyword.contentEquals("")) && languages.length==1) {
					// 키워드 값의 존재 o, 언어 값x 인 경우
					System.out.println("searchQnaByKeyword");
					list = dao.searchQnaByKeyword(qna_keyword);
					
					RequestDispatcher rd = request.getRequestDispatcher("/board/qna_board.jsp");
					request.setAttribute("qnaList", list);
					System.out.println(list.size());
					rd.forward(request, response);

				}else if(qna_keyword.contentEquals("") && languages.length==1) {
					// 키워드 값의 존재 x, 언어 값 x인 경우
					System.out.println("selectAll");
					response.sendRedirect("selectQnaList.board");

				}else {
					response.sendRedirect("error.jsp");
				}

			}
			
			else if(cmd.contentEquals("/reportQna.board")) {
				System.out.println("도착");
				int seq = Integer.parseInt(request.getParameter("seq"));
				String writer = request.getParameter("writer");
				MemberDto key = (MemberDto)request.getSession().getAttribute("mem");
				String loginid = key.getId();
				int result = dao.reportQna(seq);
				System.out.println("결과 : " + result);
				if(result>0) {
					request.setAttribute("seq", seq);
					request.setAttribute("writer",writer);
					request.setAttribute("loginid",loginid);
					request.setAttribute("success", "success");
					RequestDispatcher rd = request.getRequestDispatcher("selectQnaDetail.board");
					rd.forward(request, response);
					//response.sendRedirect("selectQnaDetail.board?seq=" + seq);
//					String resp = "성공";
//					response.getWriter().append(resp);
				}else {
					response.sendRedirect("error.jsp");
				}
				
			}
			//태훈씨 파트 끝
			
			//이수지 파트 시작
			
			else if(cmd.contentEquals("/board/membersearch.board")) {
				String name = request.getParameter("name");
				String[] languages = request.getParameterValues("language");
				String etcLanguage = request.getParameter("etc_lang");
				String career = request.getParameter("career");
				String start_date = request.getParameter("start_date");
				String end_date = request.getParameter("end_date");
		
				
				MemberSearchBoardDao msbdao = new MemberSearchBoardDao();
				List<MemberSearchBoardDto> list= new ArrayList();
				
				
				list = msbdao.select(name,languages,etcLanguage,career,start_date,end_date);
				
				
				List<MemberSearchBoardResultDto> tempList = new ArrayList();
				List<findLangDto> lang = msbdao.findlang();
				for(MemberSearchBoardDto dto : list) {
					String rid = dto.getId();
					String rname = dto.getName();
					Date rstart_date = dto.getStart_date();
					Date rend_date = dto.getEnd_date();
					String langs="";
					
					for(int j=0;j<lang.size();j++) { 
						String user_id = lang.get(j).getUser_id();
					 if(rid.contentEquals(user_id)) {
						 langs = lang.get(j).getLanguage(); 
						} 
					}
					
					String remail = dto.getEmail();
					String rcareer = dto.getCareer();
					tempList.add(new MemberSearchBoardResultDto(rid,rname,rstart_date,rend_date,remail,langs,rcareer));
				}
				
				
				
				request.setAttribute("list", tempList);
		
				RequestDispatcher rd = request.getRequestDispatcher("/board/membersearch_board.jsp");
				rd.forward(request, response);
			}
			//이수지 파트 끝
			
			
			//지은씨 파트 시작
			else if(cmd.contentEquals("/selectNoticeList.board")){
				int cpage=1;
				try {
					cpage = Integer.parseInt(request.getParameter("cpage"));
				}catch(Exception e) {

				}
				//BoardDao dao = new BoardDao();
				List<NoticeBoardDto> list = dao.selectNoticeList(cpage);
				System.out.println(list.size());
				String navi = dao.getPageNavi(cpage);

				request.setAttribute("NoticeList", list);             
				request.setAttribute("NoticeNavi", navi);

				RequestDispatcher rd = request.getRequestDispatcher("/board/notice_board.jsp");
				rd.forward(request, response);

			}else if(cmd.contentEquals("/selectNoticeDetail.board")) {
				//공고게시판 내용 보기
				System.out.println("도착");
				int seq = Integer.parseInt(request.getParameter("seq"));
				String writer = request.getParameter("writer");
				MemberDto mem = (MemberDto)request.getSession().getAttribute("mem");
				String id = mem.getId();
				
				System.out.println(seq +":"+ writer +":"+ id);
				
				if(!(writer.contentEquals(id))) {
					dao.NoticeViewCountUp(seq);
				}
				
				NoticeBoardDto nbdto = dao.selectNoticeDetail(seq);
				request.setAttribute("nbdto", nbdto);
				if(request.getParameter("success") != null) {
					request.setAttribute("success", "success");
				}
				RequestDispatcher rd = request.getRequestDispatcher("/board/notice_contents.jsp");
				rd.forward(request, response);
				//끝

			}else if(cmd.contentEquals("/editNotice.board")) {
				//BoardDao dao = new BoardDao();

				int seq = Integer.parseInt(request.getParameter("seq"));
				String language = request.getParameter("language");
				String writer = request.getParameter("writer");
				String title = request.getParameter("title");
				String contents = request.getParameter("contents");
				
				String day1 =  request.getParameter("start_date");
				Date start_date = Date.valueOf(day1);

				String day2 = request.getParameter("end_date");
				Date end_date = Date.valueOf(day2);
				
				System.out.println(seq+" + "+language+" + "+title+" + "+contents+" + " +start_date + "+ " + end_date);

				int result = dao.editNotice(seq, start_date, end_date, language, title, contents);

				if(result>0) {
					response.sendRedirect("/selectNoticeDetail.board?seq="+seq+"&writer="+writer);
				}else {
					response.sendRedirect("error.jsp");
				}

			}else if(cmd.contentEquals("/deleteNotice.board")) {
				//BoardDao dao = new BoardDao();
				int seq = Integer.parseInt(request.getParameter("seq"));
				int result = dao.deleteNotice(seq);

				if(result>0) {
					response.sendRedirect("/selectNoticeList.board");
				}else {
					response.sendRedirect("error.jsp");
				}

			}else if(cmd.contentEquals("/writeNotice.board")) {
				//BoardDao dao = new BoardDao();
				NoticeBoardDto nbdto = new NoticeBoardDto();

				String language= request.getParameter("language");
				String title = request.getParameter("title");
				String contents = request.getParameter("contents");
				String img_photo;
				if(request.getParameter("img_photo") != null) {
					img_photo = request.getParameter("img_photo");
				}else {
					img_photo = nbdto.getImg_photo();
				}
				int view_count = 0;
				int report_count = 0;
				//loginInfo 세선 값을 가져옴
				MemberDto mem = (MemberDto)request.getSession().getAttribute("mem");
				String writer = mem.getId();

				String day1 =  request.getParameter("start_date");
				Date start_date = Date.valueOf(day1);

				String day2 = request.getParameter("end_date");
				Date end_date = Date.valueOf(day2);

				System.out.println(language);

				int result = dao.writeNotice(language, title, contents, img_photo, view_count, report_count, writer, start_date, end_date);

				if(result > 0) {
					response.sendRedirect("/selectNoticeList.board");
				}
				else {
					response.sendRedirect("error.jsp");
				}

			}else if(cmd.contentEquals("/reportNotice.board")) {
					System.out.println("도착");
					int seq = Integer.parseInt(request.getParameter("seq"));
					
					int result = dao.reportNotice(seq);
					System.out.println("결과 : " + result);
					if(result>0) {
						request.setAttribute("seq", seq);
						request.setAttribute("success", "success");
						RequestDispatcher rd = request.getRequestDispatcher("selectNoticeDetail.board");
						rd.forward(request, response);

					}else {
						response.sendRedirect("error.jsp");
					}
				
			}else if(cmd.contentEquals("/searchNotice.board")) {
				String notice_keyword = request.getParameter("notice_keyword");
				String search_start_date = request.getParameter("search_start_date");
				String search_end_date = request.getParameter("search_end_date");
				String[] languages = request.getParameterValues("language");

				System.out.println("================================");
				//BoardDao dao = new BoardDao();
				List <NoticeBoardDto> list = new ArrayList<>();

				if(!(notice_keyword.contentEquals("")) && !(search_start_date.contentEquals("") && search_end_date.contentEquals("")) && languages.length!=1) {
					System.out.println("searchByKeywordAndDateAndLanguage");
					// 키워드 값의 존재 o, 날짜 값 o, 언어 값 o인 경우
					list = dao.searchByKeywordAndDateAndLanguage(notice_keyword, search_start_date, search_end_date, languages);
					
					RequestDispatcher rd = request.getRequestDispatcher("/board/notice_board.jsp");

					request.setAttribute("NoticeList", list); 
					System.out.println(list.size());
					rd.forward(request, response);

				}else if(!(notice_keyword.contentEquals("")) && !(search_start_date.contentEquals("") && search_end_date.contentEquals("")) && languages.length==1) {
					System.out.println("searchByKeywordAndDate");
					list = dao.searchByKeywordAndDate(notice_keyword, search_start_date, search_end_date);
					// 키워드 값의 존재 o, 날짜 값 o, 언어 값 x인 경우
					
					RequestDispatcher rd = request.getRequestDispatcher("/board/notice_board.jsp");
					request.setAttribute("NoticeList", list); 
					System.out.println(list.size());
					rd.forward(request, response);

				}else if(notice_keyword.contentEquals("") && !(search_start_date.contentEquals("") && search_end_date.contentEquals("")) && languages.length!=1) {
					System.out.println("searchByDateAndLanguage");
					list = dao.searchByDateAndLanguage(search_start_date, search_end_date, languages);
					// 키워드 값의 존재 x, 날짜 값 o, 언어 값 o인 경우

					RequestDispatcher rd = request.getRequestDispatcher("/board/notice_board.jsp");
					request.setAttribute("NoticeList", list); 
					System.out.println(list.size());
					rd.forward(request, response);

				}else if(!(notice_keyword.contentEquals("")) && (search_start_date.contentEquals("") && search_end_date.contentEquals("")) && languages.length!=1) {
					System.out.println("searchByKeywordAndLanguage");
					// 키워드 값의 존재 o, 날짜 값 x, 언어 값 o인 경우
					list = dao.searchByKeywordAndLanguage(notice_keyword, languages);

					RequestDispatcher rd = request.getRequestDispatcher("/board/notice_board.jsp");
					request.setAttribute("NoticeList", list); 
					System.out.println(list.size());
					rd.forward(request, response);

				}else if(notice_keyword.contentEquals("") && (search_start_date.contentEquals("") && search_end_date.contentEquals("")) && languages.length!=1) {
					// 키워드 값의 존재 x, 날짜 값 x, 언어 값 o인 경우
					System.out.println("searchNoticeByLanguages");
					list = dao.searchNoticeByLanguages(languages);
					
					RequestDispatcher rd = request.getRequestDispatcher("/board/notice_board.jsp");
					request.setAttribute("NoticeList", list); 
					System.out.println(list.size());
					rd.forward(request, response);

				}else if(!(notice_keyword.contentEquals("")) && (search_start_date.contentEquals("") && search_end_date.contentEquals("")) && languages.length==1) {
					// 키워드 값의 존재 o, 날짜 값 x, 언어 값x 인 경우
					System.out.println("searchNoticeByKeyword");
					list = dao.searchNoticeByKeyword(notice_keyword);
					
					RequestDispatcher rd = request.getRequestDispatcher("/board/notice_board.jsp");
					request.setAttribute("NoticeList", list); 
					System.out.println(list.size());
					rd.forward(request, response);

				}else if(notice_keyword.contentEquals("") && !(search_start_date.contentEquals("") && search_end_date.contentEquals("")) && languages.length==1) {
					// 키워드 값의 존재 x, 날짜 값 o, 언어 값 x인 경우
					System.out.println("searchNoticeByDate");
					list = dao.searchNoticeByDate(search_start_date, search_end_date);
					
					RequestDispatcher rd = request.getRequestDispatcher("/board/notice_board.jsp");
					request.setAttribute("NoticeList", list); 
					System.out.println(list.size());
					rd.forward(request, response);

				}else if(notice_keyword.contentEquals("") && (search_start_date.contentEquals("") && search_end_date.contentEquals("")) && languages.length==1) {
					// 키워드 값의 존재 x, 날짜 값 x, 언어 값 x인 경우
					System.out.println("selectAll");
					response.sendRedirect("/selectNoticeList.board");

				}else {
					response.sendRedirect("error.jsp");
				}

			}
		
			
			
			//지은씨 파트 끝
			
			
			
			//상빈씨 파트 시작
			else if(cmd.contentEquals("/board/contest.board")) {
				ContestBoardDao cbdto = new ContestBoardDao();
				
				int page=1;
				String name = "";
				String[] field = {};
				try {
					page = Integer.parseInt(request.getParameter("page"));
				}catch(Exception e) {
					page = 1;
				}
				try {
					field = request.getParameterValues("field");
					for(String s : field) {
					System.out.println("filed "+s);
					}
				}catch(Exception e) {}
				try {
					name = request.getParameter("name");
				}catch(Exception e) {}
				List<ContestDto> list = null;
				try {
					list = cbdto.selectAllContest(page, field , name);
					request.setAttribute("contestindex" , list);
					String navi = cbdto.getContestNavi(page , field , name);
					request.setAttribute("navi" , navi);
					RequestDispatcher rd = request.getRequestDispatcher("/board/contestboard.jsp");
					rd.forward(request, response);
				}catch(Exception e) {e.printStackTrace();}
			}
			
			
			//상빈씨 파트 끝
			

		}catch(Exception e){
			e.printStackTrace();
			response.sendRedirect("/error.jsp");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doGet(request, response);
	}

}
