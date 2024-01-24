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

			if ("write".equals(actionName)) {
				UserVo authUser = getAuthUser(request);

				int userNo = authUser.getNo();
				String title = multi.getParameter("title");
				String content = multi.getParameter("content");

				BoardVo vo = new BoardVo(title, content, userNo);
				BoardDao dao = new BoardDaoImpl();

				/* 다중 파일첨부 시작 경성 */
				Enumeration files = multi.getFileNames();
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

					// 아래 내용 set으로 값 넣기
					if (cnt == 1) {
						vo.setFileName1(filename);
						vo.setFileSize1(filesize);
					} else {
						vo.setFileName2(filename);
						vo.setFileSize2(filesize);
					}
					System.out.println(file);
					System.out.println("filename: " + filename);
					System.out.println("filesize: " + filesize);
				}

				System.out.println(vo.toString());

				dao.insert(vo);
				/* 다중 파일첨부 끝 경성 */
				WebUtil.redirect(request, response, "/mysite/board?a=list");

			} else if ("modify".equals(actionName)) {
				// 게시물 가져오기
				String title = multi.getParameter("title");
				String content = multi.getParameter("content");
				System.out.println(multi.getParameter("no"));
				int no = Integer.parseInt(multi.getParameter("no"));

				BoardVo vo = new BoardVo(no, title, content);
				BoardDao dao = new BoardDaoImpl();

				/* 파일첨부 시작 경성 */

				/* 파일첨부 끝 경성 */
				/* 다중 파일첨부 시작 경성 */
				Enumeration files = multi.getFileNames();
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

					// 아래 내용 set으로 값 넣기
					if (cnt == 1) {
						vo.setFileName1(filename);
						vo.setFileSize1(filesize);
					} else {
						vo.setFileName2(filename);
						vo.setFileSize2(filesize);
					}
					System.out.println(file);
					System.out.println("filename: " + filename);
					System.out.println("filesize: " + filesize);
				}

				System.out.println(vo.toString());

				dao.update(vo);
				/* 다중 파일첨부 끝 경성 */
				WebUtil.redirect(request, response, "/mysite/board?a=list");
			}
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
