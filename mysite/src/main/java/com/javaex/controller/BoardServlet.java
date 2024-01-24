package com.javaex.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

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

	/* 파일 경로 업로드 및 다운로드 */
	private static final String SAVEFOLDER = "/Users/User/git/BoardProject/mysite/src/main/webapp/WEB-INF/uploadfile";
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String actionName = request.getParameter("a");
		HttpSession session = request.getSession();
		
		// 게시판 클릭시 searchKeyword 세션에 저장된 내용 삭제
		if (actionName == null ) session.removeAttribute("searchKeyword");

		if ("list".equals(actionName)) {
			
			
			int pageNo = 1; //기본 페이지 번호
			int pageSize = 10; //페이지크기
			
			
			//페이지 번호, 페이지 크기 파라미터 값 설정
			String pageNoStr = request.getParameter("pageNo");
			String pageSizeStr = request.getParameter("pageSize");
			
			if(pageNoStr != null && !pageNoStr.equals("")) pageNo = Integer.parseInt(pageNoStr);
			if(pageSizeStr != null && !pageSizeStr.equals("")) pageSize = Integer.parseInt(pageSizeStr);
			
			BoardDao dao = new BoardDaoImpl();
			
			// 전체 페이지 수(totalPages)를 계산
		    int totalPosts = dao.getTotalPosts(); 
		    int totalPages = (int) Math.ceil((double) totalPosts / pageSize);
		    
		    // NULL이 아닐때 한번만 실행됨 (검색어를 세션에 넣어주기)
		    String word = "";
		    if (request.getParameter("kwd") != null ) {
		    	word = request.getParameter("kwd");
		    	session.setAttribute("searchKeyword", word);
		    }
		
		    // NULL -> 검색어 입력하지 않았을 때
		    System.out.println("세션에 들어있는 정보: "+ session.getAttribute("searchKeyword"));
		    if (session.getAttribute("searchKeyword") == null) {	
		    	List<BoardVo> list = dao.getPageList(pageNo, pageSize);
		    	
		    	request.setAttribute("list", list);
			    request.setAttribute("pageNo", pageNo);
			    request.setAttribute("totalPages", totalPages);
		    } else {
				// 검색어를 JSP 페이지로 전송 (예시)
				word = (String) session.getAttribute("searchKeyword");
				List<BoardVo> searchedList = dao.getSearchList(word, pageNo, pageSize);
				totalPosts = dao.getTotalSearchedPosts(word);
				totalPages = (int) Math.ceil((double) totalPosts / pageSize);
				
				request.setAttribute("list", searchedList);
			    request.setAttribute("pageNo", pageNo);
			    request.setAttribute("totalPages", totalPages);
		    }
			WebUtil.forward(request, response, "/WEB-INF/views/board/list.jsp");
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
		
		else if ("writeform".equals(actionName)) {
			// 로그인 여부체크
			UserVo authUser = getAuthUser(request);
			if (authUser != null) { // 로그인했으면 작성페이지로
				WebUtil.forward(request, response, "/WEB-INF/views/board/writeform.jsp");
			} else { // 로그인 안했으면 리스트로
				WebUtil.redirect(request, response, "/mysite/board?a=list");
			}

		} 

		else if ("delete".equals(actionName)) {
			int no = Integer.parseInt(request.getParameter("no"));

			BoardDao dao = new BoardDaoImpl();
			dao.delete(no);

			WebUtil.redirect(request, response, "/mysite/board?a=list");

		} 
		else if ("download".equals(actionName)) {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html; charset=utf-8");
			String fileName = request.getParameter("fileName");
			String path = SAVEFOLDER + "/" + fileName;
			System.out.println(fileName);

			OutputStream out = response.getOutputStream();
			File file = new File(path);
			
			response.setHeader("Cache-Control", "no-cache");
			response.addHeader("Content-disposition", "attachment; fileName=" + fileName);
			
			FileInputStream in = new FileInputStream(file); 
			
			byte[] buffer = new byte[1024 * 8];
			
			while (true) {
				int count = in.read(buffer);
				if (count == -1) {
					break;
				}
				out.write(buffer, 0, count);
			}
			in.close();
			out.close();
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