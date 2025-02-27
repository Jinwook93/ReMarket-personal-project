package com.cos.project.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.dto.BoardDTO;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.Buy_Sell;
import com.cos.project.entity.Category;
import com.cos.project.entity.CommentEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.repository.BoardLikeRepository;
import com.cos.project.repository.BoardRepository;
import com.cos.project.repository.MemberRepository;
import com.cos.project.service.AlarmService;
import com.cos.project.service.BoardService;
import com.cos.project.service.CommentService;
import com.cos.project.service.MemberService;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

	private BoardService boardService;

	public BoardController(BoardService boardService) {
		this.boardService = boardService;
	}

	@Autowired
	private CommentService commentService;

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

	
	  @GetMapping("/list")
	    public String boardList(@RequestParam(name = "page", defaultValue = "1") Integer page, Model model) {
	        int pageSize = 10; // 페이지당 10개씩 출력
	        
        int pageNumber = page - 1; // 1-based page를 0-based page로 변환
	        
	        Page<BoardEntity> searchresult = boardService.getBoardList(pageNumber, pageSize);

	       
	        
	     //   totalPages는 Page 객체에서 직접 가져올 수 있습니다. Page<BoardEntity> 객체에는 총 페이지 수와 같은 정보가 포함되어 있기 때문입니다.
	        int totalPages = searchresult.getTotalPages();
	        model.addAttribute("allBoards", searchresult);
	        model.addAttribute("totalPages", totalPages);		//전체 페이지
	        model.addAttribute("currentPage", page); // 현재 페이지 번호

	        return "boardlist";
	    }

	
	
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
		
		return "boardwriteform";
	}

	@PostMapping("/writeboard")
	public String writeBoard(@RequestParam(name = "title") String title, @RequestParam(name = "name") String name,
			@RequestParam(name = "price") int price, @RequestParam(name = "userid") String userid,
			@RequestParam(name = "category") String category, @RequestParam(name = "product") String product,
			@RequestParam(name = "buy_Sell") String buy_Sell,
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
				.product(product).buy_Sell(buy_Sell_enum).contents(contents).boardFiles(boardFileJson).build();

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
	public String updateBoard(@PathVariable(name = "id") Long id, @RequestPart(name = "boardData") String boardDataJson,
			@RequestPart(value = "boardFiles", required = false) MultipartFile[] boardFiles,
			RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		BoardDTO boardDTO = objectMapper.readValue(boardDataJson, BoardDTO.class);

		// 기존 파일 목록 처리
		String[] existingFileList = objectMapper.readValue(boardDTO.getBoardFiles(), String[].class);
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
		boardService.updateContents(id, boardDTO, boardFileJson);

		// 리다이렉트 처리
		// redirectAttributes.addAttribute("id", id);
//    return "redirect:/board/view/{id}";
		String id_String = String.valueOf(id);
		Long loggedId = principalDetails.getMemberEntity().getId();
		alarmService.postAlarm(loggedId,null, null, "BOARD", "게시판", id_String, "수정", null);	
		return "/board/view/" + id;
	}


	@DeleteMapping("/deleteboard/{id}")
	public ResponseEntity<?> deleteBoard(@PathVariable(name = "id") Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		commentService.deleteAllCommentAboutBoard(id);
		System.out.println("삭제되는중?" + id);
		String result = boardService.deleteContents(id);
		System.out.println("삭제되는중2?" + id);
		
		String id_String = String.valueOf(id);
		Long loggedId = principalDetails.getMemberEntity().getId();
		alarmService.postAlarm(loggedId,null, null, "BOARD", "게시판", id_String, "삭제", null);	
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

		model.addAttribute("board", boardEntity);
//		model.addAttribute("comments", comments);

//		if(boardFiles[0].equals("/boardimage/nullimage.jpg")) {
//			boardFiles = new String[0];
//		}
		model.addAttribute("boardFiles", boardFiles);

		// return ResponseEntity.status(200).body(result);
		return "boardcontent";
	}



}
