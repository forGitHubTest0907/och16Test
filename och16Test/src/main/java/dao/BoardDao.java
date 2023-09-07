package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;



// DBCP + Singleton
public class BoardDao {
	private static BoardDao instance;
		private BoardDao() {}
		public static BoardDao getInstance() {
			if (instance == null) {
				instance = new BoardDao();
			}
			return instance;
		}
		
		private Connection getConnection() {
			Connection conn = null;
			try {
				Context ctx = new InitialContext();
				DataSource ds = (DataSource)
						ctx.lookup("java:comp/env/jdbc/OracleDB");
				conn = ds.getConnection();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			return conn;
					
		}
		
		public int getTotalCnt() throws SQLException {
			Connection conn = null;
			Statement stmt = null;
			ResultSet rs =	null;
			int tot = 0;
			String sql = "Select count(*) from Board";
			
			try {
				conn = getConnection();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				if (rs.next()) tot = rs.getInt(1);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				if(conn != null) conn.close();
				if(stmt != null) stmt.close();
				if(rs != null) rs.close();
			}
			
			return tot;
		}
		
		public List<Board> boardList(int startRow, int endRow) throws SQLException {
			List<Board> list = new ArrayList<Board>();
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			// String sql = "select * from board order by num desc";
			// mysql select * from board order by num desc limit startPage-1,10;
			String sql = "SELECT * "
					+ "FROM (SELECT rownum rn, a.* "
					+ "    FROM       (select * from board  order by ref desc,re_step) a ) "
					+ "WHERE rn BETWEEN ? AND ? " ;
			
			try {
				conn = getConnection();
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, startRow);
				pstmt.setInt(2, endRow);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					Board board = new Board();
					board.setNum(rs.getInt("num"));
					board.setWriter(rs.getString("writer"));
					board.setSubject(rs.getString("subject"));
					board.setEmail(rs.getString("email"));
					board.setContent(rs.getString("content"));
					board.setReadcount(rs.getInt("readcount"));
					board.setIp(rs.getString("ip"));
					board.setRef(rs.getInt("ref"));
					board.setRe_level(rs.getInt("re_level"));
					board.setRe_step(rs.getInt("re_step"));
					board.setReg_date(rs.getDate("reg_date"));
					list.add(board);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				if(conn != null) conn.close();
				if(pstmt != null) pstmt.close();
				if(rs != null) rs.close();
			}
			return list;
		}
		
		public void readCount(int num) throws SQLException {
			Connection conn = null;
			PreparedStatement pstmt = null;
			String sql="update board set readcount=readcount+1 where num=?";
			try {
				conn = getConnection();
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, num);
				pstmt.executeUpdate();
			} catch (Exception e) { System.out.println(e.getMessage());
			} finally {
				if (pstmt != null) pstmt.close();
				if (conn != null) conn.close();
			}
			
		}
		
		public int insert(Board board) throws SQLException {
			// 1. board Setting 값들을 board TBL 안에 Insert
			//	  1-1 num -> MAX 로 Setting(PK)
			//	  1-2 PreparedStatement 적용
			//	  1-3 ref -> num Setting
			//	  1-4 return DML 결과값
			
			// 새로운 글을 추가할 때 필요한 정보를 가져옵니다.
			int num = board.getNum();// 글 번호
			Connection conn = null;// 데이터베이스 연결을 위한 객체
			PreparedStatement pstmt = null;// SQL 문장을 실행하기 위한 객체
			int result = 0;// 쿼리 실행 결과를 저장할 변수
			ResultSet rs = null;// SQL 쿼리 결과를 저장하는 객체
			String sql1 = "select nvl(max(num),0) from board";// 글 번호(num)의 최대값을 가져오는 SQL 쿼리
			String sql = "Insert INTO board VALUES(?,?,?,?,?,?,?,?,?,?,?,sysdate)";
			// 홍해의 기적 --> 나와 같은 댓글 Group & 내가 댓글 다는 항목보다 큰 re_step 을 + 1
			String sql2 = "update board set re_step = re_step+1 where ref=? and re_step > ?";
			
			try {
				conn = getConnection();// 데이터베이스 연결을 수행합니다.
				pstmt = conn.prepareStatement(sql1);// SQL1을 실행하기 위한 PreparedStatement 객체를 생성합니다.
				rs = pstmt.executeQuery(); // SQL1을 실행하고 결과를 rs에 저장합니다
				rs.next();// 결과 집합의 첫 번째 행으로 이동합니다.
				
				// key인 num 1씩 증가, mysql auto_increment 또는 oracle sequence
				// sequence를 사용: values(시퀀스명(board_seq).nextval,?,?...)
				int number = rs.getInt(1) + 1;
				rs.close();
				pstmt.close();
				
				// 댓글 --> sql2
				if (num != 0) {
					System.out.println("BoardDAO insert 댓글 sql2->"+sql2);
					System.out.println("BoardDAO insert 댓글 board.getRef()->"+board.getRef());
					System.out.println("BoardDAO insert 댓글 board.getRe_step()->"+board.getRe_step());
					pstmt = conn.prepareStatement(sql2);
					pstmt.setInt(1, board.getRef());
					pstmt.setInt(2, board.getRe_step());
					pstmt.executeUpdate();
					pstmt.close();
					
					// 댓글 관련 정보
					board.setRe_step(board.getRe_step()+1);
					board.setRe_level(board.getRe_level()+1);
				}
				if (num == 0) board.setRef(number);
				
				// 신규 글/댓글 공용
				// 만약 글 번호(num)가 0이면 (신규 글이라면) 참조 글 번호(ref)를 설정합니다.
				pstmt = conn.prepareStatement(sql);
				// SQL2에 데이터를 설정합니다.
				pstmt.setInt(1, number);
				pstmt.setString(2, board.getWriter());
				pstmt.setString(3, board.getSubject());
				pstmt.setString(4, board.getContent());
				pstmt.setString(5, board.getEmail());
				pstmt.setInt(6, board.getReadcount());
				pstmt.setString(7, board.getPasswd());
				pstmt.setInt(8, board.getRef());
				pstmt.setInt(9, board.getRe_step());
				pstmt.setInt(10, board.getRe_level());
				pstmt.setString(11, board.getIp());
				result = pstmt.executeUpdate();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				if(conn != null) conn.close();
				if(pstmt != null) pstmt.close();
				if(rs != null) rs.close();
			}
			
			return result;
		}
		
		public Board select(int num) throws SQLException  {
			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;
			
			String sql = "select * from board where num="+num;
			
			Board board = new Board();
			
			try {
				conn = getConnection();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				
				 // 결과가 있는 경우에만 데이터를 가져옵니다.
				if (rs.next()) {
					 // 8. ResultSet에서 각 열(컬럼)의 데이터를 읽어와서 Board 객체에 설정합니다
					// rs에 있는 걸 get해서 board에 set한다~
					// num 을 가져와서, rs로 sql 돌려서 board에 넣기 
					board.setNum(rs.getInt("num"));
					board.setWriter(rs.getString("writer"));
					board.setSubject(rs.getString("subject"));
					board.setContent(rs.getString("content"));
					board.setEmail(rs.getString("email"));
					board.setReadcount(rs.getInt("readcount"));
					board.setIp(rs.getString("ip"));
					board.setReg_date(rs.getDate("reg_date"));
					board.setRef(rs.getInt("ref"));
					board.setRe_level(rs.getInt("re_level"));
					board.setRe_step(rs.getInt("re_step"));
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			}
			// 11. 가져온 데이터가 설정된 Board 객체를 반환합니다.
			return board;
		}
		
		public int update(Board board) throws SQLException {
			Connection conn = null;
			PreparedStatement pstmt = null;
			int result = 0;
			String sql = "Update board "
					+ "set subject=?, writer=?, email=?, passwd=?,content=?, ip=? "
					+ "where num=?";
			
			try {
				conn = getConnection();
				// board 에 있는 걸 get해서 pstmt 에 set한다~
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, board.getSubject());
				pstmt.setString(2, board.getWriter());
				pstmt.setString(3, board.getEmail());
				pstmt.setString(4, board.getPasswd());
				pstmt.setString(5, board.getContent());
				pstmt.setString(6, board.getIp());
				pstmt.setInt(7, board.getNum());
				result = pstmt.executeUpdate();
				//board.getNum(Integer.parseInt)); 
				//어떨 때 board에 쓰고 pstmt에 쓰나 -> board 의 데이터를 받아와서 pstmt에 넣고 spl돌리기
				
				
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				if(conn != null) conn.close();
				if(pstmt != null) pstmt.close();
			}
			return result;
			
		}
		
		public int delete(int num, String passwd) throws SQLException {
			Connection conn = null;
			PreparedStatement pstmt = null;
			String sql = "Delete from board where num=?";
			
			pstmt = conn.prepareStatement(sql);
			
			
			pstmt.executeUpdate(sql);	//어떨 때 update고 어떨 때 아니더라???
			// executeUpdate 메소드는 데이터베이스에 변경(추가, 수정, 삭제)을 가하는 SQL 문장을 실행할 때 
			
			return 0;
		}
		
		
	
}
