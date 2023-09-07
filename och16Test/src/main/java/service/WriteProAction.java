package service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.Board;
import dao.BoardDao;

public class WriteProAction implements CommandProcess {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		try {
			// <1> 사용자로부터 입력된 글 정보를 받아옵니다.
			// 1. num , pageNum, writer ,  email , subject , passwd , content   Get
			request.setCharacterEncoding("utf-8");
			String pageNum = request.getParameter("pageNum");
			// 2. Board board(DTO) 생성하고 Value Setting
			Board board = new Board();
			board.setNum(Integer.parseInt(request.getParameter("num")));
			board.setWriter(request.getParameter("writer"));
			board.setEmail(request.getParameter("email"));
			board.setSubject(request.getParameter("subject"));
			board.setPasswd(request.getParameter("passwd"));
			board.setRef(Integer.parseInt(request.getParameter("ref")));
			board.setRe_step(Integer.parseInt(request.getParameter("re_step")));
			board.setRe_level(Integer.parseInt(request.getParameter("re_level")));
			board.setContent(request.getParameter("content"));
			board.setIp(request.getRemoteAddr());
			
			// <2> 데이터베이스에 접근하여 글을 저장합니다.
			// 3. BoardDao bd Instance
			BoardDao bd = BoardDao.getInstance();//DB
			// 4 int result = bd.insert(board);
			int result = bd.insert(board);
			
			// <3> 저장 결과와 관련된 정보를 request 객체에 저장합니다.
			// 5. request 객체에 result, num , pageNum , 왜 얘네만?
			request.setAttribute("pageNum", pageNum);
			request.setAttribute("num", board.getNum());
			request.setAttribute("result", result);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		
		return "writePro.jsp";
	}

}
