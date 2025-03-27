package com.cos.project.controller;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.dto.AlarmDTO;
import com.cos.project.dto.ChattingRoomDTO;
import com.cos.project.dto.MessageDTO;
import com.cos.project.dto.TradeDTO;
import com.cos.project.entity.AlarmEntity;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.BoardLikeEntity;
import com.cos.project.entity.ChattingRoomEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.entity.TradeEntity;
import com.cos.project.entity.TradeStatus;
import com.cos.project.repository.BoardLikeRepository;
import com.cos.project.service.AlarmService;
import com.cos.project.service.BoardService;
import com.cos.project.service.ChatService;
import com.cos.project.service.LikeService;
import com.cos.project.service.MemberService;
import com.cos.project.service.MessageService;
import com.cos.project.service.TradeService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/trade")
@RequiredArgsConstructor
public class TradeController {

	private final TradeService tradeService;
	private final AlarmService alarmService;
	private final MemberService memberService;
	private final ChatService chatService;
	private final BoardService boardService;
	private final BoardLikeRepository boardLikeRepository;

	@ResponseBody
	@PostMapping("/checkCreateTrade1/{boardId}")
	public ResponseEntity<?> checkCreateTrade(@PathVariable(name = "boardId") Long boardId,
			@RequestBody TradeDTO tradeDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		Long loggedId = principalDetails.getMemberEntity().getId();
		
		BoardEntity boardEntity = boardService.findByBoardId(boardId);
		List<TradeEntity> boardTrades = boardEntity.getTrades();
		TradeEntity tradeEntity = boardTrades.stream().filter(tr -> tr != null).findFirst()
				  .orElse(null);
		
		if (tradeEntity != null) {
		    String expiredMessage = "만료된 정보입니다";
		    return ResponseEntity.ok(expiredMessage);
		}
		
		
		
		AlarmEntity alarmEntity = alarmService.postAlarm(loggedId, tradeDTO.getMember1Id(), tradeDTO.getMember2Id(),
				"TRADE", "거래", String.valueOf(boardId), "상대방 동의 확인", null);
		AlarmDTO responseDTO = alarmEntity.toDTO();
//		System.out.println(responseDTO.toString());

		// 알람메시지를 채팅방으로도 전송 (상대방에게 responseDTO의 member2Content 전송)
		//// 먼저 채팅방 ID를 조회

		Long roomId = chatService.findRoomId(principalDetails.getMemberEntity(), boardId);

		MessageDTO messageDTO = new MessageDTO();
		
		
		
		
		//messageDTO.setMessageContent(responseDTO.getMember2Content());
		
		
		messageDTO.setMessageContent(responseDTO.getMember2Content() 
			    + "<br><div class=\"messageButtonSelect\"><hr> " 
			    + "<button id=\"agreeMember2-" + responseDTO.getId() + "\">거래하기</button> "
			    + "<button id=\"denyMember2-" + responseDTO.getId() + "\">거절하기</button>"
			    + "</div>");


		
		
		//		messageDTO.setMessageContent(responseDTO.getMember2Content() + "<br><hr> <button id=\"agreeMember2-"
//				+ responseDTO.getId() + "\" onclick=\"enrollTrade2(" + responseDTO.getId() + ")\">\r\n"
//				+ "거래하기</button>" + "<button id=\"denyMember2-" + responseDTO.getId() + "\" onclick=\"denyCreateTrade("
//				+ responseDTO.getId() + ")\">\r\n" + "거절하기</button>");

		BoardEntity boardEntity3 = boardService.findByBoardId(boardId);
		messageDTO.setReceiverUserId(boardEntity3.getMemberEntity().getUserid());
		messageDTO.setAlarmType(true);
		
		ChattingRoomEntity chattingRoomEntity2= chatService.findChatRoom(roomId).orElse(null);
		
	if(chattingRoomEntity2 == null) {
		return null;
	}
	chatService.setMessageExpired(chattingRoomEntity2);	//메시지 내부 버튼 기능 만료
		
		chatService.addMessage(roomId, principalDetails, messageDTO);

		boolean result = true;
		return ResponseEntity.ok(responseDTO);
	}

	@ResponseBody
	@PostMapping("/checkCreateTrade2/{alarmId}") // 상대방이 거래를 승낙할 경우
	public ResponseEntity<?> createTrade2(@PathVariable(name = "alarmId") Long alarmId, @RequestBody TradeDTO tradeDTO,
			@AuthenticationPrincipal PrincipalDetails principalDetails) throws IllegalAccessException {
		Long loggedId = principalDetails.getMemberEntity().getId();
		
		
		TradeEntity tradeEntity = tradeService.findByTradeAlarmId(alarmId);
		if(tradeEntity != null) {
			String expiredMessage = "만료된 정보입니다";
			return ResponseEntity.ok(expiredMessage);
		}
		
		if (tradeDTO.getAccept1() && tradeDTO.getAccept2()) {
				
//			TradeEntity tradeEntity = tradeService.findByTradeAlarmId(alarmId);
//			if(tradeEntity != null) {
//				String expiredMessage = "만료된 정보입니다";
//				return ResponseEntity.ok(expiredMessage);
//			}
			
			
			boolean result = true;
			TradeDTO responseDTO = tradeService.createTrade(alarmId, tradeDTO);
			AlarmEntity alarmEntity = alarmService.postAlarm(loggedId, responseDTO.getMember1Id(),
					responseDTO.getMember2Id(), "TRADE", "거래", String.valueOf(responseDTO.getBoardEntityId()), "거래수락", null);
			AlarmDTO responseAlarmDTO = alarmEntity.toDTO();

			
			//알람메시지를 채팅방으로도 전송 (상대방에게 responseDTO의 member2Content 전송)
			//// 먼저 채팅방 ID를 조회
			MemberEntity member1 = memberService.findById(responseDTO.getMember1Id());
			Long roomId = chatService.findRoomId(member1, responseDTO.getBoardEntityId());
					//보드 관리자 로그인 기준으로 responseDTO 안에 정보가 있으니 member1(먼저 거래신청을 한 유저)을 타겟으로 하였다 
			MessageDTO messageDTO = new MessageDTO();
			messageDTO.setMessageContent(responseAlarmDTO.getMember2Content());
			
			BoardEntity boardEntity = boardService.findByBoardId(responseDTO.getBoardEntityId());
			messageDTO.setReceiverUserId(member1.getUserid());
			messageDTO.setAlarmType(true);
			
			ChattingRoomEntity chattingRoomEntity2= chatService.findChatRoom(roomId).orElse(null);
			
		if(chattingRoomEntity2 == null) {
			return null;
		}
		chatService.setMessageExpired(chattingRoomEntity2);	//메시지 내부 버튼 기능 만료
			
			
			chatService.addMessage(roomId, principalDetails, messageDTO);
			
			
			
			
			
			
			
			return ResponseEntity.ok(responseAlarmDTO);
		} else {
			
			
			Map<String, Object> tradeDTO_map = tradeService.searchMember1Member2Board(alarmId);

			// 메시지가 존재할 시, trade Entity 삭제
			TradeEntity IsExistTradeEntity = tradeService.findTradeEntityByMember1Member2Board(
					(Long) tradeDTO_map.get("member1Id"), (Long) tradeDTO_map.get("member2Id"),
					(Long) tradeDTO_map.get("boardId"));
			if (IsExistTradeEntity != null) {
				tradeService.deleteByTradeEntityId(IsExistTradeEntity.getId());
			}

			AlarmEntity alarmEntity = alarmService.postAlarm(loggedId, (Long) tradeDTO_map.get("member1Id"),
					(Long) tradeDTO_map.get("member2Id"), "TRADE", "거래", String.valueOf(tradeDTO_map.get("boardId")),
					"거래거절", null);
			AlarmDTO responseAlarmDTO = alarmEntity.toDTO();

			
			//알람메시지를 채팅방으로도 전송 (상대방에게 responseDTO의 member2Content 전송)
			//// 먼저 채팅방 ID를 조회
			
			
			MemberEntity member1 = memberService.findById((Long) tradeDTO_map.get("member1Id"));
			
			Long roomId = chatService.findRoomId(member1,(Long) tradeDTO_map.get("boardId"));
			
			MessageDTO messageDTO = new MessageDTO();
			messageDTO.setMessageContent(responseAlarmDTO.getMember2Content());
			
			BoardEntity boardEntity = boardService.findByBoardId((Long) tradeDTO_map.get("boardId"));
			messageDTO.setReceiverUserId(member1.getUserid());
			messageDTO.setAlarmType(true);
			
			
			ChattingRoomEntity chattingRoomEntity2= chatService.findChatRoom(roomId).orElse(null);
			
		if(chattingRoomEntity2 == null) {
			return null;
		}
		chatService.setMessageExpired(chattingRoomEntity2);	//메시지 내부 버튼 기능 만료
		
			chatService.addMessage(roomId, principalDetails, messageDTO);
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			return ResponseEntity.ok(responseAlarmDTO);
		}
	}

	@ResponseBody
	@PostMapping("/completeTrade/{tradeId}") // 거래완료 설정
	public ResponseEntity<?> CompleteTrade(@PathVariable(name = "tradeId") Long tradeId, @RequestBody TradeDTO tradeDTO,
			@AuthenticationPrincipal PrincipalDetails principalDetails) throws IllegalAccessException {
		Long loggedId = principalDetails.getMemberEntity().getId();
		
		
		TradeEntity tradeEntity = tradeService.findByTradeEntityId(tradeId);
		if (tradeEntity == null || (tradeEntity != null && tradeEntity.getTradeStatus() != null && tradeEntity.getTradeStatus().equals(TradeStatus.완료))) {
		    String expiredMessage = "만료된 정보입니다";
		    return ResponseEntity.ok(expiredMessage);
		}

		
		
		
		
		if (tradeDTO.getCompleted1() == null && tradeDTO.getCompleted2()) { // 보드 관리자 쪽에서 먼저 거래완료
			System.out.println("completed1,completed2 확인" + tradeDTO.getCompleted1() + tradeDTO.getCompleted2());
			TradeDTO responseDTO = tradeService.setCompleted(tradeId, tradeDTO.getCompleted1(),
					tradeDTO.getCompleted2());

			AlarmEntity alarmEntity = alarmService.postAlarm(loggedId, responseDTO.getMember1Id(),
					responseDTO.getMember2Id(), "TRADE", "거래", String.valueOf(responseDTO.getBoardEntityId()), "거래 완료 확인", null);
			AlarmDTO responseAlarmDTO = alarmEntity.toDTO();
			System.out.println("여기까지 가나?" + responseAlarmDTO.toString());
			
			
			

			
			
			//알람메시지를 채팅방으로도 전송 (상대방에게 responseDTO의 member2Content 전송)
			//// 먼저 채팅방 ID를 조회
			
			
			MemberEntity member1 = memberService.findById(responseDTO.getMember1Id());
			MemberEntity member2 = memberService.findById(responseDTO.getMember2Id());
			
			Long roomId = chatService.findRoomId(member1, responseDTO.getBoardEntityId());
			
			MessageDTO messageDTO = new MessageDTO();
//			messageDTO.setMessageContent(
//				    responseAlarmDTO.getMember2Content() + 
//				    "<div class=\"messageButtonSelect\">" + 
//				    "<hr> <button  id=\"complete1-Sell-" + responseDTO.getId() + "\">" + 
//				    "거래완료</button></div>"
//				);

			
			
//			messageDTO.setMessageContent(
//				    responseAlarmDTO.getMember2Content()
//				);
			messageDTO.setMessageContent(
					member2.getNickname()+ " 님이 거래완료를 희망합니다. 거래를 마치시겠습니까? <div class='messageButtonSelect'><button id='complete1-Sell-" + responseDTO.getId() + "'>거래완료</button></div>"		
			);
			
			

			
			BoardEntity boardEntity = boardService.findByBoardId(responseDTO.getBoardEntityId());
			messageDTO.setReceiverUserId(member1.getUserid());
			messageDTO.setAlarmType(true);
			
			
			ChattingRoomEntity chattingRoomEntity2= chatService.findChatRoom(roomId).orElse(null);
			
		if(chattingRoomEntity2 == null) {
			return null;
		}
		chatService.setMessageExpired(chattingRoomEntity2);	//메시지 내부 버튼 기능 만료
			chatService.addMessage(roomId, principalDetails, messageDTO);
			
			
			
			
			
			
			
			
			
			
			
			
			return ResponseEntity.ok(responseAlarmDTO);

		} else if (tradeDTO.getCompleted1() && tradeDTO.getCompleted2()) { // 상대 거래자 쪽에서 그다음 거래완료

			TradeDTO responseDTO = tradeService.setCompleted(tradeId, tradeDTO.getCompleted1(),
					tradeDTO.getCompleted2());

			
			
			
			
			AlarmEntity alarmEntity = alarmService.postAlarm(loggedId, responseDTO.getMember1Id(),
					responseDTO.getMember2Id(), "TRADE", "거래", String.valueOf(responseDTO.getBoardEntityId()), "거래완료", null);
			AlarmDTO responseAlarmDTO = alarmEntity.toDTO();
			System.out.println("여기까지 가나?" + responseAlarmDTO.toString());
			
			
			
			//알람메시지를 채팅방으로도 전송 (상대방에게 responseDTO의 member2Content 전송)
			//// 먼저 채팅방 ID를 조회
			
			Long roomId = chatService.findRoomId(principalDetails.getMemberEntity(), 	(Long) responseDTO.getBoardEntityId());
			
			MessageDTO messageDTO = new MessageDTO();  //object : 거래번호
			messageDTO.setMessageContent(responseAlarmDTO.getMember2Content());
			
			ChattingRoomEntity chattingRoomEntity = chatService.findChatRoom(roomId).orElse(null);
			if(chattingRoomEntity == null) {
				return null;
			}
			chatService.setMessageExpired(chattingRoomEntity);	//메시지 내부 버튼 기능 만료
			
			
			BoardEntity boardEntity = boardService.findByBoardId((Long) responseDTO.getBoardEntityId());
			messageDTO.setReceiverUserId(boardEntity.getMemberEntity().getUserid());
			messageDTO.setAlarmType(true);
			chatService.addMessage(roomId, principalDetails, messageDTO);
			
			
			
			
			
			
			return ResponseEntity.ok(responseAlarmDTO);
		} else {
			Map<String, Object> tradeDTO_map = tradeService.searchMember1Member2Board(tradeId);

			// 메시지가 존재할 시, trade Entity 삭제
			TradeEntity IsExistTradeEntity = tradeService.findTradeEntityByMember1Member2Board(
					(Long) tradeDTO_map.get("member1Id"), (Long) tradeDTO_map.get("member2Id"),
					(Long) tradeDTO_map.get("boardId"));
			if (IsExistTradeEntity != null) {
				tradeService.deleteByTradeEntityId(IsExistTradeEntity.getId());
			}

			AlarmEntity alarmEntity = alarmService.postAlarm(loggedId, (Long) tradeDTO_map.get("member1Id"),
					(Long) tradeDTO_map.get("member2Id"), "TRADE", "거래", String.valueOf(tradeDTO_map.get("boardId")),
					"거래거절", null);
			AlarmDTO responseAlarmDTO = alarmEntity.toDTO();

			return ResponseEntity.ok(responseAlarmDTO);
		}
	}

//	@ResponseBody
//	@PostMapping("/denyCreateTrade/{alarmId}")
//	public ResponseEntity<?> denyCreateTrade(@PathVariable(name = "alarmId")Long alarmId,@RequestBody TradeDTO tradeDTO) {
//		
//		if(tradeDTO.getAccept1() && tradeDTO.getAccept2()== false) {
//		
//		boolean result =true; 
////		TradeDTO responseDTO = tradeService.createTrade(tradeDTO);
//		return ResponseEntity.ok(result);
//		}
//		else {
//		return ResponseEntity.ok("상대방이 거래를 희망하지 않습니다");
//		}
//	}
	
	
	@ResponseBody
	@GetMapping("/findTrade/{roomId}")
	public ResponseEntity<?> findTradeByRoomId(@PathVariable(name = "roomId")Long roomId,
			@AuthenticationPrincipal PrincipalDetails principalDetails) {
			Long loggedId = principalDetails.getMemberEntity().getId();
		TradeDTO responseDTO = tradeService.findTradeByRoomId(roomId, loggedId);
//		System.out.println(responseDTO.toString());
		if(responseDTO ==null) {
			responseDTO = new TradeDTO();
		}
		
		return ResponseEntity.ok(responseDTO);
	
	}
	
	
	
	
	
	
	
	
	
	
	//예약 신청 보냄
	
	@ResponseBody
	@PostMapping("/checkBookTrade1/{boardId}")
	public ResponseEntity<?> checkBookTrade(@PathVariable(name = "boardId") Long boardId,
			@RequestBody TradeDTO tradeDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		Long loggedId = principalDetails.getMemberEntity().getId();
		
		BoardEntity boardEntity = boardService.findByBoardId(boardId);
		List<TradeEntity> boardTrades = boardEntity.getTrades();
		TradeEntity tradeEntity = boardTrades.stream().filter(tr -> tr != null).findFirst()
				  .orElse(null);
		
		if (tradeEntity != null) {
		    String expiredMessage = "만료된 정보입니다";
		    return ResponseEntity.ok(expiredMessage);
		}
		
		
//		System.out.println(tradeDTO.toString());
		AlarmEntity alarmEntity = alarmService.postAlarm(loggedId, tradeDTO.getMember1Id(), tradeDTO.getMember2Id(),
				"TRADE", "거래", String.valueOf(boardId), "예약", null);
		AlarmDTO responseDTO = alarmEntity.toDTO();
//		System.out.println(responseDTO.toString());

		// 알람메시지를 채팅방으로도 전송 (상대방에게 responseDTO의 member2Content 전송)
		//// 먼저 채팅방 ID를 조회

		Long roomId = chatService.findRoomId(principalDetails.getMemberEntity(), boardId);

		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setMessageContent(
			    responseDTO.getMember2Content() + 
			    "<br><div class=\"messageButtonSelect\"> <hr>" + 
			    "<button id=\"enroll-Book2-" + responseDTO.getId() + "\">예약하기</button> " + 
			    "<button id=\"deny-enroll-Book2-" + responseDTO.getId() + "\">거절하기</button>" + 
			    "</div>"
			);


		BoardEntity boardEntity2 = boardService.findByBoardId(boardId);
		messageDTO.setReceiverUserId(boardEntity2.getMemberEntity().getUserid());
		messageDTO.setAlarmType(true);
		
		ChattingRoomEntity chattingRoomEntity = chatService.findChatRoom(roomId).orElse(null);
		if(chattingRoomEntity == null) {
			return null;
		}
		chatService.setMessageExpired(chattingRoomEntity);	//메시지 내부 버튼 기능 만료
		
		chatService.addMessage(roomId, principalDetails, messageDTO);

		boolean result = true;
		return ResponseEntity.ok(responseDTO);
	}
	
	
	
	
	@ResponseBody
	@PostMapping("/checkBookTrade2/{alarmId}") // 상대방이 예약을 승낙할 경우
	public ResponseEntity<?> bookTrade(@PathVariable(name = "alarmId") Long alarmId, @RequestBody TradeDTO tradeDTO,
			@AuthenticationPrincipal PrincipalDetails principalDetails) throws IllegalAccessException {
		Long loggedId = principalDetails.getMemberEntity().getId();
		
		
		TradeEntity tradeEntity = tradeService.findByTradeAlarmId(alarmId);
		if(tradeEntity != null) {
			String expiredMessage = "만료된 정보입니다";
			return ResponseEntity.ok(expiredMessage);
		}
		
		
		
		
		if (tradeDTO.getBooking1() && tradeDTO.getBooking2()) {

			boolean result = true;
			TradeDTO responseDTO = tradeService.bookTrade(alarmId, tradeDTO);
			AlarmEntity alarmEntity = alarmService.postAlarm(loggedId, responseDTO.getMember1Id(),
					responseDTO.getMember2Id(), "TRADE", "거래", String.valueOf(responseDTO.getBoardEntityId()), "예약수락", null);
			AlarmDTO responseAlarmDTO = alarmEntity.toDTO();

			
			//알람메시지를 채팅방으로도 전송 (상대방에게 responseDTO의 member2Content 전송)
			//// 먼저 채팅방 ID를 조회
			MemberEntity member1 = memberService.findById(responseDTO.getMember1Id());
			Long roomId = chatService.findRoomId(member1, responseDTO.getBoardEntityId());
					//보드 관리자 로그인 기준으로 responseDTO 안에 정보가 있으니 member1(먼저 거래신청을 한 유저)을 타겟으로 하였다 
			MessageDTO messageDTO = new MessageDTO();
			messageDTO.setMessageContent(responseAlarmDTO.getMember2Content());
			
			BoardEntity boardEntity = boardService.findByBoardId(responseDTO.getBoardEntityId());
			messageDTO.setReceiverUserId(member1.getUserid());
			messageDTO.setAlarmType(true);
			
			
			
			ChattingRoomEntity chattingRoomEntity= chatService.findChatRoom(roomId).orElse(null);
			
			if(chattingRoomEntity == null) {
				return null;
			}
			chatService.setMessageExpired(chattingRoomEntity);	//메시지 내부 버튼 기능 만료
			
			
			chatService.addMessage(roomId, principalDetails, messageDTO);
			return ResponseEntity.ok(responseAlarmDTO);
		} else {
			Map<String, Object> tradeDTO_map = tradeService.searchMember1Member2Board(alarmId);

			// tradeEntity가 존재할 시, trade Entity 삭제
			TradeEntity IsExistTradeEntity = tradeService.findTradeEntityByMember1Member2Board(
					(Long) tradeDTO_map.get("member1Id"), (Long) tradeDTO_map.get("member2Id"),
					(Long) tradeDTO_map.get("boardId"));
			if (IsExistTradeEntity != null) {
				tradeService.deleteByTradeEntityId(IsExistTradeEntity.getId());
			}
			AlarmEntity alarmEntity = alarmService.postAlarm(loggedId, (Long) tradeDTO_map.get("member1Id"),
					(Long) tradeDTO_map.get("member2Id"), "TRADE", "거래", String.valueOf(tradeDTO_map.get("boardId")),
					"예약거절", null);
			AlarmDTO responseAlarmDTO = alarmEntity.toDTO();

			
			//알람메시지를 채팅방으로도 전송 (상대방에게 responseDTO의 member2Content 전송)
			//// 먼저 채팅방 ID를 조회
			
			
			MemberEntity member1 = memberService.findById((Long) tradeDTO_map.get("member1Id"));
			
			Long roomId = chatService.findRoomId(member1,(Long) tradeDTO_map.get("boardId"));
			
			MessageDTO messageDTO = new MessageDTO();
			messageDTO.setMessageContent(responseAlarmDTO.getMember2Content());
			
			BoardEntity boardEntity = boardService.findByBoardId((Long) tradeDTO_map.get("boardId"));
			messageDTO.setReceiverUserId(member1.getUserid());
			messageDTO.setAlarmType(true);
			
			ChattingRoomEntity chattingRoomEntity= chatService.findChatRoom(roomId).orElse(null);
			
			if(chattingRoomEntity == null) {
				return null;
			}
			chatService.setMessageExpired(chattingRoomEntity);	//메시지 내부 버튼 기능 만료
			
			
			
			chatService.addMessage(roomId, principalDetails, messageDTO);
			
			
			 System.out.println(IsExistTradeEntity.toString());
			
			return ResponseEntity.ok(responseAlarmDTO);
		}
	}
	
	
	//보드 게시자가 로그인한 입장 (member2)에서 예약을 풀고 거래중으로 전환할 때 
	
	@ResponseBody
	@PostMapping("/changeBookTrade2/{roomId}") 
	public ResponseEntity<?> changeBookTrade(@PathVariable(name = "roomId") Long roomId, @RequestBody TradeDTO tradeDTO,
			@AuthenticationPrincipal PrincipalDetails principalDetails) throws IllegalAccessException {
		Long loggedId = principalDetails.getMemberEntity().getId();
		
		
		
		TradeDTO tradeDTOisNotNull = tradeService.findNotTradeByRoomId(roomId, loggedId);
		if(tradeDTOisNotNull == null) {
			String expiredMessage = "만료된 정보입니다";
			return ResponseEntity.ok(expiredMessage);
		}
		
		
		
//		if (!tradeDTO.getBooking1() && !tradeDTO.getBooking2() && tradeDTO.getAccept1() && tradeDTO.getAccept2()) {

			TradeDTO responseDTO = tradeService.changeBookEnrollTrade(roomId, loggedId, tradeDTO);
				
			AlarmEntity alarmEntity = alarmService.postAlarm(loggedId, responseDTO.getMember1Id(),
					responseDTO.getMember2Id(), "TRADE", "거래", String.valueOf(responseDTO.getBoardEntityId()), "예약상태변경", null);
			AlarmDTO responseAlarmDTO = alarmEntity.toDTO();
	
			//알람메시지를 채팅방으로도 전송 (상대방에게 responseDTO의 member2Content 전송)
			//// 먼저 채팅방 ID를 조회
			MemberEntity member1 = memberService.findById(responseDTO.getMember1Id());
//			Long roomId = chatService.findRoomId(member1, responseDTO.getBoardEntityId());
			
//			//보드 관리자 로그인 기준으로 responseDTO 안에 정보가 있으니 member1(먼저 거래신청을 한 유저)을 타겟으로 하였다 
			MessageDTO messageDTO = new MessageDTO();
			messageDTO.setMessageContent(responseAlarmDTO.getMember2Content());
			
			BoardEntity boardEntity = boardService.findByBoardId(responseDTO.getBoardEntityId());
			messageDTO.setReceiverUserId(member1.getUserid());
			messageDTO.setAlarmType(true);
			
			
			ChattingRoomEntity chattingRoomEntity= chatService.findChatRoom(roomId).orElse(null);
			
			if(chattingRoomEntity == null) {
				return null;
			}
			chatService.setMessageExpired(chattingRoomEntity);	//메시지 내부 버튼 기능 만료
			
			
			
			
			chatService.addMessage(roomId, principalDetails, messageDTO);
			return ResponseEntity.ok(responseAlarmDTO);
//		} 
//		else {
//			Map<String, Object> tradeDTO_map = tradeService.searchMember1Member2Board(alarmId);
//
//			// tradeEntity가 존재할 시, trade Entity 삭제
//			TradeEntity IsExistTradeEntity = tradeService.findTradeEntityByMember1Member2Board(
//					(Long) tradeDTO_map.get("member1Id"), (Long) tradeDTO_map.get("member2Id"),
//					(Long) tradeDTO_map.get("boardId"));
//			if (IsExistTradeEntity != null) {
//				tradeService.deleteByTradeEntityId(IsExistTradeEntity.getId());
//			}
//
//			AlarmEntity alarmEntity = alarmService.postAlarm(loggedId, (Long) tradeDTO_map.get("member1Id"),
//					(Long) tradeDTO_map.get("member2Id"), "TRADE", "거래", String.valueOf(tradeDTO_map.get("boardId")),
//					"예약거절", null);
//			AlarmDTO responseAlarmDTO = alarmEntity.toDTO();
//
//			
//			//알람메시지를 채팅방으로도 전송 (상대방에게 responseDTO의 member2Content 전송)
//			//// 먼저 채팅방 ID를 조회
//			
//			
//			MemberEntity member1 = memberService.findById((Long) tradeDTO_map.get("member1Id"));
//			
//			Long roomId = chatService.findRoomId(member1,(Long) tradeDTO_map.get("boardId"));
//			
//			MessageDTO messageDTO = new MessageDTO();
//			messageDTO.setMessageContent(responseAlarmDTO.getMember2Content());
//			
//			BoardEntity boardEntity = boardService.findByBoardId((Long) tradeDTO_map.get("boardId"));
//			messageDTO.setReceiverUserId(member1.getUserid());
//			messageDTO.setAlarmType(true);
//			chatService.addMessage(roomId, principalDetails, messageDTO);
//			
//			
//			
//			
//			return ResponseEntity.ok(responseAlarmDTO);
//		}
	}
	
	
	
	
	
	
	
	
	@ResponseBody
	@PostMapping("/cancelTrade/{tradeId}") 
	public ResponseEntity<?> cancelTrade(@PathVariable(name = "tradeId") Long tradeId, @RequestBody TradeDTO tradeDTO,
			@AuthenticationPrincipal PrincipalDetails principalDetails) throws IllegalAccessException {
		
		TradeEntity tradeEntity = tradeService.findByTradeEntityId(tradeId);
		AlarmEntity alarmEntity = null;
		
		if(tradeEntity == null) {
			return null;
		}
		
		TradeDTO responseDTO = new TradeDTO();
			responseDTO =	responseDTO.fromEntity(tradeEntity);
		Long loggedId = principalDetails.getMemberEntity().getId();
		
		
//		if(responseDTO.getMember1Id().equals(loggedId) ) {
//			alarmEntity = alarmService.postAlarm(loggedId, responseDTO.getMember1Id(),
//					responseDTO.getMember2Id(), "TRADE", "거래", String.valueOf(responseDTO.getBoardEntityId()), "거래취소", null);
//		}else if(responseDTO.getMember2Id().equals(loggedId)) {
//			alarmEntity = alarmService.postAlarm(loggedId, responseDTO.getMember2Id(),
//					responseDTO.getMember1Id(), "TRADE", "거래", String.valueOf(responseDTO.getBoardEntityId()), "거래취소", null);
//		}
		
		

			alarmEntity = alarmService.postAlarm(loggedId, responseDTO.getMember1Id(),
					responseDTO.getMember2Id(), "TRADE", "거래", String.valueOf(responseDTO.getBoardEntityId()), "거래취소", null);
		
		
		
		AlarmDTO responseAlarmDTO = alarmEntity.toDTO();
		
		
		
//				TradeEntity tradeEntity = tradeService.findTradeEntityByMember1Member2Board(tradeDTO.getMember1Id(),tradeDTO.getMember2Id(), boardId);
				tradeService.deleteByTradeEntityId(tradeId);
				
				
				
				
				
				
				
				
				
				//채팅방으로도 전송
				
				MemberEntity member1 = memberService.findById(responseDTO.getMember1Id());
				MemberEntity member2 = memberService.findById(responseDTO.getMember2Id());
				
				Long roomId = chatService.findRoomId(member1, responseDTO.getBoardEntityId());
				ChattingRoomEntity chattingRoomEntity= chatService.findChatRoom(roomId).orElse(null);
				
				if(chattingRoomEntity == null) {
					return null;
				}
				chatService.setMessageExpired(chattingRoomEntity);	//메시지 내부 버튼 기능 만료
				
				
				
				MessageDTO messageDTO = new MessageDTO();
				messageDTO.setMessageContent(
					    responseAlarmDTO.getMember2Content()
					);

					ChattingRoomEntity chattingRoomEntity2= chatService.findChatRoom(roomId).orElse(null);
				
				if(chattingRoomEntity2 == null) {
					return null;
				}
				chatService.setMessageExpired(chattingRoomEntity2);	//메시지 내부 버튼 기능 만료
				
				BoardEntity boardEntity = boardService.findByBoardId(responseDTO.getBoardEntityId());
				messageDTO.setReceiverUserId(member1.getUserid());
				messageDTO.setAlarmType(true);
				chatService.addMessage(roomId, principalDetails, messageDTO);
				
				
				
				
				
				
				
				
				
				return ResponseEntity.ok("거래가 취소되었습니다");
	
	
	
	
	}
	
	//BoardId 로 completed2 =true인  trade 찾기 
	@ResponseBody
	@GetMapping("/findCompleted2TradeByBoardId/{boardId}")
	public ResponseEntity<?> findCompleted2TradeByBoardId(@PathVariable(name = "boardId")Long boardId,
			@AuthenticationPrincipal PrincipalDetails principalDetails) {
			Long loggedId = principalDetails.getMemberEntity().getId();
			
			BoardEntity boardEntity = boardService.findByBoardId(boardId);
			List<TradeEntity> trades = boardEntity.getTrades();
		
			TradeDTO tradeDTO = trades.stream()
				    .filter(trade -> Boolean.FALSE.equals(trade.getCompleted1()) && Boolean.TRUE.equals(trade.getCompleted2()))
				    .findFirst()
				    .map(trade -> new TradeDTO().fromEntity(trade))			//   ==    .map(TradeDTO::fromEntity)	      파라미터가 하나일 경우에 :: 사용 가능 . 그 외에는 -> 사용
				    .orElse(new TradeDTO());
			
			
//		TradeDTO responseDTO = tradeService.find
//		System.out.println(tradeDTO.toString());
			
			
			
		return ResponseEntity.ok(tradeDTO);		
	
	}
	
	
	// 나의 거래 목록
//	@GetMapping("/mytrade/{loggedId}")
//	public String myTrade(@PathVariable(name = "loggedId") Long loggedId,
//	                      @AuthenticationPrincipal PrincipalDetails principalDetails,
//	                      Model model) throws IllegalAccessException {
//
//		MemberEntity loggedMember = memberService.findById(loggedId);
//	    List<BoardEntity> boards = boardService.allContents();
//
//	    // Stream으로 필터링
//	    List<TradeEntity> filteredTrades = boards.stream()
//	            .flatMap(board -> board.getTrades().stream())
//	            .filter(trade ->
//	                    (trade.getMember1().getId().equals(loggedId) || trade.getMember2().getId().equals(loggedId)) &&
//	                    (Boolean.TRUE.equals(trade.getAccept1()) || Boolean.TRUE.equals(trade.getAccept2()) ||
//	                     Boolean.TRUE.equals(trade.getBooking1()) || Boolean.TRUE.equals(trade.getBooking2())
//	                    )
//	            )
//	            .collect(Collectors.toList());
//
//	    // DTO 변환 (필요하다면 TradeDTO.fromEntityList 형태로 구현 추천)
//	    List<TradeDTO> filteredDTO = filteredTrades.stream()
//	            .map(TradeDTO::fromEntity)			//   ==   trade -> tradeDTO.fromEntity(trade)      파라미터가 하나일 경우에 :: 사용 가능 . 그 외에는 -> 사용
//	            .collect(Collectors.toList());
//	    model.addAttribute("loggedMember", loggedMember);
//	    model.addAttribute("trades", filteredDTO);
//	    model.addAttribute("boards", boards);
//	    return "/mytradelist";
//	}

	
	
	
	
	@GetMapping("/mytrade/{loggedId}")
	public String myTrade(@PathVariable(name = "loggedId") Long loggedId,
	                      @AuthenticationPrincipal PrincipalDetails principalDetails,
	                      Model model) throws IllegalAccessException {

		MemberEntity loggedMember = memberService.findById(loggedId);
	    List<BoardEntity> boards = boardService.allContents();

	    
	    
	    
	    
	    
//	    .anyMatch() VS .filter() 차이점

//	    .anyMatch(Predicate)	: 조건을 만족하는 요소가 하나라도 있는지 검사	반환 : boolean (true / false)  용도 : 요소 걸러내기 (선별)
//	    .filter(Predicate)	: 조건을 만족하는 요소만 추려냄      반환 : 	Stream <원본타입>   용도 : 조건 충족 여부 검사
	    
	    
	    List<BoardEntity> filteredBoards = boards.stream()
	            .filter(board -> board.getTrades().stream()
	                    .anyMatch(trade ->
	                            (trade.getMember1().getId().equals(loggedId) || trade.getMember2().getId().equals(loggedId)) &&
	                            (Boolean.TRUE.equals(trade.getAccept1()) || Boolean.TRUE.equals(trade.getAccept2()) ||
	                             Boolean.TRUE.equals(trade.getBooking1()) || Boolean.TRUE.equals(trade.getBooking2()))
	                    )
	            )
	            .collect(Collectors.toList());

	    
	    
	    List<TradeDTO> filteredBoardTrades = filteredBoards.stream()
	            .flatMap(board -> board.getTrades().stream()
	                    .map(trade -> new TradeDTO().fromEntity(trade)))
	            .collect(Collectors.toList());
	    
	    
	    

	    for (BoardEntity board : filteredBoards) {
	        board.getTrades().stream()
	            .filter(trade -> trade != null)
	            .forEach(trade -> board.setCreateTime(
	                trade.getCreateTime().equals(trade.getUpdateTime()) ? trade.getCreateTime() : trade.getUpdateTime()
	            ));
	    }
	    
	    // board를 createTime 역순으로 정렬
	    
	    filteredBoards.sort(Comparator.comparing(BoardEntity::getCreateTime).reversed());
	    
	    
	    model.addAttribute("boards", filteredBoards);
	    model.addAttribute("trades", filteredBoardTrades);
	    return "/mytradelist";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	@GetMapping("/myfavorite/{loggedId}")
	public String myFavorite(@PathVariable(name = "loggedId") Long loggedId,
	                      @AuthenticationPrincipal PrincipalDetails principalDetails,
	                      Model model) throws IllegalAccessException {

		MemberEntity loggedMember = memberService.findById(loggedId);
	    List<BoardEntity> boards = boardService.allContents();

	    
	    
	    
	    
	    

	    
	    
	    List<BoardEntity> likedBoards = boards.stream()
	            .filter(board -> {
	                Optional<BoardLikeEntity> boardLikeEntityOptional = boardLikeRepository.findBoardLikeEntity(board.getId(), loggedId);
	                return boardLikeEntityOptional.isPresent() && 
	                       boardLikeEntityOptional.get().isFlag() == true;
	            })
	            .collect(Collectors.toList());



	    List<TradeDTO> filteredBoardTrades = likedBoards.stream()
	            .flatMap(board -> board.getTrades().stream()
	                    .map(trade -> new TradeDTO().fromEntity(trade)))
	            .collect(Collectors.toList());

	    for (BoardEntity board : likedBoards) {
	    	
	    	Optional<BoardLikeEntity> boardLikeEntityOptional = boardLikeRepository.findBoardLikeEntity(board.getId(), loggedId);
	    	
	    	if(boardLikeEntityOptional.isEmpty()) {
	    		continue;
	    	}
	    	
	    	if(!boardLikeEntityOptional.get().isFlag()== true) {
	    		continue;
	    	}
	    	
	    	board.setCreateTime(boardLikeEntityOptional.get().getCreateTime());
	    	
	    }
	    
	    
	    // board를 boardLikeEntity의 createTime 역순으로 정렬
	    
	    likedBoards.sort(Comparator.comparing(BoardEntity::getCreateTime).reversed());
	    
	    
	    
	    model.addAttribute("likedBoards", likedBoards);
	    model.addAttribute("trades", filteredBoardTrades);
	    return "/myfavorite";
	}
	
	
	
	
	
	
	
}