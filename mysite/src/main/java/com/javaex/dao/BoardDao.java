package com.javaex.dao;

import java.util.List;
import com.javaex.vo.BoardVo;

public interface BoardDao {
	public List<BoardVo> getSearchList(String word, int pageNo, int pageSize); // 게시물 검색 조회
	public List<BoardVo> getPageList(int pageNo, int pageSize); //게시물10개씩 목록조회
	public int getTotalPosts(); // 전체 게시물 수 조회
	public int getTotalSearchedPosts(String word);
	public BoardVo getBoard(int no); // 게시물 상세 조회
	public int insert(BoardVo vo);   // 게시물 등록
	public int delete(int no);       // 게시물 삭제
	public int update(BoardVo vo);   // 게시물 수정
}

