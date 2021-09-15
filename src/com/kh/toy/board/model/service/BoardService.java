package com.kh.toy.board.model.service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kh.toy.board.model.dao.BoardDao;
import com.kh.toy.board.model.dto.Board;
import com.kh.toy.common.db.JDBCTemplate;
import com.kh.toy.common.file.FileDTO;

public class BoardService {
	
	private JDBCTemplate template = JDBCTemplate.getInstance();
	private BoardDao boardDao = new BoardDao();
	
	public Map<String, Object> selectBoardDetail(String bdIdx) {
		
		Connection conn = template.getConnection();
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			
			Board board = boardDao.selectBoardDetail(bdIdx, conn);
			List<FileDTO> files = boardDao.selectFileDTOs(bdIdx, conn);
			res.put("board", board);
			res.put("files", files);
			
		} finally {
			template.close(conn);
		}
		
		return null;
	}

	public void insertBoard(Board board, List<FileDTO> fileDTOs) {
		// TODO Auto-generated method stub
		
	}

}
