package com.javaex.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.javaex.dao.BoardDao;
import com.javaex.dao.BoardDaoImpl;
import com.javaex.util.WebUtil;
import com.javaex.vo.BoardVo;
import com.javaex.vo.UserVo;

@WebServlet("/board")
public class BoardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
//20240122 10:08
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String actionName = request.getParameter("a");
		System.out.println("board:" + actionName);

		if ("list".equals(actionName)) {
			// 리스트 가져오기
			int pageNo=1; //기본 페이지 번호
			int pageSize=10; //페이지크기
			
			
			//페이지 번호, 페이지 크기 파라미터 값 설정
			String pageNoStr=request.getParameter("pageNo");
			String pageSizeStr=request.getParameter("pageSize");
			
			if(pageNoStr != null && !pageNoStr.equals("")) {
				pageNo=Integer.parseInt(pageNoStr);
				
			}
			
			if(pageSizeStr != null && !pageSizeStr.equals("")) {
				pageSize = Integer.parseInt(pageSizeStr);
			}
			
			BoardDao dao = new BoardDaoImpl();
			
			 // 전체 페이지 수(totalPages)를 계산
		    int totalPosts = dao.getTotalPosts(); 
		    int totalPages = (int) Math.ceil((double) totalPosts / pageSize);
		    
		    
			List<BoardVo> list = dao.getPageList(pageNo, pageSize);

		//	System.out.println(list.toString());

			// 리스트 화면에 보내기
			request.setAttribute("list", list);
		    request.setAttribute("pageNo", pageNo);
		    request.setAttribute("totalPages", totalPages);
			WebUtil.forward(request, response, "/WEB-INF/views/board/list.jsp");
			
			//RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/board/list.jsp");
	  //  rd.forward(request, response);
		} 
		
		else if ("read".equals(actionName)) {
			// 게시물 가져오기
			int no = Integer.parseInt(request.getParameter("no"));
			BoardDao dao = new BoardDaoImpl();
			BoardVo boardVo = dao.getBoard(no);

			System.out.println(boardVo.toString());

			// 게시물 화면에 보내기
			request.setAttribute("boardVo", boardVo);
			WebUtil.forward(request, response, "/WEB-INF/views/board/read.jsp");
		} 
		
		else if ("modifyform".equals(actionName)) {
			// 게시물 가져오기
			int no = Integer.parseInt(request.getParameter("no"));
			BoardDao dao = new BoardDaoImpl();
			BoardVo boardVo = dao.getBoard(no);

			// 게시물 화면에 보내기
			request.setAttribute("boardVo", boardVo);
			WebUtil.forward(request, response, "/WEB-INF/views/board/modifyform.jsp");
		} 
		
		else if ("modify".equals(actionName)) {
			// 게시물 가져오기
			String title = request.getParameter("title");
			String content = request.getParameter("content");
			int no = Integer.parseInt(request.getParameter("no"));
			
			BoardVo vo = new BoardVo(no, title, content);
			BoardDao dao = new BoardDaoImpl();
			
			dao.update(vo);
			
			WebUtil.redirect(request, response, "/mysite/board?a=list");
		} 
		
		else if ("writeform".equals(actionName)) {
			// 로그인 여부체크
			UserVo authUser = getAuthUser(request);
			if (authUser != null) { // 로그인했으면 작성페이지로
				WebUtil.forward(request, response, "/WEB-INF/views/board/writeform.jsp");
			} else { // 로그인 안했으면 리스트로
				WebUtil.redirect(request, response, "/mysite/board?a=list");
			}

		} 
		
		else if ("write".equals(actionName)) {
			UserVo authUser = getAuthUser(request);

			String title = request.getParameter("title");
			String content = request.getParameter("content");
			
			int userNo = authUser.getNo();
			System.out.println("userNo : ["+userNo+"]");
			System.out.println("title : ["+title+"]");
			System.out.println("content : ["+content+"]");

			BoardVo vo = new BoardVo(title, content, userNo);
			BoardDao dao = new BoardDaoImpl();
			dao.insert(vo);

			WebUtil.redirect(request, response, "/mysite/board?a=list");

		} 
		
		else if ("delete".equals(actionName)) {
			int no = Integer.parseInt(request.getParameter("no"));

			BoardDao dao = new BoardDaoImpl();
			dao.delete(no);

			WebUtil.redirect(request, response, "/mysite/board?a=list");

		} 
		
		else {
			WebUtil.redirect(request, response, "/mysite/board?a=list");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	// 로그인 되어 있는 정보를 가져옴
	protected UserVo getAuthUser(HttpServletRequest request) {
		HttpSession session = request.getSession();
		UserVo authUser = (UserVo) session.getAttribute("authUser");

		return authUser;
	}

	
	
}
