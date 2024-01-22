<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.javaex.dao.BoardDao"%>
<%@ page import="com.javaex.dao.BoardDaoImpl"%>
<%
  // BoardDao 객체 생성
  BoardDao boardDao = new BoardDaoImpl();
  // DAO를 통해 전체 게시물 수 가져오기
  int totalPosts = boardDao.getTotalPosts();
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<link href="/mysite/assets/css/board.css" rel="stylesheet" type="text/css">
<title>Mysite</title>
</head>
<body>
	<div id="container">
		
		<c:import url="/WEB-INF/views/includes/header.jsp"></c:import>
		<c:import url="/WEB-INF/views/includes/navigation.jsp"></c:import>
		
		<!-- 전체 게시물 수 표시 -->
        <p style="float: left; margin-top: 40px;">전체 게시물 수:  <%= totalPosts %>  </p>
		<div id="content">
			<div id="board">       
                 <!-- 검색 폼 -->
				    <form id="search_form" action="" method="post">
					<input type="text" id="kwd" name="kwd" value="${sessionScope.searchKeyword}">
					<input type="submit" value="찾기">
				</form>
				<table class="tbl-ex">
					<tr>
						<th>번호</th>
						<th>제목</th>
						<th>글쓴이</th>
						<th>조회수</th>
						<th>작성일</th>
						<th>&nbsp;</th>
					</tr>	
					
								
					<c:forEach items="${list }" var="vo">
						
						<tr>
							<td>${vo.no }</td>
							<td><a href="/mysite/board?a=read&no=${vo.no }"> ${vo.title } </a></td>
							<td>${vo.userName }</td>
							<td>${vo.hit }</td>
							<td>${vo.regDate }</td>
							<td>
								<c:if test="${authUser.no == vo.userNo }">
									<a href="/mysite/board?a=delete&no=${vo.no }" class="del">삭제</a>
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</table>
				
				
				
				
<div class="pager">
    <ul>
        <c:if test="${pageNo > 1}">
            <li><a href="/mysite/board?a=list&pageNo=${pageNo - 1}">◀</a></li>
        </c:if>

        <c:forEach begin="1" end="${totalPages}" var="i">
            <c:choose>
                <c:when test="${pageNo == i}">
                    <li class="selected">${i}</li>
                </c:when>
                <c:otherwise>
                    <li><a href="/mysite/board?a=list&pageNo=${i}">${i}</a></li>
                </c:otherwise>
            </c:choose>
        </c:forEach>

        <c:if test="${pageNo < totalPages}">
            <li><a href="/mysite/board?a=list&pageNo=${pageNo + 1}">▶</a></li>
        </c:if>
    </ul>
</div>
				
				<c:if test="${authUser != null }">
					<div class="bottom">
						<a href="/mysite/board?a=writeform" id="new-book">글쓰기</a>
					</div>
				</c:if>				
			</div>
		</div>
		
		<c:import url="/WEB-INF/views/includes/footer.jsp"></c:import>
		
	</div><!-- /container -->
</body>
</html>		
		