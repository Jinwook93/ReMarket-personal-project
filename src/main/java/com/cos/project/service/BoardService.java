package com.cos.project.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.dto.BoardDTO;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.Category;
import com.cos.project.entity.MemberEntity;
import com.cos.project.repository.BoardRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
public class BoardService {

	private final BoardRepository boardRepository;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	public BoardService(BoardRepository boardRepository) {
		this.boardRepository = boardRepository;
	}

	@Autowired
	HttpSession session;

	public List<BoardEntity> allContents() { // 글 목록 조회

		List<BoardEntity> boards = boardRepository.findAll();

		return boards;

	}

	@Transactional
	public void writeContents(BoardEntity boardEntity) throws IllegalArgumentException { // 글쓰기

		if (boardEntity.getTitle() == null || boardEntity.getContents() == null) {
			throw new IllegalArgumentException("제목이나 내용이 비어있습니다");
		} else {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
			boardEntity.setMemberEntity(principalDetails.getMemberEntity());
			boardRepository.save(boardEntity);
		}

	}

	@Transactional
	public String updateContents(Long id, BoardDTO boardDTO, String boardFiles) throws IllegalArgumentException { // 수정하기

		if (boardDTO.getTitle() == null || boardDTO.getContents() == null) {
			throw new IllegalArgumentException("제목이나 내용이 비어있습니다");
		}

		BoardEntity boardEntity = boardRepository.findById(id)
				.orElseThrow(() -> new IllegalStateException("수정할 게시글을 찾을 수 없습니다"));

		boardEntity.setTitle(boardDTO.getTitle());
		boardEntity.setContents(boardDTO.getContents());
		boardEntity.setBoardFiles(boardFiles);

		boardRepository.save(boardEntity);
		System.out.println("게시글 수정이 완료되었습니다");
		return "게시글 수정이 완료되었습니다";
	}

	@Transactional
	public String deleteContents(Long id) throws IllegalArgumentException { // 삭제하기

		BoardEntity board = boardRepository.findById(id)
				.orElseThrow(() -> new IllegalStateException("삭제할 게시글을 찾을 수 없습니다"));

		boardRepository.deleteById(id);

		return "게시글 삭제가 완료되었습니다";
	}

	@Transactional
	public List<BoardEntity> searchContents(String searchindex) { // 검색하기

		List<BoardEntity> result = boardRepository.searchResult(searchindex).orElse(new ArrayList<>());

		return result;

	}

	@Transactional // 게시물 보기
	public BoardEntity viewContent(Long id, boolean updateData) {

		BoardEntity boardEntity = boardRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

		if (updateData == false) { // 데이터를 수정하지 않을 경우 조회수 증가
			boardEntity.setView(boardEntity.getView() + 1);

			// 조회수 갱신 후 데이터베이스에 저장
			boardRepository.save(boardEntity); // 변경된 엔티티를 저장
		}

		return boardEntity;

	}

	public List<BoardEntity> findMyBoards(Long id) {
		List<BoardEntity> myboards = boardRepository.findByMemberID(id);

		return myboards;
	}

	public List<BoardEntity> searchBoard(String category1, String category2, String search) {
	    List<BoardEntity> searchresult = new ArrayList<>();

	    System.out.println("검색창도착2");

	    if (category1.equals("")) {
	        if (category2.equals("title")) {
	            searchresult = boardRepository.searchResult(search).orElse(new ArrayList<>());
	        } else if (category2.equals("contents")) {
	            searchresult = boardRepository.searchContentsResult(search).orElse(new ArrayList<>());
	        } else if (category2.equals("userid")) {
	            searchresult = boardRepository.searchMemberUseridResult(search).orElse(new ArrayList<>());
	        } else if (category2.equals("name")) {
	            searchresult = boardRepository.searchMembernameResult(search).orElse(new ArrayList<>());
	        }
	    } else {
	        Category category = Category.valueOf(category1);
	        if (category2.equals("title")) {
	            searchresult = boardRepository.findByCategoryAndTitleAndsearch(category, search).orElse(new ArrayList<>());
	        } else if (category2.equals("contents")) {
	            searchresult = boardRepository.findByCategoryAndContentsAndsearch(category, search).orElse(new ArrayList<>());
	        } else if (category2.equals("userid")) {
	            searchresult = boardRepository.findByCategoryAndUseridAndsearch(category, search).orElse(new ArrayList<>());
	        } else if (category2.equals("name")) {
	            searchresult = boardRepository.findByCategoryAndNameAndsearch(category, search).orElse(new ArrayList<>());
	        }
	    }

	    System.out.println("검색창도착3");

	    return searchresult;
	}


}
