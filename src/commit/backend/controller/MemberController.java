package commit.backend.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import commit.backend.dao.MemberDao;
import commit.backend.dto.MemberDto;
import commit.backend.dto.MyBoardDto;
import commit.backend.dto.MyDateDto;
import commit.backend.dto.NewContestDto;
import commit.backend.statics.Mail;

@WebServlet("*.mem")
public class MemberController extends HttpServlet {
   public static String getSHA512(String input){

      String toReturn = null;
      try {
         MessageDigest digest = MessageDigest.getInstance("SHA-512");
         digest.reset();
         digest.update(input.getBytes("utf8"));
         toReturn = String.format("%0128x", new BigInteger(1, digest.digest()));
      } catch (Exception e) {
         e.printStackTrace();
      }

      return toReturn;
   }

   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      response.setCharacterEncoding("utf8");
      String requestURI = request.getRequestURI();
      String ctxPath = request.getContextPath();
      String cmd = requestURI.substring(ctxPath.length());
      MemberDao mdao = new MemberDao();
      try {
         if(cmd.contentEquals("/signup.mem")) {
            request.setCharacterEncoding("utf8");
            String id = request.getParameter("id");
            String pw = request.getParameter("pw");
            String pw2 = this.getSHA512(pw);
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String email = request.getParameter("email");
            String zipcode = request.getParameter("zipcode");
            String address1 = request.getParameter("address1");
            String address2 = request.getParameter("address2");
            String birthday = request.getParameter("birthday");
            String career = request.getParameter("career");
            String agreement = request.getParameter("agreement");
            if(agreement == null) {
               agreement = "false";
            }

            int hintQnum = Integer.parseInt(request.getParameter("hintQnum"));
            String hintA = request.getParameter("hintA");
            String[] userlanguage = request.getParameterValues("language");

            for(int i = 0 ;i<userlanguage.length;i++) {
               mdao.addLanguage(id, userlanguage[i]);
            }


            int result = mdao.addMember(new MemberDto(id,pw2,name,phone,email,zipcode,address1,address2,birthday,career,agreement,hintQnum,hintA,null));
            if(result > 0) {
               mdao.addStartDate(id);
               response.sendRedirect("../index.jsp");
            }
         }else if(cmd.contentEquals("/duplcheck.mem")) {
            String id = request.getParameter("id");

            boolean result = mdao.isIdAvailable(id);
            if(result) {
               JsonObject resp = new JsonObject();
               resp.addProperty("check", "사용 가능한 아이디입니다.");
               response.getWriter().append(resp.toString());
            }


         }else if(cmd.contentEquals("/leave.mem")) {
            MemberDto mem = (MemberDto)request.getSession().getAttribute("mem");
            String id = mem.getId();


            boolean result = (boolean)mdao.removeMember(id);
            mdao.deletelanguage(id);
            Thread.sleep(2000);
            mdao.deletedate(id);
            Thread.sleep(2000);
            if(result) {
               request.getSession().invalidate();
            }response.sendRedirect("../index.jsp");
         }else if(cmd.contentEquals("/mywrite.mem")) {
            int cpage = 1;
            String id = (String) request.getSession().getAttribute("mid");
            try {
               cpage = Integer.parseInt(request.getParameter("cpage"));
            }catch(Exception e) {
            }

            List<MyBoardDto> myboard = mdao.getMyboard(id,cpage);
            String navi = mdao.getPageNavi(id,cpage);
            request.setAttribute("myboard", myboard);
            request.setAttribute("navi", navi);


            RequestDispatcher rd = request.getRequestDispatcher("/main/mywrite.jsp");
            rd.forward(request, response);

         }else if(cmd.contentEquals("/login.mem")) {
            String id = request.getParameter("id");
            String pw = request.getParameter("pw");

            boolean result = mdao.login(id, getSHA512(pw));

            if(result) {
               request.getSession().setAttribute("mid", id);
               MemberDto mem = mdao.mypage(id);
               request.getSession().setAttribute("mem", mem);
               List<String> language = mdao.mylanguage(id);
               request.getSession().setAttribute("language", language);
               MyDateDto date = mdao.mydate(id);
               request.getSession().setAttribute("date", date);

               response.sendRedirect("/index.jsp");

            }else{
               response.sendRedirect("main/loginalert.jsp");
            }

         }else if(cmd.contentEquals("/logout.mem")) {
            request.getSession().invalidate();
            response.sendRedirect("../index.jsp");
         }else if(cmd.contentEquals("/mail.mem")) {
            String mail = request.getParameter("email");
            boolean result = mdao.isAbleMail(mail);
            if(result) {
               int random = Mail.gmailSend(mail);

               JsonObject resp = new JsonObject();
               resp.addProperty("check", true);
               resp.addProperty("number", random);
               response.getWriter().append(resp.toString());

            }else {
               JsonObject resp = new JsonObject();
               resp.addProperty("check", false);
               response.getWriter().append(resp.toString());
            }
         }else if(cmd.contentEquals("/updateDate.mem")) {
            String id = request.getParameter("id");

            String start = request.getParameter("start");
            String end = request.getParameter("end");

            int result = mdao.updateDate(id, start, end);
            if(result > 0) {
               MyDateDto mddto = (MyDateDto) request.getSession().getAttribute("date");
               mddto.setStart_date(start);
               mddto.setEnd_date(end);
               request.getSession().setAttribute("date", mddto);
               JsonObject resp = new JsonObject();
               resp.addProperty("check", true);
               response.getWriter().append(resp.toString());
            }else {
               JsonObject resp = new JsonObject();
               resp.addProperty("check", false);
               response.getWriter().append(resp.toString());
            }
         }else if(cmd.contentEquals("/findid.mem")) {
            String findname = request.getParameter("findname");
            String findemail = request.getParameter("findemail");

            String id = mdao.findid(findname, findemail);

            JsonObject resp = new JsonObject();

            resp.addProperty("id",id);
            response.getWriter().append(resp.toString());


         }else if(cmd.contentEquals("/findpw.mem")) {
            request.setCharacterEncoding("utf8");
            String findpwid = request.getParameter("findpwid");
            String hintqnum = request.getParameter("hintqnum");
            String hintanswer = request.getParameter("hintanswer");


            boolean result = mdao.findpw(findpwid, hintqnum, hintanswer);
            if(result) {
               request.setAttribute("result", result);
               request.setAttribute("id", findpwid);
               RequestDispatcher rd = request.getRequestDispatcher("/main/changepw.jsp");

               rd.forward(request, response);
            }else {
               response.sendRedirect("/main/findidpw.jsp");
            }

         }else if(cmd.contentEquals("/changepw.mem")) {
            request.setCharacterEncoding("utf8");

            String id = request.getParameter("id");
            String pw = request.getParameter("pw");
            String pw2 = MemberController.getSHA512(pw);   


            int result = mdao.changepw(id, pw2);
            MemberDto mdto = new MemberDto();

            mdto.setPw(pw2);

            request.getSession().setAttribute("mem", mdto);
            request.getSession().invalidate();
            response.sendRedirect("../main/changepwalert.jsp");



         }else if(cmd.contentEquals("/changeinfo.mem")) {
            request.setCharacterEncoding("utf8");
            MemberDto mem = (MemberDto) request.getSession().getAttribute("mem");
            String id = mem.getId();
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String zipcode = request.getParameter("zipcode");
            String address1 = request.getParameter("address1");
            String address2 = request.getParameter("address2");
            String birthday = request.getParameter("birthday");
            String career = request.getParameter("career");
            String agreement = request.getParameter("agreement");
            if(agreement == null) {
               agreement = "false";
            }

            String hintQnum = request.getParameter("hintQnum");
            String hintA = request.getParameter("hintA");
            String[] userlanguage = request.getParameterValues("language");

            mdao.deletelanguage(id);

            for(int i = 0 ;i<userlanguage.length;i++) {
               mdao.addLanguage(id, userlanguage[i]);
            }
            request.getSession().setAttribute("language", userlanguage);


            int result = mdao.changeinfo(id, name,hintQnum, hintA, zipcode, address1, address2, phone, birthday,career,agreement);
            if(result > 0) {
               response.sendRedirect("/logout.mem");
            }
         }else if(cmd.contentEquals("/mydelete.mem")) {
            String[] str = null;
            if(request.getParameter("category") == null) {
            String[] arr = request.getParameterValues("rowcheck");
            
            str = arr[0].split(",");

               for(int i=0;i<str.length;i += 2) {
                  if(str[i].contentEquals("질문")) {
                     int seq = Integer.parseInt(str[i+1]);
                     mdao.deleteQna(seq);
                  }else if(str[i].contentEquals("공고")) {
                     int seq = Integer.parseInt(str[i+1]);
                     mdao.deleteNotice(seq);
                  }
               }
            }else {
               String category = request.getParameter("category");
               int seq = Integer.parseInt(request.getParameter("seq"));
               if(category.contentEquals("질문")) {
               mdao.deleteQna(seq);
               }else {
               mdao.deleteNotice(seq);
               }
            }
            response.sendRedirect("mywrite.mem");
         }else if(cmd.contentEquals("/indexboard.mem")) {
            List<MyBoardDto> board = mdao.newboard();
            request.getSession().setAttribute("newboard", board);
            response.sendRedirect("/index.jsp");
//            request.setAttribute("newboard", board);
//            RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
//            rd.forward(request, response);
            
         }else if(cmd.contentEquals("/popboard.mem")) {
            List<MyBoardDto> popboard = mdao.popularboard();
            request.getSession().setAttribute("popboard", popboard);
            response.sendRedirect("/index.jsp");
//            request.setAttribute("popboard", popboard);
//            System.out.println(popboard.size());
//            RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
//            rd.forward(request, response);
         }else if(cmd.contentEquals("/newmember.mem")) {
            List<MemberDto> members = mdao.selectNewMember();
            request.getSession().setAttribute("members", members);
            response.sendRedirect("index.jsp");
         }else if(cmd.contentEquals("/newcontest.mem")) {
            List<NewContestDto> newcontest = mdao.selectcontest();
            request.getSession().setAttribute("newcontest", newcontest);
            response.sendRedirect("index.jsp");
         }
      }catch(Exception e) {
         e.printStackTrace();
         response.sendRedirect("/loginerror/error.jsp");
      }
   }

   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      doGet(request, response);
   }

}