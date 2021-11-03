package com.kh.toy.board.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kh.toy.board.model.dto.Board;
import com.kh.toy.board.model.service.BoardService;
import com.kh.toy.common.exception.PageNotFoundException;
import com.kh.toy.common.file.FileDTO;
import com.kh.toy.common.file.FileUtil;
import com.kh.toy.common.file.MultiPartParams;
import com.kh.toy.member.model.dto.Member;
import com.oreilly.servlet.MultipartRequest;

/**
 * Servlet implementation class BoardController
 */
@WebServlet("/board/*")
public class BoardController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private BoardService boardService = new BoardService();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BoardController() {
		super();
		// TODO Auto-generated constructor stub
		System.out.println("fkfkfk");
		System.out.println("qwer");
		System.out.println("qqqq");
		System.out.println("asdf");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String[] uriArr = request.getRequestURI().split("/");

		switch (uriArr[uriArr.length - 1]) {
		case "board-form":
			boardForm(request, response);
			break;
		case "upload":
			upload(request, response);
			break;
		case "board-detail":
			boardDetail(request, response);
			break;

		default:
			throw new PageNotFoundException();

		}

	}

	private void boardDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//bdIdx로 게시글 상세 데이터 조회
		String bdIdx = request.getParameter("bdIdx");
		Map<String, Object> datas = boardService.selectBoardDetail(bdIdx);
		
		
		request.setAttribute("datas", datas);
		request.getRequestDispatcher("/board/board-detail").forward(request, response);
		
		
	}

	private void upload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		FileUtil util = new FileUtil();
		MultiPartParams params = util.fileUpload(request);
		
		Member member = (Member) request.getSession().getAttribute("authentication");
		
		Board board = new Board();
		board.setUserId(member.getUserId());
		board.setTitle(params.getParameter("title"));
		board.setContent(params.getParameter("content"));
		
		List<FileDTO> fileDTOs = params.getFilesInfo();
		boardService.insertBoard(board, fileDTOs);
		response.sendRedirect("/");
		// 게시물 저장을 위해 board DTO 생성
		/*(테스트용코드)
		 * Board board = new Board(); Map<String,List> paramMap =
		 * util.fileUpload(request); board.setTitle((String)
		 * paramMap.get("title").get(0)); board.setContent((String)
		 * paramMap.get("content").get(0));
		 * 
		 * board.setTitle(params.getParameter("title"));
		 * board.setContent(params.getParameter("content"));
		 * 
		 * String[] titles = params.getParameterValues("title"); List<FileDTO> fileDTOs
		 * = params.getFilesInfo(); for (FileDTO fileDTO : fileDTOs) {
		 * System.out.println(fileDTO); }
		 */
	}

	private void boardForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println(request);
		request.getRequestDispatcher("/board/board-form").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
