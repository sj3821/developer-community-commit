package commit.backend.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import commit.backend.dao.DmDao;

@WebServlet("*.dm")
public class DmController extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf8");
		String contextPath = request.getContextPath();
		String requestURI = request.getRequestURI();
		String cmd = requestURI.substring(contextPath.length());
		System.out.println("Cmd : " + cmd);
		
		DmDao ddao = new DmDao();
		
		try {
			if(cmd.contentEquals("/board/sendDm.dm")) {
				String receiver_id = request.getParameter("receiver_id");
				String sender_id = request.getParameter("sender_id");
				String title = request.getParameter("title");
				String contents = request.getParameter("contents");
				
				System.out.println(receiver_id);
				System.out.println(sender_id);
				System.out.println(title);
				System.out.println(contents);
				
				String senderAddr = ddao.findSenderEmailAddr(sender_id);
				String receiverAddr = ddao.findReceiverEmailAddr(receiver_id);
				
				System.out.println(senderAddr);
				System.out.println(receiverAddr);
				System.out.println(title);
				System.out.println(contents);
				
				ddao.sendDm(sender_id,senderAddr, receiverAddr, title, contents);
				request.setAttribute("sendresult", "success");
				
				RequestDispatcher rd = request.getRequestDispatcher("/board/sendDm.jsp");
				rd.forward(request, response);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
