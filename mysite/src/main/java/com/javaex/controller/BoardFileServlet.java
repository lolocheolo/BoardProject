package com.javaex.controller;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

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
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

/**
 * 6조 게시판 역할 및 기능구현
 * 박철호(Team Leader) 	   : Board Paging
 * 양민주(Trouble Shooter)  : Board Search
 * 백경성(Developer)		   : Board File Upload 
 * 박경진(Project Manager)  : Board File Download
 */

@WebServlet("/boardwirteupdate")
public class BoardFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// 파일저장소 경로
	private static final String SAVEFOLDER = "/Users/User/git/BoardProject/mysite/src/main/webapp/WEB-INF/uploadfile";
	private static final String ENCTYPE = "UTF-8";
	private static int MAXSIZE = 5 * 1024 * 1024;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String filename = null;
		int filesize = 0;
		MultipartRequest multi = null;
		File fileCreate = null;
		
		try {
			multi = new MultipartRequest(request, SAVEFOLDER, MAXSIZE, ENCTYPE, new DefaultFileRenamePolicy());
			String actionName = multi.getParameter("a");
			System.out.println("boardfile:" + actionName);
			
			/* Board File Upload Start By 박경진, 백경성 */
			if ("write".equals(actionName)) {
				// View로 부터 로그인 정보 받아 오기
				UserVo authUser = getAuthUser(request);
				
				// 현재 사용자 정보로 게시판 등록
				int userNo = authUser.getNo();				
				String title = multi.getParameter("title");
				String content = multi.getParameter("content");

				// VO, DAO 객체 생성
				BoardVo vo = new BoardVo(title, content, userNo);
				BoardDao dao = new BoardDaoImpl();

				Enumeration files = multi.getFileNames();
				
				// 게시판에 저장될 제목, 내용, 첨부파일을 등록
				// 	이때, 첨부파일의 경우 최대 2개 까지 등록 가능하다.
				// 	VO 및 DB Table에는 파일1, 파일2로 존재하므로 cnt변수로 들어갈 위치를 정해준다. 
				int cnt = 0;
				while (files.hasMoreElements()) {
					String file = (String) files.nextElement();

					fileCreate = new File(SAVEFOLDER);

					if (!fileCreate.exists())
						fileCreate.mkdirs();

					if (multi.getFilesystemName(file) != null) {
						filename = multi.getFilesystemName(file);
						filesize = (int) multi.getFile(file).length();
						cnt++;
					}

					// VO 및 DB Table에 알맞게 SET하고 DAO에서 DB COLUMN과 맵핑시킴
					if (cnt == 1) {
						vo.setFileName1(filename);
						vo.setFileSize1(filesize);
					} else {
						vo.setFileName2(filename);
						vo.setFileSize2(filesize);
					}
				}

				dao.insert(vo);
				WebUtil.redirect(request, response, "/mysite/board?a=list");

			} else if ("modify".equals(actionName)) {
				// View로 부터 수정할 게시물 정보 받아오기
				String title = multi.getParameter("title");
				String content = multi.getParameter("content");
				int no = Integer.parseInt(multi.getParameter("no"));

				// VO, DAO 객체 생성
				BoardVo vo = new BoardVo(no, title, content);
				BoardDao dao = new BoardDaoImpl();

				Enumeration files = multi.getFileNames();
				
				// 게시판에 수정될 제목, 내용, 첨부파일을 수정
				// 	VO 및 DB Table에는 파일1, 파일2로 존재하므로 cnt변수로 들어갈 위치를 정해준다. 
				int cnt = 0;
				while (files.hasMoreElements()) {
					String file = (String) files.nextElement();

					fileCreate = new File(SAVEFOLDER);

					if (!fileCreate.exists())
						fileCreate.mkdirs();

					if (multi.getFilesystemName(file) != null) {
						filename = multi.getFilesystemName(file);
						filesize = (int) multi.getFile(file).length();
						cnt++;
					}

					if (cnt == 1) {
						vo.setFileName1(filename);
						vo.setFileSize1(filesize);
					} else {
						vo.setFileName2(filename);
						vo.setFileSize2(filesize);
					}
				}

				dao.update(vo);
				WebUtil.redirect(request, response, "/mysite/board?a=list");
			}
			/* Board File Upload End By 박경진, 백경성 */
		} catch (NullPointerException e) {
			System.out.println(e.toString());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	// 로그인 되어 있는 정보를 가져온다.
	protected UserVo getAuthUser(HttpServletRequest request) {
		HttpSession session = request.getSession();
		UserVo authUser = (UserVo) session.getAttribute("authUser");

		return authUser;
	}
}
