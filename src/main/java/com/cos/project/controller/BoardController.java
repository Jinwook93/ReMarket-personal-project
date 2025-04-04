package com.cos.project.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.dto.BoardDTO;
import com.cos.project.dto.ChattingRoomDTO;
import com.cos.project.dto.TradeDTO;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.Buy_Sell;
import com.cos.project.entity.Category;
import com.cos.project.entity.ChattingRoomEntity;
import com.cos.project.entity.CommentEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.entity.TradeEntity;
import com.cos.project.entity.TradeStatus;
import com.cos.project.repository.BoardLikeRepository;
import com.cos.project.repository.BoardRepository;
import com.cos.project.repository.MemberRepository;
import com.cos.project.service.AlarmService;
import com.cos.project.service.BoardService;
import com.cos.project.service.ChatService;
import com.cos.project.service.CommentService;
import com.cos.project.service.MemberService;
import com.cos.project.service.TradeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.bytebuddy.description.modifier.EnumerationState;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/board")
public class BoardController {

	@Autowired
	private ChatService chatService;
	
	private BoardService boardService;

	public BoardController(BoardService boardService) {
		this.boardService = boardService;
	}

	@Autowired
	private CommentService commentService;
	
	@Autowired
	private TradeService tradeService;

	@Autowired
	private BoardLikeRepository boardLikeRepository;

	@ModelAttribute("principalDetails") // 전역적으로 처리
	public Authentication getPrincipalDetails() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    System.out.println("현재 사용자 : "+ authentication.getName());

		return authentication; // 현재 사용자 이름 반환
	}

//@ModelAttribute("loggedUser")			//전역적으로 처리
//public MemberEntity getPrincipalDetails(PrincipalDetails principalDetails) {
//  
//	
//    return principalDetails.getMemberEntity(); // 현재 사용자 이름 반환
//}

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private AlarmService alarmService;


//	@GetMapping("/allboard")
//	public String allBoard(Model model) {
//		List<BoardEntity> searchresult = boardService.allContents();
//		
//		 searchresult.sort((b1, b2) -> Long.compare(b2.getId(), b1.getId()));		//id 역순으로 재정렬
//		
//		model.addAttribute("allBoards", searchresult);
//		return "boardlist";
//	}

	
//	@GetMapping("/list")
//	public String boardList(@RequestParam(name = "page", defaultValue = "1") Integer page,
//	                        @RequestParam(name = "condition", defaultValue = "0") Integer condition,
//	                        Model model) {
////	                        ,@RequestParam(name = "loggedAddress", required = false) String loggedAddress) {
//	    int pageSize = 9;
//	    int pageNumber = page - 1; // 페이지 번호 보정
//
//	    Page<BoardEntity> searchresult;
//	    String loggedAddress = null;
//	 // 모델에서 principalDetails 가져오기
//	    Authentication authentication = (Authentication) model.getAttribute("principalDetails");
//	    if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
//	        // Authentication에서 PrincipalDetails 객체를 가져옵니다.
//	        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//
//	        // PrincipalDetails에서 MemberEntity를 가져옵니다.
//	        MemberEntity memberEntity = principalDetails.getMemberEntity();
//	        
//	        // memberEntity 사용 (예: memberEntity의 ID와 사용자 ID 출력)
//	        	loggedAddress = memberEntity.getAddress();
//	    }
//
//	   System.out.println("컨디션"+condition);
//	   System.out.println("loggedAddress"+loggedAddress);
//	    
//	    
//	    if (loggedAddress != null && !loggedAddress.isEmpty()) {
//	        String[] splitedAddress = loggedAddress.split("/");
//	        searchresult = boardService.getBoardList(pageNumber, pageSize, splitedAddress[0], condition);
//	    } else {
//	        // loggedAddress가 없으면 전체 검색 또는 디폴트 동작
//	        searchresult = boardService.getBoardList(pageNumber, pageSize, null, condition);
//	    }
//
//	    int totalPages = searchresult.getTotalPages();
//	    model.addAttribute("allBoards", searchresult);
//	    model.addAttribute("totalPages", totalPages);
//	    model.addAttribute("currentPage", page); // 현재 페이지 번호
//
//	    return "boardlist";
//	}


	
	
//@GetMapping("/searchboard")
//public List<BoardEntity> searchBoard(@RequestParam String searchindex) {
//	List<BoardEntity> searchresult = boardService.searchContents(searchindex);
//	
//	
//    return searchresult;
//}

	@GetMapping("/writeboard")
	public String writeBoard(Model model, @AuthenticationPrincipal PrincipalDetails principal) {
		model.addAttribute("name", principal.getMemberEntity().getName());
		model.addAttribute("userid", principal.getMemberEntity().getUserid());
		model.addAttribute("profileImage", principal.getMemberEntity().getProfileImage());
		model.addAttribute("nickname", principal.getMemberEntity().getNickname());
		
		return "boardwriteform";
	}

	@PostMapping("/writeboard")
	public String writeBoard(@RequestParam(name = "title") String title, @RequestParam(name = "name") String name,
			@RequestParam(name = "price") int price, @RequestParam(name = "userid") String userid,
			@RequestParam(name = "category") String category, @RequestParam(name = "product") String product,
			@RequestParam(name = "buy_Sell") String buy_Sell,
			@RequestParam(name = "address") String address,
			@RequestParam(name = "address2") String address2,
			@RequestParam(name = "boardFiles", required = false) MultipartFile[] boardFiles,
			@RequestParam(name = "contents") String contents,
			@AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		String[] boardFilePath;

		// boardFiles가 null인 경우 빈 배열로 초기화
		if (boardFiles == null || boardFiles.length == 0
				|| (boardFiles.length == 1 && boardFiles[0].getOriginalFilename().isEmpty())) {
			boardFilePath = new String[0]; // 첨부파일이 없을 경우 빈 배열로 처리
		} else {
			boardFilePath = new String[boardFiles.length];
			int i = 0;

			// 파일 업로드 처리
			for (MultipartFile boardFile : boardFiles) {
				String uniqueFileName = UUID.randomUUID().toString() + "_" + boardFile.getOriginalFilename();
				Path filePath = Paths.get("src/main/resources/static/boardimage").resolve(uniqueFileName);
				Files.createDirectories(filePath.getParent());
				Files.write(filePath, boardFile.getBytes());
				boardFilePath[i++] = "/boardimage/" + uniqueFileName;
			}
		}

		// JSON 변환
		String boardFileJson = objectMapper.writeValueAsString(boardFilePath);

		// String 타입, Enum으로 형변환
		Category category_enum = Category.valueOf(category);
		Buy_Sell buy_Sell_enum = Buy_Sell.valueOf(buy_Sell);

		
		
		// BoardEntity 생성 및 저장
		BoardEntity boardEntity = BoardEntity.builder().title(title).price(price).category(category_enum)
				.product(product).buy_Sell(buy_Sell_enum).contents(contents).boardFiles(boardFileJson)
				.address(address+" "+address2).deleted(false).updated(false)
				.build();

		boardService.writeContents(boardEntity);
		Long loggedId = principalDetails.getMemberEntity().getId();
		alarmService.postAlarm(loggedId,null, null, "BOARD", "게시판", null, "등록", null);	
		return "redirect:/board/list";
	}



	@GetMapping("/updateboard/{id}")
//public String updateBoard(@PathVariable(name = "id") Long id, @RequestBody BoardEntity boardEntity, RedirectAttributes redirectAttributes) {
	public String goUpdateForm(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes, Model model)
			throws JsonMappingException, JsonProcessingException {
		BoardEntity board = boardService.viewContent(id, true);
		model.addAttribute("board", board);

		ObjectMapper om = new ObjectMapper();
		String[] boardFiles = om.readValue(board.getBoardFiles(), String[].class);
		// Redirect to the view page after the update

		model.addAttribute("boardFiles", boardFiles);

		
		return "updateboardwriteform";
	}

	// MultiPart Form 데이터에서 PUT을 지원하지 않아서 Post로 처리

//Multipart/form-data는 주로 웹 폼을 통해 파일 업로드를 처리할 때 사용되며, 기본적으로 POST 메서드와 함께 사용됩니다. 이는 파일 전송이 포함된 요청이 서버에서 더 일반적인 방식으로 처리되기 때문입니다.

	@PostMapping("/updateboard/{id}")
	@ResponseBody
	public ResponseEntity<?> updateBoard(@PathVariable(name = "id") Long id, @RequestPart(name = "boardData") String boardDataJson,
			@RequestPart(value = "boardFiles", required = false) MultipartFile[] boardFiles,
//			@RequestPart(value = "existingFiles", required = false) String existingFiles,  // 문자열로 처리
			 @RequestParam(value = "existingFiles", required = false) String[] existingFiles, // 배열로 받기
			@RequestParam(name = "nullimageButton") Boolean nullimageButton,
			RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		BoardDTO boardDTO = objectMapper.readValue(boardDataJson, BoardDTO.class);

		
		
		//기존파일 정보

//		for(int i =0; i<existingFiles.length;i++) {
//		 System.out.println("Existing Files: " + existingFiles[i]);  // 예시로 출력해봄
//		}
		
		
		if(existingFiles == null) {
			existingFiles = new String [0];
		}
		
		
		
		
		// 기존 파일 목록 처리
//		String[] existingFileList = objectMapper.readValue(boardDTO.getBoardFiles(), String[].class);
		String[] existingFileList = existingFiles;
		for (int i = 0; i < existingFileList.length; i++) {
			existingFileList[i] = existingFileList[i].replace("http://localhost:8081", "");
		}

		// 새 파일 목록 처리
		if (boardFiles == null) {
			boardFiles = new MultipartFile[0]; // boardFiles가 null이면 빈 배열로 초기화
		}

		// 모든 파일 경로 합치기
		String[] allFilePath = Arrays.copyOf(existingFileList, existingFileList.length + boardFiles.length);
		int index = existingFileList.length;
		for (MultipartFile boardFile : boardFiles) {
			String uniqueFileName = UUID.randomUUID().toString() + "_" + boardFile.getOriginalFilename();
			Path filePath = Paths.get("src/main/resources/static/boardimage").resolve(uniqueFileName);
			Files.createDirectories(filePath.getParent());
			Files.write(filePath, boardFile.getBytes());
			allFilePath[index++] = "/boardimage/" + uniqueFileName;
		}

		// 파일 경로를 JSON으로 변환
		String boardFileJson = objectMapper.writeValueAsString(allFilePath);

		
		
		// 게시글 업데이트
		boardService.updateContents(id, boardDTO, boardFileJson,nullimageButton);

		// 리다이렉트 처리
		// redirectAttributes.addAttribute("id", id);
//    return "redirect:/board/view/{id}";
		String id_String = String.valueOf(id);
		Long loggedId = principalDetails.getMemberEntity().getId();
		alarmService.postAlarm(loggedId,null, null, "BOARD", "게시판", id_String, "수정", null);	
//		return "/board/view/" + id;
		
		return ResponseEntity.ok(true);
	}

					//실제 삭제
//	@DeleteMapping("/deleteboard/{id}")
//	public ResponseEntity<?> deleteBoard(@PathVariable(name = "id") Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
//		commentService.deleteAllCommentAboutBoard(id);
////		System.out.println("삭제되는중?" + id);
//		String result = boardService.deleteContents(id);
////		System.out.println("삭제되는중2?" + id);
//		
//		String id_String = String.valueOf(id);
//		Long loggedId = principalDetails.getMemberEntity().getId();
//		alarmService.postAlarm(loggedId,null, null, "BOARD", "게시판", id_String, "삭제", null);	
//		return ResponseEntity.status(200).body(result);
//	}

	
	
	//참조 무결성 이슈로 삭제 대신 숨김 처리를 사용
	@DeleteMapping("/deleteboard/{id}")
	public ResponseEntity<?> deleteBoard(@PathVariable(name = "id") Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		
		Long loggedId = principalDetails.getMemberEntity().getId();
		String result = boardService.hideBoard(id);
		
		BoardEntity boardEntity = boardService.findByBoardId(id);
		
		List<TradeEntity> trades = boardEntity.getTrades();
		TradeEntity tradeEntity =null;
		MemberEntity member1  = null;
		MemberEntity member2  = null;
		ChattingRoomDTO room =null;
		
		if(!trades.isEmpty()) {
			tradeEntity = trades.stream().filter(trade ->  (trade.getTradeStatus().name().equals("완료"))
				).findFirst().orElse(null);
		
		 member1 = tradeEntity.getMember1();
		member2 = tradeEntity.getMember2();	
		
		room = chatService.findRoomByBoardIdAndMemberId(id, member1.getId(), member2.getId(), loggedId);
		}
		
		String id_String = String.valueOf(id);
		
		if(tradeEntity != null) {
			alarmService.postAlarm(loggedId,member1.getId(), member2.getId(), "BOARD", "게시판", String.valueOf(room.getId()), "삭제", null);	
		}else {
			alarmService.postAlarm(loggedId,null, null, "BOARD", "게시판", id_String, "삭제", null);	
		}
		
		return ResponseEntity.status(200).body(result);
	}
	
	
	
	
	
	
	
	
	
	
	@GetMapping("/view/{id}")
	public String viewContent(@PathVariable(name = "id") Long id, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails)
			throws JsonMappingException, JsonProcessingException {
		BoardEntity boardEntity = boardService.viewContent(id, false);
//		List<CommentEntity> comments = commentService.getAllCommentAboutBoard(id);
//		List<CommentEntity> comments = commentService.getFilteredComments(id, principalDetails.getMemberEntity().getUserid());
		ObjectMapper om = new ObjectMapper();
		String[] boardFiles = om.readValue(boardEntity.getBoardFiles(), String[].class);
		
		
	    List<TradeDTO> filteredBoardTrades = boardEntity.getTrades().stream()
	                    .map(trade -> new TradeDTO().fromEntity(trade))
	            .collect(Collectors.toList());
		

		model.addAttribute("board", boardEntity);
//		model.addAttribute("comments", comments);

//		if(boardFiles[0].equals("/boardimage/nullimage.jpg")) {
//			boardFiles = new String[0];
//		}
		model.addAttribute("boardFiles", boardFiles);
		model.addAttribute("tradeDTO",filteredBoardTrades);
		
		if(principalDetails != null) {
		model.addAttribute("loggedId", principalDetails.getMemberEntity().getId());
		}
		// return ResponseEntity.status(200).body(result);
		return "boardcontent";
	}



	
	@GetMapping("/getBoardMainFile/{boardId}")
	@ResponseBody
	public String getBoardMainFile(@PathVariable(name = "boardId") Long boardId, @AuthenticationPrincipal PrincipalDetails principalDetails)
			throws JsonMappingException, JsonProcessingException {
			return boardService.getBoardMainFile(boardId);
			
	}
	
	
	
	
//	@GetMapping("/addressfilter")
//	@ResponseBody		//address 검색 범위에 따라 주소 검색
//	public ResponseEntity<?> addressFilter(@RequestParam(name = "address") Long condition , @AuthenticationPrincipal PrincipalDetails principalDetails)
//			throws JsonMappingException, JsonProcessingException {
//			String[] loggedAddress = principalDetails.getMemberEntity().getAddress().split("/");
//			List<BoardEntity> filteredBoards= boardService.searchBoardListByAddressFilter(condition, loggedAddress[0]);
//			return ResponseEntity.ok(filteredBoards);
//	}
	
	
	

//	@PostMapping("/searchBoardManager")
//	public String getBoardResult(@RequestParam(name = "page", defaultValue = "1") Integer page,
//	                             @ModelAttribute BoardDTO boardDTO, 
//	                             Model model) {
//	    int pageSize = 9;
//	    int pageNumber = page - 1; // 페이지 번호 보정
//	    
//	    // 전체 게시글 목록을 가져옴
//	    List<BoardEntity> boards = boardService.allContents();
//	    
//	    // 필터링 조건에 맞는 게시글을 추출
//	    List<BoardEntity> filteredBoards = boards.stream()
//	        .filter(board -> {
//	            boolean matchesTitle = true;
//	            boolean matchesContents = true;
//	            boolean matchesMemberUserId = true;
//	            boolean matchesPrice = true;
//	            boolean matchesAddress = true;
//	            boolean matchesCategory = true;
//	            boolean matchesBuySell = true;
//	            boolean matchesProduct = true;
//
//	            // 필터링 로직 (이전과 동일)
//	            if (boardDTO.getTitle() != null && !boardDTO.getTitle().isEmpty()) {
//	                matchesTitle = board.getTitle().contains(boardDTO.getTitle());
//	            }
//	            if (boardDTO.getContents() != null && !boardDTO.getContents().isEmpty()) {
//	                matchesContents = board.getContents().contains(boardDTO.getContents());
//	            }
//	            if (boardDTO.getMemberUserId() != null && !boardDTO.getMemberUserId().isEmpty()) {
//	                matchesMemberUserId = board.getMemberEntity().getUserid().contains(boardDTO.getMemberUserId());
//	            }
//	            if (boardDTO.getPrice() != null && boardDTO.getPrice() > 0) {
//	                matchesPrice = board.getPrice() == boardDTO.getPrice();
//	            }
//	            if (boardDTO.getAddress() != null && !boardDTO.getAddress().isEmpty()) {
//	                matchesAddress = board.getAddress().contains(boardDTO.getAddress());
//	            }
//	            if (boardDTO.getCategory() != null) {
//	                matchesCategory = board.getCategory().name().equals(boardDTO.getCategory().name());
//	            }
//	            if (boardDTO.getBuy_Sell() != null) {
//	                matchesBuySell = board.getBuy_Sell().name().equals(boardDTO.getBuy_Sell().name());
//	            }
//	            if (boardDTO.getProduct() != null && !boardDTO.getProduct().isEmpty()) {
//	                matchesProduct = board.getProduct().contains(boardDTO.getProduct());
//	            }
//
//	            return matchesTitle && matchesContents && matchesMemberUserId &&
//	                   matchesPrice && matchesAddress && matchesCategory && matchesBuySell && matchesProduct;
//	        })
//	        .collect(Collectors.toList());
//
//	    filteredBoards.sort(Comparator.comparing(BoardEntity::getCreateTime).reversed());
//
//	    // 페이징 처리
//	    int startIndex = pageNumber * pageSize;
//	    int endIndex = Math.min(startIndex + pageSize, filteredBoards.size());
//	    
//	    List<BoardEntity> pagedBoards = filteredBoards.subList(startIndex, endIndex);
//	    
//	    // 총 페이지 수 계산
//	    int totalPages = (int) Math.ceil((double) filteredBoards.size() / pageSize);
//	    
//	    // 검색된 결과와 페이지 정보 모델에 추가
//	    model.addAttribute("allBoards", pagedBoards);
//	    model.addAttribute("currentPage", page);
//	    model.addAttribute("totalPages", totalPages);
//	    model.addAttribute("boardDTO", boardDTO);  // 검색 조건도 다시 추가
//	    
//	    return "searchboardresult";
//	}

	
//	@GetMapping("/searchBoardManager")
//	public String searchBoardManagerPage(@RequestParam(name = "page",defaultValue = "1") int page,
//			@RequestParam(name = "condition", required = false) Integer condition,
//	                                     @ModelAttribute BoardDTO boardDTO, 
//	                                     Model model) {
//	    int pageSize = 9;
//	    int pageNumber = page - 1; // 페이지 번호 보정
//	    
//	    
//	    // 전체 게시글 목록을 가져옴
//	    List<BoardEntity> boards = boardService.allContents();
//	    
////	    if(condition != null && !boardDTO.getAddress().isEmpty() && boardDTO.getAddress() !=null) {
////	    String [] splitedAddress = boardDTO.getAddress().split("/");
////	    boards =   boardService.getBoardList2( splitedAddress[0], condition);
////	    }
//	    // 필터링 조건에 맞는 게시글을 추출
//	    List<BoardEntity> filteredBoards = boards.stream()
//	        .filter(board -> {
//	            boolean matchesTitle = true;
//	            boolean matchesContents = true;
//	            boolean matchesMemberUserId = true;
//	            boolean matchesPrice = true;
//	            boolean matchesAddress = true;
//	            boolean matchesCategory = true;
//	            boolean matchesBuySell = true;
//	            boolean matchesProduct = true;
//
//	            // 필터링 로직 (이전과 동일)
//	            if (boardDTO.getTitle() != null && !boardDTO.getTitle().isEmpty()) {
//	                matchesTitle = board.getTitle().contains(boardDTO.getTitle());
//	            }
//	            if (boardDTO.getContents() != null && !boardDTO.getContents().isEmpty()) {
//	                matchesContents = board.getContents().contains(boardDTO.getContents());
//	            }
//	            if (boardDTO.getMemberUserId() != null && !boardDTO.getMemberUserId().isEmpty()) {
//	                matchesMemberUserId = board.getMemberEntity().getUserid().contains(boardDTO.getMemberUserId());
//	            }
//	            if (boardDTO.getPrice() != null && boardDTO.getPrice() > 0) {
//	                matchesPrice = board.getPrice() == boardDTO.getPrice();
//	            }
////	            if (boardDTO.getAddress() != null && !boardDTO.getAddress().isEmpty() && board.getAddress() != null && !board.getAddress().isEmpty()) {
////	                matchesAddress = board.getAddress().contains(boardDTO.getAddress());
////	            }
//	            
//	            
//	            
//	            if (boardDTO.getAddress() != null && !boardDTO.getAddress().isEmpty()) {
//	                // board.getAddress()가 null이거나 비어있으면 무조건 false 처리
//	                if (board.getAddress() == null || board.getAddress().isEmpty()) {
//	                    matchesAddress = false;
//	                } else {
//	                	String [] splitedAddress = boardDTO.getAddress().split("/");
//    	    		    String [] splitedAddress2 = splitedAddress[0].split(" ");
//	                	
////	            	    if(condition != null && condition == 0 ) {
////	            	    			//	    	    		    boardDTO.setAddress(""); 
////	    	    		    }else
//	    	    		    	if(condition != null && condition ==1 &&  splitedAddress2.length > 0) {
//		    	    		    boardDTO.setAddress(splitedAddress2[0]); 
//		    	    		    }else if(condition != null && condition ==2 &&  splitedAddress2.length > 1) {
//			    	    		    boardDTO.setAddress(splitedAddress2[0] + " " +splitedAddress2[1]); 
//			    	    		    }
//		    	    		    else if(condition != null && condition ==3 &&  splitedAddress2.length > 2) {
//				    	    		    boardDTO.setAddress(boardDTO.getAddress()); 
//				    	    		    }
//	                	
//	                    matchesAddress = board.getAddress().contains(condition != null && condition == 0 ? "":boardDTO.getAddress());
//	                }
//	            }
//	            
//	            
//	            
//	            
//	            if (boardDTO.getCategory() != null) {
//	                matchesCategory = board.getCategory().name().equals(boardDTO.getCategory().name());
//	            }
//	            if (boardDTO.getBuy_Sell() != null) {
//	                matchesBuySell = board.getBuy_Sell().name().equals(boardDTO.getBuy_Sell().name());
//	            }
//	            if (boardDTO.getProduct() != null && !boardDTO.getProduct().isEmpty()) {
//	                matchesProduct = board.getProduct().contains(boardDTO.getProduct());
//	            }
//
//	            return matchesTitle && matchesContents && matchesMemberUserId &&
//	                   matchesPrice && matchesAddress && matchesCategory && matchesBuySell && matchesProduct;
//	        })
//	        .collect(Collectors.toList());
//
//	    filteredBoards.sort(Comparator.comparing(BoardEntity::getCreateTime).reversed());
//
//	    // 페이징 처리
//	    int startIndex = pageNumber * pageSize;
//	    int endIndex = Math.min(startIndex + pageSize, filteredBoards.size());
//	    
//	    List<BoardEntity> pagedBoards = filteredBoards.subList(startIndex, endIndex);
//	    
//	    // 총 페이지 수 계산
//	    int totalPages = (int) Math.ceil((double) filteredBoards.size() / pageSize);
//	    
//	    // 검색된 결과와 페이지 정보 모델에 추가
//	    model.addAttribute("allBoards", pagedBoards);
//	    model.addAttribute("currentPage", page);
//	    model.addAttribute("totalPages", totalPages);
//	    model.addAttribute("boardDTO", boardDTO);  // 검색 조건 유지
//	    
//	    return "searchboardresult";
//	}

	@GetMapping("/list")
	public String boardList(@RequestParam(name = "page", defaultValue = "1") Integer page,
	                        @RequestParam(name = "condition", defaultValue = "0") Integer condition,
	                        @RequestParam(name ="searched", required=false) Boolean searched, 
	                        @RequestParam(name ="tradestatus", required=false) String tradestatus, 
	                        @RequestParam(name ="min_price", required=false) Integer min_price, 
	                        @RequestParam(name ="max_price", required=false) Integer max_price, 
	                        @ModelAttribute BoardDTO boardDTO,
	                        Model model) {

	    int pageSize = 4;
	    int pageNumber = page - 1;

	    // 로그인한 사용자 주소 가져오기
	    String loggedAddress = null;
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
	        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
	        MemberEntity memberEntity = principalDetails.getMemberEntity();
	        loggedAddress = memberEntity.getAddress();
	    }

	    // 전체 게시글 가져오기	(deleted TRUE 인 게시글 제외)
	    List<BoardEntity> boards = boardService.allContents();

	    // 주소 기본값 설정
	    if ((boardDTO.getAddress() == null || boardDTO.getAddress().isEmpty()) && searched == null) {
	        boardDTO.setAddress(loggedAddress);
	    }
	    	
	    

	    
	    
	    
	 // 필터링
	    List<BoardEntity> filteredBoards = boards.stream()
	        .filter(board -> boardService.isBoardMatched(board, boardDTO, condition,min_price,max_price))
	        .filter(board -> {
	            if (tradestatus == null || tradestatus.isEmpty() || tradestatus.equals("")) {
	                return true; // tradestatus 없으면 필터링 건너뜀
	            }
	            if ("팝니다".equals(tradestatus) || "삽니다".equals(tradestatus)) {
	                return board.getBuy_Sell().name().equals(tradestatus);
	            }   
	            
	            if ("미거래".equals(tradestatus) ) {
	                return board.getTrades().size()==0;
	            }  
	            return board.getTrades().stream().anyMatch(trade -> trade != null && (
	                ("거래중".equals(tradestatus) && trade.getAccept1() && trade.getAccept2() &&
	                    (Boolean.FALSE.equals(trade.getCompleted1()) || Boolean.FALSE.equals(trade.getCompleted2()))) ||
	                ("예약중".equals(tradestatus) && trade.getBooking1() && trade.getBooking2()) ||
	                ("거래완료".equals(tradestatus) && trade.getCompleted1() && trade.getCompleted2())
	            ));
	        })
	        .sorted(Comparator.comparing(
	        	    (BoardEntity board) -> board.getReCreateTime() != null ? board.getReCreateTime() : board.getCreateTime()
	        	).reversed())
	        .collect(Collectors.toList());



	    // 페이징 처리
	    int startIndex = pageNumber * pageSize;
	    int endIndex = Math.min(startIndex + pageSize, filteredBoards.size());
	    List<BoardEntity> pagedBoards = filteredBoards.subList(Math.max(0, startIndex), endIndex);

	    int totalPages = (int) Math.ceil((double) filteredBoards.size() / pageSize);

	    // 모델 추가
	    model.addAttribute("allBoards", pagedBoards);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("boardDTO", boardDTO); // 검색 조건 유지
	    model.addAttribute("tradestatus", tradestatus); 
	    return "boardlist";
	}


	
	
	
	
	
	
}
