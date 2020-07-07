package commit.backend.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import commit.backend.dao.CommentDao;
import commit.backend.dto.NoticeCommentDto;
import commit.backend.dto.QnaCommentDto;

@WebServlet("*.comment")
public class CommentController extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf8");
		response.setCharacterEncoding("utf8");
		String contextPath = request.getContextPath();
		System.out.println(contextPath);
		String requestURI = request.getRequestURI();
		System.out.println(requestURI);
		String cmd = requestURI.substring(contextPath.length());
		System.out.println("command : " + cmd);
		
		CommentDao dao = new CommentDao();
		
		try {
			
			//태훈씨 파트 시작
			if(cmd.contentEquals("/selectQna.comment")) {
				int parent_seq = Integer.parseInt(request.getParameter("parent_seq"));
				try {
					List<QnaCommentDto> list = dao.selectQnaComment(parent_seq);
					System.out.println(list);
					String respArr = new Gson().toJson(list);
					
					response.getWriter().append(respArr);
					
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			else if(cmd.contentEquals("/writeQna.comment")) {
				int parent_seq = Integer.parseInt(request.getParameter("parent_seq"));
				String writer = request.getParameter("writer");
				String contents = request.getParameter("contents");
				try {
					int result = dao.writeQnaComment(parent_seq, writer, contents);
					System.out.println(result);
					if(result > 0) {
						String resp = new Gson().toJson("성공");
						System.out.println("resp : " + resp);
						response.getWriter().append(resp);
						System.out.println("서버 성공");
					}
					else {
						response.sendRedirect("error.jsp");
					}	
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			else if(cmd.contentEquals("/deleteQna.comment")) {
				int parent_seq = Integer.parseInt(request.getParameter("parent_seq"));
				int seq = Integer.parseInt(request.getParameter("seq"));
				
				try {
					int result = dao.deleteQnaComment(parent_seq, seq);
					if(result > 0) {
						String resp = new Gson().toJson("성공");
						System.out.println("resp : " + resp);
						response.getWriter().append(resp);
						System.out.println("서버 성공");
					}
					else {
						response.sendRedirect("error.jsp");
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			else if(cmd.contentEquals("/reportQna.comment")) {
				System.out.println("도착");
				int parent_seq = Integer.parseInt(request.getParameter("parent_seq"));
				int seq = Integer.parseInt(request.getParameter("seq"));
				
				int result = dao.reportQnaComment(parent_seq,seq);
				System.out.println("결과 : " + result);
				if(result>0) {

					String resp = "성공";
					response.getWriter().append(resp);
				}else {
					response.sendRedirect("error.jsp");
				}
				
			}
			else if(cmd.contentEquals("/editQna.comment")) {
				System.out.println("도착");
				int parent_seq = Integer.parseInt(request.getParameter("parent_seq"));
				int seq = Integer.parseInt(request.getParameter("seq"));
				String contents = request.getParameter("contents");
				
				try {
					int result = dao.editQnaComment(parent_seq, seq, contents);
					System.out.println(result);
					if(result > 0) {
						String resp = new Gson().toJson("성공");
						System.out.println("resp : " + resp);
						response.getWriter().append(resp);
						System.out.println("서버 성공");
					}
					else {
						response.sendRedirect("error.jsp");
					}	
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			//태훈씨 파트 끝
			
			//지은씨 파트 시작
			
		else if(cmd.contentEquals("/selectNotice.comment")) {
			int parent_seq = Integer.parseInt(request.getParameter("parent_seq"));
			try {
				List<NoticeCommentDto> list = dao.selectNoticeComment(parent_seq);
				System.out.println("selectNotice.comment" + list.size());
				String respArr = new Gson().toJson(list);

				response.getWriter().append(respArr);

			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		else if(cmd.contentEquals("/writeNotice.comment")) {
			int parent_seq = Integer.parseInt(request.getParameter("parent_seq"));
			String writer = request.getParameter("writer");
			String contents = request.getParameter("contents");
			try {
				int result = dao.writeNoticeComment(parent_seq, writer, contents);
				System.out.println(result);
				if(result > 0) {
					String resp = new Gson().toJson("댓글작성 성공");
					System.out.println("resp : " + resp);
					response.getWriter().append(resp);
					System.out.println("서버 성공");
				}
				else {
					response.sendRedirect("error.jsp");
				}	
			}catch(Exception e) {
				e.printStackTrace();
			}
		}else if(cmd.contentEquals("/deleteNotice.comment")) {
			int parent_seq = Integer.parseInt(request.getParameter("parent_seq"));
			int seq = Integer.parseInt(request.getParameter("seq"));

			try {
				int result = dao.deleteNoticeComment(parent_seq, seq);
				if(result > 0) {
					String resp = new Gson().toJson("성공");
					System.out.println("resp : " + resp);
					response.getWriter().append(resp);
					System.out.println("서버 성공");
				}
				else {
					response.sendRedirect("error.jsp");
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}else if(cmd.contentEquals("/reportNotice.comment")) {
			System.out.println("도착");
			int parent_seq = Integer.parseInt(request.getParameter("parent_seq"));
			int seq = Integer.parseInt(request.getParameter("seq"));

			int result = dao.reportNoticeComment(parent_seq,seq);
			System.out.println("결과 : " + result);
			if(result>0) {

				String resp = "성공";
				response.getWriter().append(resp);
			}else {
				response.sendRedirect("error.jsp");
			}

		}else if(cmd.contentEquals("/editNotice.comment")) {
			System.out.println("도착");
			int parent_seq = Integer.parseInt(request.getParameter("parent_seq"));
			int seq = Integer.parseInt(request.getParameter("seq"));
			String contents = request.getParameter("contents");

			try {
				int result = dao.editNoticeComment(parent_seq, seq, contents);
				System.out.println(result);
				if(result > 0) {
					String resp = new Gson().toJson("댓글 수정 성공");
					System.out.println("resp : " + resp);
					response.getWriter().append(resp);
					System.out.println("서버 성공");
				}
				else {
					response.sendRedirect("error.jsp");
				}	
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
			
			
			
			//지은씨 파트 끝
			
			
			
			
			
		}catch(Exception e) {
			e.printStackTrace();
			response.sendRedirect("error.jsp");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doGet(request, response);
	}

}
