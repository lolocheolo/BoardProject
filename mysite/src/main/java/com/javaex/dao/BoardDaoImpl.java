package com.javaex.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.javaex.vo.BoardVo;

public class BoardDaoImpl implements BoardDao {
  private Connection getConnection() throws SQLException {
    Connection conn = null;
    try {
      Class.forName("oracle.jdbc.driver.OracleDriver");
      String dburl = "jdbc:oracle:thin:@localhost:1521:xe";
      conn = DriverManager.getConnection(dburl, "webdb", "1234");
    } catch (ClassNotFoundException e) {
      System.err.println("JDBC 드라이버 로드 실패!");
    }
    return conn;
  }
  
  public List<BoardVo> getPageList(int pageNo, int pageSize) {
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    List<BoardVo> pagelist = new ArrayList<>();

	    try {
	        conn = getConnection();

	        String query = 
	        					"SELECT * " +
	        		            "FROM (SELECT b.no, b.title, b.content, b.hit, b.reg_date, b.user_no, u.name, " +
	        		            "             ROW_NUMBER() OVER (ORDER BY b.no DESC) AS rnum " +
	        		            "      FROM board b, users u " +
	        		            "      WHERE b.user_no = u.no) " +
	        		            "WHERE rnum BETWEEN ? AND ?";

	        int startRow = (pageNo - 1) * pageSize + 1;
	        int endRow = pageNo * pageSize;

	        pstmt = conn.prepareStatement(query);
	        pstmt.setInt(1, startRow);
	        pstmt.setInt(2, endRow);

	        rs = pstmt.executeQuery();

	        while (rs.next()) {
	        	int no = rs.getInt("no");
	            String title = rs.getString("title");
	            String content = rs.getString("content");
	            int hit = rs.getInt("hit");
	            String regDate = rs.getString("reg_date");
	            int userNo = rs.getInt("user_no");
	            String userName = rs.getString("name");
	            BoardVo vo = new BoardVo(no, title, content, hit, regDate, userNo, userName);
	            pagelist.add(vo);
	        }

	    } catch (SQLException e) {
	        System.out.println("error:" + e);
	    } finally {
	        try {
	            if (rs != null) {
	                rs.close();
	            }
	            if (pstmt != null) {
	                pstmt.close();
	            }
	            if (conn != null) {
	                conn.close();
	            }
	        } catch (SQLException e) {
	            System.out.println("error:" + e);
	        }
	    }

	    return pagelist;
	}

  // 게시물 검색 기능구현 : 작성자, 게시물 작성일시, 제목, 내용 으로 검색가능해야 함
  public List<BoardVo> getSearchList(String word, int pageNo, int pageSize) {
		// 0. import java.sql.*;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BoardVo> list = new ArrayList<BoardVo>();
		
		try {
			conn = getConnection();
			
			// 3. SQL문 준비 / 바인딩 / 실행    
			String query = "SELECT * FROM ("
		             + "    SELECT "
		             + "        b.no, b.title, b.hit, b.reg_date, b.user_no, u.name, "
		             + "        ROW_NUMBER() OVER (ORDER BY b.no DESC) AS rnum "
		             + "    FROM "
		             + "        board b "
		             + "        JOIN users u ON b.user_no = u.no "
		             + "    WHERE "
		             + "        LOWER(u.name) LIKE LOWER(?) "
		             + "        OR TO_CHAR(b.reg_date, 'YYYY-MM-DD') LIKE ? "
		             + "        OR LOWER(b.title) LIKE LOWER(?) "
		             + "        OR LOWER(b.content) LIKE LOWER(?) "
		             + "    ORDER BY no DESC"
		             + ") WHERE rnum BETWEEN ? AND ?";

			
			int startRow = (pageNo - 1) * pageSize + 1;;
	        int endRow = pageNo * pageSize;

			
			pstmt = conn.prepareStatement(query);
			
			for (int i = 1; i <= 4; i++) {
	            pstmt.setString(i, "%" + word + "%".toLowerCase());
	        }
			
			pstmt.setInt(5, startRow);
	        pstmt.setInt(6, endRow);
			
			rs = pstmt.executeQuery();
			
			// 4.결과처리
			while (rs.next()) {
				int no = rs.getInt("no");
				String title = rs.getString("title");
				int hit = rs.getInt("hit");
				String regDate = rs.getString("reg_date");
				int userNo = rs.getInt("user_no");
				String userName = rs.getString("name");
				
				BoardVo vo = new BoardVo(no, title, hit, regDate, userNo, userName);
				list.add(vo);
			}
		} catch (SQLException e) {
			System.out.println("error:" + e);
		} finally {
			// 5. 자원정리
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				System.out.println("error:" + e);
			}

		}
		
		return list;

	}
	
	
	
	public int getTotalPosts() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int totalPosts = 0;

        try {
            conn = getConnection();

            String query = "SELECT COUNT(*) FROM board";

            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                totalPosts = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("error:" + e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("error:" + e);
            }
        }
      
        return totalPosts;
        
    }
	
	public int getTotalSearchedPosts(String word) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int totalSearchedPosts = 0;

        try {
            conn = getConnection();
            String query = "SELECT COUNT(*)"
                    + " FROM board b"
                    + " JOIN users u ON b.user_no = u.no"
                    + " WHERE LOWER(u.name) LIKE LOWER(?)"
                    + "    OR TO_CHAR(b.reg_date, 'YYYY-MM-DD') LIKE ?"
                    + "    OR LOWER(b.title) LIKE LOWER(?)"
                    + "    OR LOWER(b.content) LIKE LOWER(?)";
  
            pstmt = conn.prepareStatement(query);

            for (int i = 1; i <= 4; i++) {
	            pstmt.setString(i, "%" + word + "%".toLowerCase());
	        }
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
            	totalSearchedPosts = rs.getInt(1);
            }
		
        } catch (SQLException e) {
            System.out.println("error:" + e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("error:" + e);
            }
        }
        return totalSearchedPosts;
        
    }
	
	
	public BoardVo getBoard(int no) {

		// 0. import java.sql.*;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BoardVo boardVo = null;
		
		try {
		  conn = getConnection();

			// 3. SQL문 준비 / 바인딩 / 실행
			String query = "select b.no, b.title, b.content, b.hit, b.reg_date, b.user_no, u.name "
					     + "from board b, users u "
					     + "where b.user_no = u.no "
					     + "and b.no = ?";
			
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, no);
			
			rs = pstmt.executeQuery();
			// 4.결과처리
			while (rs.next()) {
				String title = rs.getString("title");
				String content = rs.getString("content");
				int hit = rs.getInt("hit");
				String regDate = rs.getString("reg_date");
				int userNo = rs.getInt("user_no");
				String userName = rs.getString("name");
				
				
				// 조회수 증가 코드 추가
		        hit++; // 현재 조회수를 1 증가시킴
		        updateHit(no, hit); // 조회수를 업데이트하는 메서드 호출
		        
		        
				boardVo = new BoardVo(no, title, content, hit, regDate, userNo, userName);
			}
			
		} catch (SQLException e) {
			System.out.println("error:" + e);
		} finally {
			// 5. 자원정리
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				System.out.println("error:" + e);
			}

		}
		System.out.println(boardVo);
		return boardVo;

	}
	
	
	
	// 조회수 업데이트 메서드 추가
	private void updateHit(int no, int hit) {
	    Connection conn = null;
	    PreparedStatement pstmt = null;

	    try {
	        conn = getConnection();

	        // 조회수를 업데이트하는 SQL문 작성
	        String query = "UPDATE board SET hit = ? WHERE no = ?";
	        pstmt = conn.prepareStatement(query);
	        pstmt.setInt(1, hit);
	        pstmt.setInt(2, no);

	        // SQL문 실행
	        pstmt.executeUpdate();

	        // 트랜잭션 커밋
	        conn.commit();
	    } catch (SQLException e) {
	        // 오류 처리
	        try {
	            if (conn != null) {
	                conn.rollback(); // 오류 발생 시 롤백
	            }
	        } catch (SQLException rollbackEx) {
	            rollbackEx.printStackTrace();
	        }
	        e.printStackTrace();
	    } finally {
	        // 자원 정리
	        try {
	            if (pstmt != null) {
	                pstmt.close();
	            }
	            if (conn != null) {
	                conn.close();
	            }
	        } catch (SQLException closeEx) {
	            closeEx.printStackTrace();
	        }
	    }
	}
	
	
	
	
	public int insert(BoardVo vo) {
		// 0. import java.sql.*;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int count = 0;

		try {
		  conn = getConnection();
		  
		  System.out.println("vo.userNo : ["+vo.getUserNo()+"]");
	      System.out.println("vo.title : ["+vo.getTitle()+"]");
	      System.out.println("vo.content : ["+vo.getContent()+"]");
	      
				// 3. SQL문 준비 / 바인딩 / 실행
	      String query = "insert into board values (seq_board_no.nextval, ?, ?, 0, sysdate, ?, 0, 0, 0)";

			pstmt = conn.prepareStatement(query);

			pstmt.setString(1, vo.getTitle());
			pstmt.setString(2, vo.getContent());
			pstmt.setInt(3, vo.getUserNo());
			
			
      
			count = pstmt.executeUpdate();

			// 4.결과처리
			System.out.println(count + "건 등록");

		} catch (SQLException e) {
			System.out.println("error:" + e);
		} finally {
			// 5. 자원정리
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				System.out.println("error:" + e);
			}

		}

		return count;
	}
	
	
	public int delete(int no) {
		// 0. import java.sql.*;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int count = 0;

		try {
		  conn = getConnection();

			// 3. SQL문 준비 / 바인딩 / 실행
			String query = "delete from board where no = ?";
			pstmt = conn.prepareStatement(query);

			pstmt.setInt(1, no);

			count = pstmt.executeUpdate();

			// 4.결과처리
			System.out.println(count + "건 삭제");

		} catch (SQLException e) {
			System.out.println("error:" + e);
		} finally {
			// 5. 자원정리
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				System.out.println("error:" + e);
			}

		}

		return count;
	}
	
	
	public int update(BoardVo vo) {
		// 0. import java.sql.*;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int count = 0;

		try {
		  conn = getConnection();

			// 3. SQL문 준비 / 바인딩 / 실행
			String query = "update board set title = ?, content = ? where no = ? ";
			pstmt = conn.prepareStatement(query);

			pstmt.setString(1, vo.getTitle());
			pstmt.setString(2, vo.getContent());
			pstmt.setInt(3, vo.getNo());

			count = pstmt.executeUpdate();

			// 4.결과처리
			System.out.println(count + "건 수정");

		} catch (SQLException e) {
			System.out.println("error:" + e);
		} finally {
			// 5. 자원정리
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				System.out.println("error:" + e);
			}

		}

		return count;
	}
	
}
