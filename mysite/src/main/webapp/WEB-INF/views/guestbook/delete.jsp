<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ page import="com.javaex.dao.GuestbookDao"%>
<%@ page import="com.javaex.dao.GuestbookDaoImpl"%>
<%@ page import="com.javaex.vo.GuestbookVo"%>
<%@ page import="java.util.*"%>

<% 
	request.setCharacterEncoding("UTF-8");
	
	int no = Integer.parseInt( request.getParameter("no") ) ;
	String password = request.getParameter("password");
	
	GuestbookVo vo = new GuestbookVo(no, password);
	
	GuestbookDao dao = new GuestbookDaoImpl();
	dao.delete(vo);
	
	response.sendRedirect("list.jsp");
%>
