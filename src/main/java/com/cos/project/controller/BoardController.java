package com.cos.project.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.CommentEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.repository.BoardRepository;
import com.cos.project.repository.MemberRepository;
import com.cos.project.service.BoardService;
import com.cos.project.service.CommentService;
import com.cos.project.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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






@ModelAttribute("principalDetails")			//전역적으로 처리
public Authentication getPrincipalDetails() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    System.out.println("현재 사용자 : "+ authentication.getName());
    return authentication; // 현재 사용자 이름 반환
}






@Autowired
private MemberService memberService;

@GetMapping("/allboard")
public String allBoard(Model model) {
	List<BoardEntity> searchresult = boardService.allContents();
	model.addAttribute("allBoards", searchresult);
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
public String writeBoard(@RequestParam (name = "title") String title, 
        @RequestParam (name = "name") String name, 
        @RequestParam (name = "userid") String userid, 
        @RequestParam (name = "boardFiles") MultipartFile [] boardFiles,
        @RequestParam (name = "contents") String contents) throws IOException {
	
	
	
	String[] boardFilePath = new String[boardFiles.length];
    ObjectMapper objectMapper = new ObjectMapper();

    int i = 0;
    for (MultipartFile boardFile : boardFiles) {
        String uniqueFileName = UUID.randomUUID().toString() + "_" + boardFile.getOriginalFilename();
        Path filePath = Paths.get("src/main/resources/static/boardimage").resolve(uniqueFileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, boardFile.getBytes());
        boardFilePath[i++] = "/boardimage/" + uniqueFileName;
    }

    // JSON 변환
    String boardFileJson = objectMapper.writeValueAsString(boardFilePath);

    BoardEntity boardEntity = BoardEntity.builder()
            .title(title)
            .contents(contents)
            .boardFile(boardFileJson)
            .build();

    
    
    boardService.writeContents(boardEntity);
	
	
	
	
	
	
	return "redirect:allboard";
}


//@PostMapping("/board/write")
//public String writeBoard(@RequestParam String title, 
//                         @RequestParam Long member_id, 
//                         @RequestParam String contents) {
//    MemberEntity member = memberService.findById(member_id); // 작성자 조회
//    BoardEntity board = BoardEntity.builder()
//                                   .title(title)
//                                   .contents(contents)
//                                   .memberEntity(member)
//                                   .build();
//    boardService.save(board); // 게시글 저장
//    return "redirect:/allboard"; // 작성 후 게시글 목록 페이지로 이동
//}



//@Autowired
//BoardRepository boardRepository;

//@PostMapping("/write")
//public String writeBoard2(@RequestBody BoardEntity boardEntity) {
//	
//
//    // Get the current authenticated user
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//    
//    	boardEntity.setMemberEntity(principalDetails.getMemberEntity());
//
//    boardRepository.save(boardEntity);
//
//    return "게시글 작성 완료!";
//}
//
//@GetMapping("/logininfo")
//public ResponseEntity<?> loginInfo() {
//    // Get the authentication object from the security context
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    
//    // Extract the PrincipalDetails (custom UserDetails implementation) from the Authentication object
//    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//    
//    // Return the MemberEntity associated with the logged-in user
//    return ResponseEntity.status(200).body(principalDetails.getMemberEntity());
//}




//@PutMapping("/updateboard/{id}")
//public ResponseEntity<?> updateBoard(@PathVariable(name = "id") Long id, @RequestBody BoardEntity boardEntity) {
//   String result =  boardService.updateContents(id, boardEntity);
//	
//    return ResponseEntity.status(200).body(result);
//}





//@PutMapping("/updateboard/{id}")
//public String updateBoard(@PathVariable(name = "id") Long id, @RequestBody BoardEntity boardEntity) {
//   String result =  boardService.updateContents(id, boardEntity);
//	
////    return ResponseEntity.status(200).body(result);
//   return "redirect:view/{id}";
//}


@GetMapping("/updateboard/{id}")
//public String updateBoard(@PathVariable(name = "id") Long id, @RequestBody BoardEntity boardEntity, RedirectAttributes redirectAttributes) {
public String goUpdateForm(@PathVariable(name = "id") Long id,  RedirectAttributes redirectAttributes, Model model) {    
	BoardEntity board = boardService.viewContent(id, true);
   model.addAttribute("board", board);

    // Redirect to the view page after the update
    return "updateboardwriteform";
}

@PutMapping("/updateboard/{id}")
public String updateBoard(@PathVariable(name = "id") Long id, @RequestBody BoardEntity boardEntity, RedirectAttributes redirectAttributes) {
    String result = boardService.updateContents(id, boardEntity);
    
    // Adding the id to the redirect URL
    redirectAttributes.addAttribute("id", id); // id 값을 URL 파라미터로 추가

    // Log to check if it's properly passed
    System.out.println("Redirecting to: /board/view/" + id);
    
    // Redirect to the view page after the update
    return "redirect:/board/view/{id}"; // {id}는 addAttribute에서 전달된 id로 치환됩니다.
}






@DeleteMapping("/deleteboard/{id}")
public ResponseEntity<?> putMethodName(@PathVariable(name = "id")  Long id) {
	   String result =  boardService.deleteContents(id);
    return ResponseEntity.status(200).body(result);
}


@GetMapping("/view/{id}")
public String viewContent(@PathVariable(name = "id") Long id, Model model) throws JsonMappingException, JsonProcessingException {
	   BoardEntity boardEntity =  boardService.viewContent(id,false);
	   List<CommentEntity> comments = commentService.getAllCommentAboutBoard(id);
	   
	   ObjectMapper om = new ObjectMapper();
	   String [] boardFiles = om.readValue(boardEntity.getBoardFile(), String[].class);                                 
	   
		model.addAttribute("board", boardEntity);
		model.addAttribute("comments", comments);
		model.addAttribute("boardFiles", boardFiles);
    //return ResponseEntity.status(200).body(result);
		return "boardcontent";
}


	
	
}
