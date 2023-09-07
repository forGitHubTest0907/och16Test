package service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.Board;
import dao.BoardDao;

public class UpdateProAction implements CommandProcess {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 1. num , pageNum, writer ,  email , subject , passwd , content   Get
		request.setCharacterEncoding("utf-8");
		String pageNum = request.getParameter("pageNum");
				
			try {
			// 2. Board board 생성하고 Value Setting
			Board board = new Board();
			board.setNum(Integer.parseInt(request.getParameter("num")));
			board.setWriter(request.getParameter("writer"));
			board.setEmail(request.getParameter("email"));
			board.setSubject(request.getParameter("subject")); 
			board.setPasswd(request.getParameter("passwd"));
			board.setContent(request.getParameter("content"));
			board.setIp(request.getRemoteAddr());
			/* 얘네는 왜 안들어가지
			 * board.setRef(Integer.parseInt(request.getParameter("ref")));
			 * board.setRe_step(Integer.parseInt(request.getParameter("re_step")));
			 * board.setRe_level(Integer.parseInt("re_level"));
			 */
			
			// 3. BoardDao bd Instance
			BoardDao bd = BoardDao.getInstance();
			
			// int result = bd.update(board);
			int result = bd.update(board);
			
			// 5.updatePro.jsp Return
			request.setAttribute("num", board.getNum());
			request.setAttribute("pageNum", pageNum);
			request.setAttribute("result", result);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return "updatePro.jsp";
	}

}
