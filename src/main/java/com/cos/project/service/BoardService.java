package com.cos.project.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.dto.BoardDTO;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.BoardLikeEntity;
import com.cos.project.entity.Category;
import com.cos.project.entity.MemberEntity;
import com.cos.project.repository.BoardLikeRepository;
import com.cos.project.repository.BoardRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
public class BoardService {

	private final BoardRepository boardRepository;

	private final BoardLikeRepository boardLikeRepository;
	@Autowired
	private AuthenticationManager authenticationManager;
	

	@Autowired
	public BoardService(BoardRepository boardRepository, BoardLikeRepository boardLikeRepository) {
		this.boardRepository = boardRepository;
		this.boardLikeRepository = boardLikeRepository;
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
		boardEntity.setBuy_Sell(boardDTO.getBuy_Sell());
		boardEntity.setPrice(boardDTO.getPrice());
		boardEntity.setCategory(boardDTO.getCategory());
		boardEntity.setProduct(boardDTO.getProduct());
		boardEntity.setAddress(boardDTO.getAddress());
		
		boardRepository.save(boardEntity);
		System.out.println("게시글 수정이 완료되었습니다");
		return "게시글 수정이 완료되었습니다";
	}

	@Transactional
	public String deleteContents(Long id) throws IllegalArgumentException { // 삭제하기

	
		
				
		BoardEntity board = boardRepository.findById(id)
				.orElseThrow(() -> new IllegalStateException("삭제할 게시글을 찾을 수 없습니다"));
		
		boardLikeRepository.deleteAllboardLikeEntityByBoardId(id);	//게시물과 연관된 좋아요, 삭제요 삭제

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

	public List<BoardEntity> searchBoard(String buy_Sell, String category1, String category2, String search) {
	    List<BoardEntity> searchresult = new ArrayList<>();

	    System.out.println("검색창도착2");

	    
	    
	    if(buy_Sell.equals("")) {
	    	
	    }
	    else if (buy_Sell.equals("팝니다")) {
	    	
	    }   else if (buy_Sell.equals("삽니다")) {
	    	
	    }
	    
	    
	    
	    
	    
	    
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

	public BoardEntity findByBoardId(Long boardId) {
			return boardRepository.findById(boardId).orElse(null);
	}

	@Transactional
	public Page<BoardEntity> getBoardList(int page, int pageSize, String address, int condition) {
	    // 페이지 요청 생성
	    PageRequest pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));
	    
	    // address가 null이거나 비어있는지 체크
	    if (address == null || address.trim().isEmpty()) {
	        address = "";  // address가 비어있으면 빈 문자열로 처리
	    }

	    String[] filteredAddress = address.split(" ");
	    

	    
	    // 주소와 condition에 따른 필터링
	    if (condition == 0 || address.isEmpty()) {
	        // condition이 0이거나 address가 비어있으면 모든 boardEntity를 가져옴
	        return boardRepository.findAll(pageable);  // 조건 없이 모든 데이터 반환
	    } else if (condition == 1 && filteredAddress.length > 0) {
	        return boardRepository.findAllByAddress(filteredAddress[0], pageable);  // 첫 번째 주소 단어 기준
	    } else if (condition == 2 && filteredAddress.length > 1) {
	        return boardRepository.findAllByAddress(filteredAddress[0] + " " + filteredAddress[1], pageable);  // 첫 두 단어 기준
	    } else if (condition == 3) {
	        return boardRepository.findAllByAddress(address, pageable);  // 전체 주소 기준
	    }

	    return boardRepository.findAll(pageable);  // 기본적으로 모든 데이터를 반환
	}



	
	
	@Transactional
	public String getBoardMainFile(Long boardId)
	        throws JsonMappingException, JsonProcessingException {
	    BoardEntity boardEntity = boardRepository.findById(boardId).orElse(null);
	    if (boardEntity == null) {
	        // Handle case when boardEntity is not found, if needed
	        return "/boardimage/nullimage.jpg";
	    }
	    
	    ObjectMapper om = new ObjectMapper();
	    String[] boardFiles = om.readValue(boardEntity.getBoardFiles(), String[].class);
	    
	    if (boardFiles != null && boardFiles.length > 0) {
	        String mainFile = boardFiles[0];
	        return mainFile;
	    } else {
	        return "/boardimage/nullimage.jpg";
	    }
	}

//	public List<BoardEntity> getBoardList2(String address, int condition) {
//	    // 페이지 요청 생성
//
//	    // address가 null이거나 비어있는지 체크
//	    if (address == null || address.trim().isEmpty()) {
//	        address = "";  // address가 비어있으면 빈 문자열로 처리
//	    }
//
//	    // 주소를 공백으로 분리
//	    String[] filteredAddress = address.split("\\s+");  // 여러 공백을 하나로 처리하는 정규식
//
//	    // 디버깅: 로그 출력
//	    System.out.println("컨디션: " + condition);
//	    if (filteredAddress.length > 0) {
//	        System.out.println("시: " + filteredAddress[0]);
//	    }
//	    if (filteredAddress.length > 1) {
//	        System.out.println("군: " + filteredAddress[1]);
//	    }
//	    if (filteredAddress.length > 2) {
//	        System.out.println("구: " + filteredAddress[2]);
//	    }
//
//	    // 주소와 condition에 따른 필터링
//	    if (condition == 0 || address.isEmpty()) {
//	        // condition이 0이거나 address가 비어있으면 모든 boardEntity를 가져옴
//	        return boardRepository.findAll();  // 조건 없이 모든 데이터 반환
//	    } else if (condition == 1 && filteredAddress.length > 0) {
//	        // 첫 번째 주소 단어 기준으로 필터링
//	        return boardRepository.findAllByAddress2(filteredAddress[0]);
//	    } else if (condition == 2 && filteredAddress.length > 1) {
//	        // 첫 두 단어 기준으로 필터링
//	        return boardRepository.findAllByAddress2(filteredAddress[0] + " " + filteredAddress[1]);
//	    } else if (condition == 3) {
//	        // 전체 주소 기준으로 필터링
//	        return boardRepository.findAllByAddress2(address);
//	    }
//
//	    // 기본적으로 모든 데이터를 반환
//	    return boardRepository.findAll();
//	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	// addressFilter condition (주소 검색 범위)
//	@Transactional
//	public List<BoardEntity> searchBoardListByAddressFilter(Long condition, String loggedAddress) {
//	    List<BoardEntity> boards = boardRepository.findAll();
//
//	    // 내 주소를 공백으로 나눔 (예: "서울특별시 강남구 역삼동")
//	    String[] splitedAddress = loggedAddress.split(" ");
//	    System.out.println("내 주소 분리 결과: " + Arrays.toString(splitedAddress));
//
//	    // 0번이면 전체 조회
//	    if (condition == 0L) {
//	        return boards;
//	    }
//
//	    // 비교 범위 계산 (4 -> 시/도까지, 3 -> 시/군/구까지, 2 -> 동까지 등등)
//	    int compareDepth = condition.intValue();
//
//	    List<BoardEntity> filteredBoards = boards.stream().filter(board -> {
//	        String boardAddress = board.getAddress();  // "서울특별시 강남구 역삼동/상세주소"
//	        String[] boardSplited = boardAddress.split(" ");
//
//	        // 주소 depth가 부족하면 제외
//	        if (splitedAddress.length < compareDepth || boardSplited.length < compareDepth) return false;
//
//	        // depth만큼 비교
//	        for (int i = 0; i < compareDepth; i++) {
//	            if (!splitedAddress[i].equals(boardSplited[i])) {
//	                return false;
//	            }
//	        }
//
//	        return true;
//	    }).collect(Collectors.toList());
//
//	    return filteredBoards;
//	}


	public boolean isBoardMatched(BoardEntity board, BoardDTO dto, Integer condition, Integer min_price, Integer max_price) {
	    if (dto.getTitle() != null && !dto.getTitle().isEmpty() && !board.getTitle().contains(dto.getTitle())) return false;
	    if (dto.getContents() != null && !dto.getContents().isEmpty() && !board.getContents().contains(dto.getContents())) return false;
	    if (dto.getMemberUserId() != null && !dto.getMemberUserId().isEmpty() && !board.getMemberEntity().getUserid().contains(dto.getMemberUserId())) return false;
//	    if (dto.getPrice() != null && dto.getPrice() > 0 && !dto.getPrice().equals(board.getPrice())) return false;

	    // 주소 필터링
	    if (dto.getAddress() != null && !dto.getAddress().isEmpty()) {
	        String conditionAddress = getAddressByCondition(dto.getAddress(), condition);
	        if (board.getAddress() == null || !board.getAddress().contains(conditionAddress)) return false;
	    }

	    if (dto.getCategory() != null && board.getCategory() != dto.getCategory()) return false;
	    if (dto.getBuy_Sell() != null && board.getBuy_Sell() != dto.getBuy_Sell()) return false;
	    if (dto.getProduct() != null && !dto.getProduct().isEmpty() && !board.getProduct().contains(dto.getProduct())) return false;

	    // ✅ 가격 범위 필터링
	    if (min_price != null && max_price != null) {
	        // 둘 다 값이 있으면 min_price 이상, max_price 이하로 비교
	        if (board.getPrice() < min_price || board.getPrice() > max_price) return false;
	    } else if (min_price != null) {
	        // min_price만 값이 있으면 min_price 이상으로 비교
	        if (board.getPrice() < min_price) return false;
	    } else if (max_price != null) {
	        // max_price만 값이 있으면 max_price 이하로 비교
	        if (board.getPrice() > max_price) return false;
	    }

	    return true;
	}


	
	
	public String getAddressByCondition(String fullAddress, Integer condition) {
	    if (fullAddress == null || fullAddress.isEmpty()) return "";
	    
//	    String[] addresses = fullAddress.split("/");
	    
	    String[] split =  fullAddress.split(" ");
	    if (condition == null) return "";
	    if (condition == 1 && split.length > 0) return split[0];
	    if (condition == 2 && split.length > 1) return split[0] + " " + split[1];
	    if (condition == 3 && split.length > 2) return split[0] + " " + split[1] + " " +split[2];
	    return "";
	}


	
	
	
	
	
}
