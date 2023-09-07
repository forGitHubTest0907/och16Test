package service;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.Board;
import dao.BoardDao;

public class ListAction implements CommandProcess {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("ListAction Start...");
		
		// 게시판 데이터를 다루는 BoardDao 객체 생성
		BoardDao bd = BoardDao.getInstance();
		try {
			//	게시판 총 갯수
			int totCnt = bd.getTotalCnt(); 	 	//38 
			
			// 현재 페이지 번호 확인
			String pageNum = request.getParameter("pageNum");
			if (pageNum==null || pageNum.equals("")) {	pageNum = "1";	}	// 기본적으로 1 페이지를 보여줌
			int currentPage = Integer.parseInt(pageNum);	// 현재 페이지 번호, 예를 들어 1
			
			// 한 페이지에 표시할 게시물 수와 페이지 블록 크기 설정
			int pageSize = 10, blockSize = 10;	// 한 페이지의 게시물 수, 페이지들 블록 크기
			
			// 현재 페이지에 해당하는 게시물의 시작과 끝 행번호 계산
			int startRow = (currentPage - 1) * pageSize + 1;	// (1-1) * 10 + 1,  1부터, 	11부터
			int endRow = startRow + pageSize - 1;	// 1 + 10 -1,  10까지,	20까지
			
			// 시작 번호 계산
			int startNum = totCnt - startRow + 1;	// 예를 들어, 38 - 1 + 1 = 38
			
			// 게시판에서 글 목록 조회					1		10
			List<Board> boardList = bd.boardList(startRow,endRow);
			
			// 전체 페이지 수 계산
			int pageCnt = (int)Math.ceil((double)totCnt/pageSize);	// 예를 들어, 38 / 10 = 4
			
			// 페이지 블록의 시작과 끝 페이지 번호 계산
			int startPage = (int)((currentPage-1)/blockSize)*blockSize + 1;	// 1	2
			int endPage = startPage + blockSize -1 ;	// 10	11
			
			// 마지막 페이지에서 공백 페이지 방지 10 > 4
			if (endPage > pageCnt) endPage = pageCnt;	// 4
			
			System.out.println("ListAction startPage->"+startPage);
			System.out.println("ListAction endPage->"+endPage);
			System.out.println("ListAction pageCnt->"+pageCnt);
			System.out.println("ListAction blockSize->"+blockSize);
			// request 객체에 데이터 저장
			request.setAttribute("boardList", boardList);	// 글 목록
			request.setAttribute("totCnt", totCnt);
			request.setAttribute("pageNum", pageNum);
			request.setAttribute("currentPage", currentPage);
			request.setAttribute("startNum", startNum);
			request.setAttribute("blockSize", blockSize);		// 페이지 블록 크기
			request.setAttribute("pageCnt", pageCnt);			// 전체 페이지 수
			request.setAttribute("startPage", startPage);		// 페이지 블록의 시작 페이지 번호
			request.setAttribute("endPage", endPage);			// 페이지 블록의 끝 페이지 번호
			
		} catch (Exception e) {
			System.out.println("ListAction e.getMessage()->"+e.getMessage());
		}

		// 		View 명칭
		return "list.jsp";
	}

}
