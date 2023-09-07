package service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.Board;
import dao.BoardDao;

public class ContentAction implements CommandProcess {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("ContentAction Service start...");
		// 1. 글 번호(num)와 페이지 번호(pageNum)를 가져옴
		int num = Integer.parseInt(request.getParameter("num")); 
		String pageNum = request.getParameter("pageNum");
		
		try {
			// 2. BoardDao bd Instance (BoardDao 객체를 생성하고 가져옴)
			BoardDao bd = BoardDao.getInstance();

			// 3. 글의 조회수를 증가시킴: num의 readCount 증가 -->void bd.readCount(num); 
			bd.readCount(num);
			
			// 4. 글 번호(num)에 해당하는 게시글을 가져옴: Board board = bd.select(num);
			Board board = bd.select(num); 
			
			// 5. request 객체에 num , pageNum , board 데이터를 저장하여 JSP에서 사용할 수 있도록 함
			request.setAttribute("num", num);
			request.setAttribute("pageNum", pageNum);
			request.setAttribute("board", board);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		 // 6. content.jsp 페이지로 이동
		return "content.jsp";
	}

}
