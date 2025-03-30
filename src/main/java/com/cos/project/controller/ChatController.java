package com.cos.project.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.cos.project.dto.AlarmDTO;
import com.cos.project.dto.ChattingRoomDTO;
import com.cos.project.dto.MessageDTO;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.ChattingRoomEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.entity.MessageEntity;
import com.cos.project.repository.ChattingRoomRepository;
import com.cos.project.repository.MemberRepository;
import com.cos.project.service.AlarmService;
import com.cos.project.service.BoardService;
import com.cos.project.service.ChatService;
import com.cos.project.service.MemberService;
import com.cos.project.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	@Autowired
	private AlarmService alarmService;


	    
    @PostMapping("/Chatroom/{boardId}")		//채팅방 개설
    @ResponseBody
    public ResponseEntity<?> createChatRoom(@PathVariable(name = "boardId") Long boardId, @RequestBody ChattingRoomDTO chattingRoomDTO , @AuthenticationPrincipal PrincipalDetails principalDetails) {
    	MemberEntity loggedMember = principalDetails.getMemberEntity();
//    	System.out.println("아이디확인"+chattingRoomDTO.getMember2UserId());
    	Long member2Id = memberService.findByUserId(chattingRoomDTO.getMember2UserId()).getId();		//상대방의 Id
    	ChattingRoomDTO responseDTO =chatService.findOrCreateRoom("대화방", loggedMember.getUserid(), chattingRoomDTO.getMember2UserId() ,boardId, 0);
     //   model.addAttribute("boardId", boardId);
    	if(responseDTO != null) {
    		chatService.myChattingRoomList(loggedMember.getId());
    	}
    	if(responseDTO.getExitedmemberId() == null && responseDTO.getRecentExitedmemberId() == null ) {
    	alarmService.postAlarm( loggedMember.getId(),  loggedMember.getId(), member2Id, "MESSAGE", "채팅방", null, "채팅방 만듬", null);
    	}
    	
    	return ResponseEntity.ok(responseDTO);
    }
    
	    
	    
	    
	    
//	    @GetMapping("/myChatRoom/{loggedId}")
//	    @ResponseBody
//	    public ResponseEntity<?> myChatRoom(@PathVariable(name = "loggedId")Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
//	    	List<ChattingRoomDTO> chattingRoomList = new ArrayList<>(chatService.myChattingRoomList(id,principalDetails.getMemberEntity().getId()));
//	    	return ResponseEntity.ok(chattingRoomList);
//	    }

    
    
    @GetMapping("/myChatRoom/{loggedId}")
    @ResponseBody
    public ResponseEntity<?> myChatRoom(
            @PathVariable(name = "loggedId") Long id,
            @AuthenticationPrincipal PrincipalDetails principalDetails, 
            @RequestParam(name = "exceptChecked", required = false) Boolean exceptChecked) {  

//    	System.out.println(exceptChecked);
        if(exceptChecked == null) {
        	exceptChecked= Boolean.FALSE;
        }
    	
    	List<ChattingRoomDTO> chattingRoomList = new ArrayList<>(chatService.myChattingRoomList(principalDetails.getMemberEntity().getId()));

        
//        // exceptChecked가 true일 경우 거래 완료 & 거래 불가인 채팅방을 제외
//        if (Boolean.TRUE.equals(exceptChecked)) {
//            chattingRoomList = chattingRoomList.stream()
//                .filter(room -> {
//                    BoardEntity boardEntity = boardService.findByBoardId(Long.valueOf(room.getBoardId()));
//
//                    // 거래 목록이 없으면 거래 가능하므로 포함
//                    if (boardEntity.getTrades() == null || boardEntity.getTrades().isEmpty()) {
//                        return true;
//                    }
//
//                    // 현재 room을 final 변수로 저장
//                    final ChattingRoomDTO currentRoom = room;
//
//                    return boardEntity.getTrades().stream().noneMatch(trade -> 
//                        trade.getMember1() != null && trade.getMember2() != null &&
//                        trade.getMember1().getUserid() != null && trade.getMember2().getUserid() != null &&
//                        (
//                            (currentRoom.getMember1UserId().equals(trade.getMember1().getUserid()) && 
//                             currentRoom.getMember2UserId().equals(trade.getMember2().getUserid())) ||
//                            (currentRoom.getMember1UserId().equals(trade.getMember2().getUserid()) && 
//                             currentRoom.getMember2UserId().equals(trade.getMember1().getUserid()))
//                        ) 
//                        &&   trade.getTradeStatus() != null // 🔴 NULL CHECK 추가
//                        &&
//                        (
//                            // 1. 거래 상태가 '완료'
//                            "완료".equals(trade.getTradeStatus().name()) ||
//                            // 2. 예약된 거래 (거래 불가)
//                            (trade.getBooking1() && trade.getBooking2()) ||
//                            // 3. 상호 거래 수락된 경우 (거래 불가)
//                            (trade.getAccept1() && trade.getAccept2())
//                        )
//                    
//                    );
//                })
//                .collect(Collectors.toList());
//        }

        return ResponseEntity.ok(chattingRoomList);
    }


    
    
    
    

//    @GetMapping("/myChatRoom/{loggedId}")
//    @ResponseBody
//    public ResponseEntity<?> myChatRoom(@PathVariable(name = "loggedId") Long id,
//                                        @AuthenticationPrincipal PrincipalDetails principalDetails,
//                                        @RequestParam(defaultValue = "0") int page,
//                                        @RequestParam(defaultValue = "5") int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<ChattingRoomDTO> chattingRoomPage = chatService.myChattingRoomList(id, pageable);
//        return ResponseEntity.ok(chattingRoomPage);
//    }

    
    
	
	
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
			Long member2Id =	memberService.findByUserId( messageDTO.getReceiverUserId()).getId();	//상대방 ID
//			String member2Nickname = memberService.findByUserId( messageDTO.getReceiverUserId()).getNickname();
//			messageDTO.setMember2Nickname(member2Nickname);
			
			boolean flag = chatService.addMessage(roomId,principalDetails ,messageDTO);		//roomId의 id를 조회
			if(flag) {
			alarmService.postAlarm(loggedId, loggedId, member2Id, "MESSAGE", "메시지", String.valueOf(roomId), "송수신", null);
			}
			//송수신 알람
			return ResponseEntity.ok(flag);
			
		}
	
	
	
	
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
	
	@GetMapping("/findRoom/{roomId}")		
	@ResponseBody
	public ResponseEntity<?> findChatRoom(@PathVariable(name = "roomId") Long roomId, @AuthenticationPrincipal PrincipalDetails principalDetails
			){
		Long loggedId = principalDetails.getMemberEntity().getId();
		
		Optional<ChattingRoomEntity> room = chatService.findChatRoom(roomId);
		
//		System.out.println("방  유저 1 아이디"+room.get().getMember1().getUserid());
//		System.out.println("방  유저 2 아이디"+room.get().getMember2().getUserid());
//		System.out.println("로그인 아이디"+loggedId);
		
		ChattingRoomDTO responseDTO = null;
		responseDTO =responseDTO.builder()
				.id(room.get().getId())
				.title(room.get().getTitle())
				.member1UserId(room.get().getMember1().getUserid())
				.member2UserId(room.get().getMember2().getUserid())
				.messageIndex1(room.get().getMessageIndex1())
				.messageIndex2(room.get().getMessageIndex2())
				.recentExitedmemberId(room.get().getRecentExitedmemberId())
				.build();
//		System.out.println(responseDTO.toString());
		
		
		return ResponseEntity.ok(responseDTO);
		
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
	
    @GetMapping("/findBoard/{roomId}")			//채팅창과 관련된 게시물 반환
    @ResponseBody
    public ResponseEntity<?> findBoard(@PathVariable(name = "roomId")Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
    	BoardEntity boardEntity = chatService.findBoard(id);
    	
    	
    	return ResponseEntity.ok(boardEntity);
    }

//    @DeleteMapping("/deleteMessage/{messageId}")		//메시지 삭제
    @PutMapping("/deleteMessage/{messageId}")	//삭제가 아닌 수정으로 변경
    @ResponseBody
    public ResponseEntity<?> deleteMessage(@PathVariable(name = "messageId")Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
    	boolean flag = chatService.deleteMessage(id);
    	return ResponseEntity.ok(flag);
    }

    
    
    
    
    @PostMapping("/exitRoom/{deleteRoomId}")		// 채팅방 나가기
    @ResponseBody
    public ResponseEntity<?> deleteRoom(@PathVariable(name = "deleteRoomId")Long roomId, @AuthenticationPrincipal PrincipalDetails principalDetails
    		, @RequestBody Map<String, String> data) {
    	String receiverUserId = data.get("receiver");
//    	System.out.println("리시버 : " + receiverUserId);
    	boolean flag = chatService.deleteRoom(roomId, principalDetails.getMemberEntity().getId(), receiverUserId);
//    	alarmService.postAlarm(loggedUserId.getId(), loggedUserId.getId(), member2Id, "MESSAGE", "채팅방", null, "삭제", null);
    	return ResponseEntity.ok(flag);
    }
    
    
    
    
    
    
    
    @PostMapping("/markAsRead")		//읽기 표시
    @ResponseBody
    public ResponseEntity<?> markMessagesAsRead(@RequestBody Map<String, List<Long>> request) {
        List<Long> messageIds = request.get("messageIds");
        
        if (messageIds == null || messageIds.isEmpty()) {
            return ResponseEntity.badRequest().body("메시지 ID가 없습니다.");
        }

        boolean result = chatService.markMessagesAsRead(messageIds);
        return ResponseEntity.ok(result);
    }

    
    
    @GetMapping("/markAsAllRead")		//채팅방 메시지 모두 읽기
    @ResponseBody
    public ResponseEntity<?> markAllMessagesAsRead(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        
    	Long loggedId = principalDetails.getMemberEntity().getId();
    	List<MessageDTO> unReadMessages= chatService.findUnReadMessages(loggedId);
        boolean result = chatService.setAllUnReadMessages(principalDetails.getMemberEntity());
        return ResponseEntity.ok(result);
    }
    
    
    
    
    
    
    
    
    
    
    @GetMapping("/findMessageCount/{roomId}")			//메시지 수 파악
    @ResponseBody
    public ResponseEntity<?> findMessageCount(@PathVariable(name = "roomId") Long roomId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
       Long messageCount = chatService.findMessagesByRoomId(roomId, principalDetails.getMemberEntity());
        return ResponseEntity.ok(messageCount);
    }
    
    
    
    
    @GetMapping("/chat/findMember1or2/{roomId}")			//메시지 수 파악
    @ResponseBody
    public ResponseEntity<?> findMember1or2(@PathVariable(name = "roomId") Long roomId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
       String result = chatService.findMemberPosition(roomId, principalDetails.getMemberEntity());
 
        return ResponseEntity.ok(result);
    }
    
    
    
//    @GetMapping("/findRecentRoomMessage/{roomId}")			//해당 방의 최근 메시지 조회
//    @ResponseBody
//    public ResponseEntity<?> findRecentRoomMessage(@PathVariable(name = "roomId") Long roomId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
//       MessageDTO result = chatService.recentRoomMessage(roomId);
//       
//       System.out.println("최근 메시지 id"+result.getId());
//       System.out.println("최근 메시지 보낸사람"+result.getSenderUserId());
//       System.out.println("최근 메시지 보낸 날짜"+result.getSendTime());
//       System.out.println("최근 메시지 내용"+result.getMessageContent());
//       
//        return ResponseEntity.ok(result);
//    }
    
    @GetMapping("/findRecentRoomMessage/{roomId}") // 해당 방의 최근 메시지 조회
    @ResponseBody
    public ResponseEntity<?> findRecentRoomMessage(@PathVariable(name = "roomId") Long roomId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MessageDTO result = chatService.recentRoomMessage(roomId,principalDetails.getMemberEntity().getId());
        
        return ResponseEntity.ok(result); // 정상적으로 메시지를 반환
    }

    
    @GetMapping("/getBoardMainFileByRoomId/{roomId}")
	@ResponseBody
	public String getBoardMainFile(@PathVariable(name = "roomId") Long roomId, @AuthenticationPrincipal PrincipalDetails principalDetails)
			throws JsonMappingException, JsonProcessingException {
    		BoardEntity boardEntity = chatService.findBoard(roomId);
    	
			return boardService.getBoardMainFile(boardEntity.getId());
			
	}
    
    
    
    @GetMapping("/unReadMessagesCount/{loggedId}")
	@ResponseBody
	public  ResponseEntity<?>  unReadMessagesCount(@PathVariable(name = "loggedId") Long loggedId, @AuthenticationPrincipal PrincipalDetails principalDetails)
			throws JsonMappingException, JsonProcessingException {
//    	 chatService.myChattingRoomList(loggedId, principalDetails.getMemberEntity().getId());
    	List<MessageDTO> messages = chatService.findUnReadMessages(loggedId);
			return ResponseEntity.ok(messages.size());
			
	}
    
    
    @GetMapping("/unReadMessageCount2/{roomId}")			//채팅방 방안의 읽지 않음 메시지 갯수
	@ResponseBody
	public  ResponseEntity<?>  unReadMessageCount(@PathVariable(name = "roomId") Long roomId, @AuthenticationPrincipal PrincipalDetails principalDetails)
			throws JsonMappingException, JsonProcessingException {
    		Long loggedId = principalDetails.getMemberEntity().getId();
    	List<MessageDTO> messages = chatService.findUnReadMessage(roomId, loggedId);
			return ResponseEntity.ok(messages.size());
			
	}
    
    
    @GetMapping("/findRoomByBoardId/{boardId}")			//채팅방으로 예약된 방 정보 조회
	@ResponseBody
	public  ResponseEntity<?>  findBookingRoomByBoardId(@PathVariable(name = "boardId") Long boardId, @AuthenticationPrincipal PrincipalDetails principalDetails)
			throws JsonMappingException, JsonProcessingException {
    		Long loggedId = principalDetails.getMemberEntity().getId();
    		ChattingRoomDTO chattingRoomDTO = chatService.findBookingRoom(boardId,loggedId);
    		
    		return ResponseEntity.ok(chattingRoomDTO);
			
	}
    
    @PostMapping("/findRoomByBoardIdAndMemberId/{boardId}")			//게시글, body 데이터로 방 정보 조회
	@ResponseBody
	public  ResponseEntity<?>  findBookingRoomByBoardId(@PathVariable(name = "boardId") Long boardId, @AuthenticationPrincipal PrincipalDetails principalDetails,
			@RequestBody AlarmDTO alarmDTO)
			throws JsonMappingException, JsonProcessingException {
    		Long loggedId = principalDetails.getMemberEntity().getId();
    		ChattingRoomDTO chattingRoomDTO = 
    				chatService.findRoomByBoardIdAndMemberId(boardId, alarmDTO.getMember1Id(), alarmDTO.getMember2Id(),loggedId);
    		
    		return ResponseEntity.ok(chattingRoomDTO);
			
	}
    
    













    
    
    
    
}
