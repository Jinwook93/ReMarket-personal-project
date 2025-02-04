package com.cos.project.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.dto.ChattingRoomDTO;
import com.cos.project.dto.MessageDTO;
import com.cos.project.entity.ChattingRoomEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.entity.MessageEntity;
import com.cos.project.service.BoardService;
import com.cos.project.service.ChatService;
import com.cos.project.service.MemberService;
import com.cos.project.service.MessageService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/chat")
public class ChatController {
    
	@Autowired
	private ChatService chatService;
	
	@Autowired
	private MemberService memberService;
    
	@Autowired
	private BoardService boardService;
	
	


//	    @GetMapping("/Chatroom/{boardId}")
//	    public String chatRoom(@PathVariable(name = "boardId") Long boardId, @RequestParam(name = "userId") String userId , @AuthenticationPrincipal PrincipalDetails principalDetails,Model model) {
//	    	MemberEntity memberEntity1 = principalDetails.getMemberEntity();
//	    	System.out.println("아이디확인"+userId);
//	    	ChattingRoomEntity chattingRoomEntity=chatService.findOrCreateRoom("채팅방 이름", memberEntity1.getUserid(), userId ,boardId, 0);
//	    	model.addAttribute("chatRoom", chattingRoomEntity);
//	     //   model.addAttribute("boardId", boardId);
//	        return "chatroom";
//	    }
	    
    @PostMapping("/Chatroom/{boardId}")
    @ResponseBody
    public ResponseEntity<?> createChatRoom(@PathVariable(name = "boardId") Long boardId, @RequestBody ChattingRoomDTO chattingRoomDTO , @AuthenticationPrincipal PrincipalDetails principalDetails) {
    	MemberEntity loggedUserId = principalDetails.getMemberEntity();
    	System.out.println("아이디확인"+chattingRoomDTO.getMember2UserId());
    	ChattingRoomEntity chattingRoomEntity=chatService.findOrCreateRoom("대화방", loggedUserId.getUserid(), chattingRoomDTO.getMember2UserId() ,boardId, 0);
     //   model.addAttribute("boardId", boardId);
    	return ResponseEntity.ok(chattingRoomEntity);
    }
    
	    
	    
	    
	    
	    @GetMapping("/myChatRoom/{loggedId}")
	    @ResponseBody
	    public ResponseEntity<?> myChatRoom(@PathVariable(name = "loggedId")Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
	    	  System.out.println("컨트롤러진입1?");
	    	//	System.out.println("로그인된아이디:"+id);
	    	List<ChattingRoomDTO> chattingRoomList = new ArrayList<>(chatService.myChattingRoomList(id));
	      System.out.println("컨트롤러진입2?");
	    	return ResponseEntity.ok(chattingRoomList);
	    }


	
	
		@GetMapping("/loadmessages/{roomId}")		// 선택한 채팅창의 메시지 목록을 출력
		@ResponseBody
		public ResponseEntity<?> loadmessages(@PathVariable(name = "roomId") Long roomId, @AuthenticationPrincipal PrincipalDetails principalDetails){
			
			Long loggedId = principalDetails.getMemberEntity().getId();
			if(loggedId != null) {
				List<MessageDTO> messages = chatService.showRoomMessage(roomId);
			    return ResponseEntity.ok(messages);		//Entity로 할 경우, 직렬화의 어려움이 있을 수 있으니 DTO로 하는 것을 
			}
			return null;
			
		}
	
		@PostMapping("/send/{roomId}")
		@ResponseBody
		public ResponseEntity<?> sendMessage(@PathVariable(name = "roomId") Long roomId, @AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody MessageDTO messageDTO){

			Long loggedId = principalDetails.getMemberEntity().getId();
			
			
			boolean flag = chatService.addMessage(roomId,principalDetails ,messageDTO);		//roomId의 id를 조회
			
			
			return ResponseEntity.ok(flag);
			
		}
	
	
	
//	@PostMapping("/createChatRoom")
//	@ResponseBody
//	public ResponseEntity<?> createChatRoom(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody Map<String, String> createChatRoomData
//			,Model model) {
//		
//		
//		
//		/*
//		{
//			"title" : "title",
//			"price" : "price",
//			"member1_userid": "member1_userid",
//			"member2_userid": "member2_userid",
//			"boardId": "boardId"
//			
//		}
//		*/
//		ChattingRoomEntity chattingRoomEntity = null;
//		
//		String title = createChatRoomData.get("title");
//		int price = Integer.parseInt(createChatRoomData.get("price"));
//		String member1_userid = createChatRoomData.get("member1_userid");
//		String member2_userid = createChatRoomData.get("member2_userid");
//		Long boardId =  Long.valueOf(createChatRoomData.get("boardId"));
//		
//		if(principalDetails.getMemberEntity().getId() != null){
//		chattingRoomEntity = 
//		chatService.createRoom(title,member1_userid,member2_userid,boardId,price);
//		}
//		
//		return ResponseEntity.ok(chattingRoomEntity);
//		
//	}
//	
//	@PostMapping("/enterChatRoom")
//	@ResponseBody
//	public ResponseEntity<?> enterChatRoom(@AuthenticationPrincipal PrincipalDetails principalDetails,
//			@RequestBody Map<String, String> enterChatRoomData){
//			
//		/*
//		{
//			"member2_userid": "member2_userid",
//			"boardId": "boardId"
//			
//		}
//		*/
//		
//		
////			Long member2Id,Long boardId){
//		Long member1Id = principalDetails.getMemberEntity().getId();
//		Long member2Id = Long.valueOf(enterChatRoomData.get("member2_userid"));
//		Long boardId =  Long.valueOf(enterChatRoomData.get("boardId"));
//		
//		
//		boolean flag = chatService.enterChatRoom(member1Id, member2Id,boardId);
//		
//		return ResponseEntity.ok(flag);
//		
//	}
	
	@PutMapping("/updateChatRoom/{id}") // board 작성자 (세션 로그인 한 유저)가 수정 가능
	@ResponseBody
	public ResponseEntity<?> updateChatRoom(@PathVariable(name = "id") Long id ,@AuthenticationPrincipal PrincipalDetails principalDetails,
			@RequestBody Map<String, String> updateChatRoomData){
			
			//			Long member2Id,Long boardId){
		
		
		/*
		{
			"member2_userid": "member2_userid",
			"boardId": "boardId",
			"title": "title",
			"price": "price,
		}
		*/
		
		
		Long member1Id = principalDetails.getMemberEntity().getId();
		Long member2Id = Long.valueOf(updateChatRoomData.get("member2_userid"));
		Long boardId =  Long.valueOf(updateChatRoomData.get("boardId"));
		String title = updateChatRoomData.get("title");
		int price =  Integer.parseInt(updateChatRoomData.get("price"));
		
		
		boolean flag = chatService.updateChatRoom(id, member1Id,boardId,title,price);
		
		return ResponseEntity.ok(flag);
		
	}
	
	@DeleteMapping("/deleteChatRoom/{id}")		//결제 완료 시 
	@ResponseBody
	public ResponseEntity<?> enterChatRoom(@PathVariable(name = "id") Long id ,@AuthenticationPrincipal PrincipalDetails principalDetails,
			@RequestBody Map<String, String> deleteChatRoomData){
//			Long member2Id,Long boardId){
		
		
		/*
		{
			"member2_userid": "member2_userid",
			"boardId": "boardId",
		}
		*/
		
		
		Long member1Id = principalDetails.getMemberEntity().getId();
		Long member2Id = Long.valueOf(deleteChatRoomData.get("member2_userid"));
		Long boardId =  Long.valueOf(deleteChatRoomData.get("boardId"));
		
		boolean flag = chatService.deleteChatRoom(id, member1Id, member2Id,boardId);
		
		return ResponseEntity.ok(flag);
		
	}
	
	@PostMapping("/disableChatRoom/{id}")		//board 작성자가 채팅창을 비활성화
	@ResponseBody
	public ResponseEntity<?> disableChatRoom(@PathVariable(name = "id") Long id ,@AuthenticationPrincipal PrincipalDetails principalDetails,
			@RequestBody Map<String, String> disableChatRoomData){
		/*
		{
			"member2_userid": "member2_userid",
			"boardId": "boardId",
		}
		 */			
			//			Long member2Id,Long boardId){
		Long member1Id = principalDetails.getMemberEntity().getId();
		Long member2Id = Long.valueOf(disableChatRoomData.get("member2_userid"));
		Long boardId =  Long.valueOf(disableChatRoomData.get("boardId"));
		
		boolean flag = chatService.disableChatRoom(id,member1Id, member2Id,boardId);
		
		return ResponseEntity.ok(flag);
		
	}
//	
//	@PostMapping("/EndChatRoom")		//결제 완료 시 비활성화
//	@ResponseBody
//	public ResponseEntity<?> enterChatRoom(@AuthenticationPrincipal PrincipalDetails principalDetails,
//			Long member2Id,Long boardId){
//		Long member1Id = principalDetails.getMemberEntity().getId();
//		boolean flag = chatService.enterChatRoom(member1Id, member2Id,boardId);
//		
//		return ResponseEntity.ok(flag);
//		
//	}
//	
	
//	@GetMapping("/findMember2Id")					//상대방 정보 파악
//	@ResponseBody
//	public ResponseEntity<?> myChattingRoomsList(@AuthenticationPrincipal PrincipalDetails principalDetails){
//		List<ChattingRoomEntity> chattingRoomList = chatService.myChattingRoomList(principalDetails.getMemberEntity());
//		return ResponseEntity.ok(chattingRoomList);
//	}
//	
	
	
	
	
}
