//package com.cos.project.controller;
//
//import org.springframework.web.bind.annotation.RestController;
//
//import com.cos.project.details.PrincipalDetails;
//import com.cos.project.entity.BoardEntity;
//import com.cos.project.entity.MemberEntity;
//import com.cos.project.repository.BoardRepository;
//import com.cos.project.repository.MemberRepository;
//import com.cos.project.service.BoardService;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//
//
//
//@RestController
//@RequestMapping("/board")
//public class BoardApiController {
//
//	
//	
//	private BoardService boardService;
//	
//	
//	
//@Autowired
//public BoardApiController(BoardService boardService) {
//	this.boardService = boardService;
//	}
//
//@GetMapping("/allboard")
//public List<BoardEntity> allBoard() {
//	List<BoardEntity> searchresult = boardService.allContents();
//    return "boardList";
//}
//
//@GetMapping("/searchboard")
//public List<BoardEntity> searchBoard(@RequestParam String searchindex) {
//	List<BoardEntity> searchresult = boardService.searchContents(searchindex);
//	
//	
//    return searchresult;
//}
//
//
//
//@PostMapping("/writeboard")
//public ResponseEntity<?> writeBoard(@RequestBody BoardEntity boardEntity) {
//
//    boardService.writeContents(boardEntity);
//	return ResponseEntity.status(200).body("게시글 저장이 완료되었습니다");
//}
//
////@Autowired
////BoardRepository boardRepository;
//
////@PostMapping("/write")
////public String writeBoard2(@RequestBody BoardEntity boardEntity) {
////	
////
////    // Get the current authenticated user
////    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
////    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
////    
////    	boardEntity.setMemberEntity(principalDetails.getMemberEntity());
////
////    boardRepository.save(boardEntity);
////
////    return "게시글 작성 완료!";
////}
////
////@GetMapping("/logininfo")
////public ResponseEntity<?> loginInfo() {
////    // Get the authentication object from the security context
////    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
////    
////    // Extract the PrincipalDetails (custom UserDetails implementation) from the Authentication object
////    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
////    
////    // Return the MemberEntity associated with the logged-in user
////    return ResponseEntity.status(200).body(principalDetails.getMemberEntity());
////}
//
//
//
//
//@PutMapping("/updateboard/{id}")
//public ResponseEntity<?> updateBoard(@PathVariable(name = "id") Long id, @RequestBody BoardEntity boardEntity) {
//   String result =  boardService.updateContents(id, boardEntity);
//	
//    return ResponseEntity.status(200).body(result);
//}
//
//@DeleteMapping("/deleteboard/{id}")
//public ResponseEntity<?> putMethodName(@PathVariable(name = "id")  Long id) {
//	   String result =  boardService.deleteContents(id);
//    return ResponseEntity.status(200).body(result);
//}
//
//
//
//	
//	
//}
