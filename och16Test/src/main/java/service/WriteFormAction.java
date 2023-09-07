package service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.Board;
import dao.BoardDao;

public class WriteFormAction implements CommandProcess {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("WriteFormAction Start...");
		
		try {
			// 신규글
			int num = 0, ref = 0, re_level = 0, re_step  = 0;
			String pageNum = request.getParameter("pageNum");
			if(pageNum == null) pageNum = "1";

			// 댓글일 경우
			if (request.getParameter("num") != null) {
				num = Integer.parseInt(request.getParameter("num"));  // 글 번호 설정
				
				// 3. 해당 글 번호로 기존 글 정보를 데이터베이스에서 가져옴
				BoardDao bd = BoardDao.getInstance();
				Board board = bd.select(num);
				
				// 4. 가져온 글 정보에서 참조 글 번호, 답변 레벨, 답변 순서를 설정
				ref = board.getRef();	// 참조 글 번호 설정
				re_level = board.getRe_level();	// 답변 레벨 설정
				re_step = board.getRe_step();	// 답변 순서 설정
			}
			
			// 5. 설정한 변수들을 request 객체에 저장하여 JSP 파일에서 사용할 수 있게 함
			request.setAttribute("num", num);	
			//왜 이런데서는 get 어쩌구 안써? -> setAttribute 메소드는 주로 HttpServletRequest 객체에서 사용
			request.setAttribute("ref", ref);	//이름표와, 물건 넣기
			request.setAttribute("re_level", re_level);
			request.setAttribute("re_step", re_step);
			request.setAttribute("pageNum", pageNum);
			
		} catch (Exception e) {
			System.out.println("WriteFormAction Exception->"+e.getMessage());
		}
		
		return "writeForm.jsp";
	}

}
