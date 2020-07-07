package commit.backend.controller;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import commit.backend.dao.VisitCountDao;

@WebListener
public class SessionListener implements HttpSessionListener {



    public void sessionCreated(HttpSessionEvent arg0)   { 
    	  // DAO 객체 생성
        VisitCountDao dao = new VisitCountDao();
         
        try {
        // 전체 방문자 수 +1
        dao.setVisitTotalCount();
         
        // 오늘 방문자 수
        int todayCount = dao.getVisitTodayCount();
         
        // 전체 방문자 수
        int totalCount = dao.getVisitTotalCount();
        
        
//        HttpSession session = arg0.getSession();
//         
//        // 세션 속성에 담아준다.
//        session.setAttribute("totalCount", totalCount); // 전체 방문자 수
//        session.setAttribute("todayCount", todayCount); // 오늘 방문자 수
        
        }catch(Exception e) {
        	
        }
    	
    	
    }


    public void sessionDestroyed(HttpSessionEvent arg0)  { 
        
    }
	
}
