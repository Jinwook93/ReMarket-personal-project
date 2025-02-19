package com.cos.project.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
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
import com.cos.project.entity.MemberEntity;
import com.cos.project.entity.TradeEntity;
import com.cos.project.service.AlarmService;
import com.cos.project.service.BoardService;
import com.cos.project.service.ChatService;
import com.cos.project.service.MemberService;
import com.cos.project.service.MessageService;
import com.cos.project.service.TradeService;

import lombok.RequiredArgsConstructor;

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

	@ResponseBody
	@PostMapping("/checkCreateTrade1/{boardId}")
	public ResponseEntity<?> checkCreateTrade(@PathVariable(name = "boardId") Long boardId,
			@RequestBody TradeDTO tradeDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		Long loggedId = principalDetails.getMemberEntity().getId();
		AlarmEntity alarmEntity = alarmService.postAlarm(loggedId, tradeDTO.getMember1Id(), tradeDTO.getMember2Id(),
				"TRADE", "거래", String.valueOf(boardId), "상대방 동의 확인", null);
		AlarmDTO responseDTO = alarmEntity.toDTO();
		System.out.println(responseDTO.toString());

		// 알람메시지를 채팅방으로도 전송 (상대방에게 responseDTO의 member2Content 전송)
		//// 먼저 채팅방 ID를 조회

		Long roomId = chatService.findRoomId(principalDetails.getMemberEntity(), boardId);

		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setMessageContent(responseDTO.getMember2Content() + "<br><hr> <button id=\"agreeMember2-"
				+ responseDTO.getId() + "\" onclick=\"enrollTrade2(" + responseDTO.getId() + ")\">\r\n"
				+ "거래하기</button>" + "<button id=\"denyMember2-" + responseDTO.getId() + "\" onclick=\"denyCreateTrade("
				+ responseDTO.getId() + ")\">\r\n" + "거절하기</button>");

		BoardEntity boardEntity = boardService.findByBoardId(boardId);
		messageDTO.setReceiverUserId(boardEntity.getMemberEntity().getUserid());
		messageDTO.setAlarmType(true);
		chatService.addMessage(roomId, principalDetails, messageDTO);

		boolean result = true;
		return ResponseEntity.ok(responseDTO);
	}

	@ResponseBody
	@PostMapping("/checkCreateTrade2/{alarmId}") // 상대방이 거래를 승낙할 경우
	public ResponseEntity<?> createTrade2(@PathVariable(name = "alarmId") Long alarmId, @RequestBody TradeDTO tradeDTO,
			@AuthenticationPrincipal PrincipalDetails principalDetails) throws IllegalAccessException {
		Long loggedId = principalDetails.getMemberEntity().getId();
		if (tradeDTO.getAccept1() && tradeDTO.getAccept2()) {

			boolean result = true;
			TradeDTO responseDTO = tradeService.createTrade(alarmId, tradeDTO);
			AlarmEntity alarmEntity = alarmService.postAlarm(loggedId, responseDTO.getMember1Id(),
					responseDTO.getMember2Id(), "TRADE", "거래", String.valueOf(responseDTO.getId()), "거래수락", null);
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
			chatService.addMessage(roomId, principalDetails, messageDTO);
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			return ResponseEntity.ok(responseAlarmDTO);
		}
	}

	@ResponseBody
	@PostMapping("/completeTrade/{tradeId}") // 거래완료 설정
	public ResponseEntity<?> CompleteTrade(@PathVariable(name = "tradeId") Long tradeId, @RequestBody TradeDTO tradeDTO,
			@AuthenticationPrincipal PrincipalDetails principalDetails) throws IllegalAccessException {
		Long loggedId = principalDetails.getMemberEntity().getId();
		if (tradeDTO.getCompleted1() == null && tradeDTO.getCompleted2()) { // 보드 관리자 쪽에서 먼저 거래완료
			System.out.println("completed1,completed2 확인" + tradeDTO.getCompleted1() + tradeDTO.getCompleted2());
			TradeDTO responseDTO = tradeService.setCompleted(tradeId, tradeDTO.getCompleted1(),
					tradeDTO.getCompleted2());

			AlarmEntity alarmEntity = alarmService.postAlarm(loggedId, responseDTO.getMember1Id(),
					responseDTO.getMember2Id(), "TRADE", "거래", String.valueOf(responseDTO.getId()), "거래 완료 확인", null);
			AlarmDTO responseAlarmDTO = alarmEntity.toDTO();
			System.out.println("여기까지 가나?" + responseAlarmDTO.toString());
			
			
			
			//알람메시지를 채팅방으로도 전송 (상대방에게 responseDTO의 member2Content 전송)
			//// 먼저 채팅방 ID를 조회
			
//			Long roomId = chatService.findRoomId(principalDetails.getMemberEntity(), 	(Long) responseDTO.getBoardEntityId());
//			
//			MessageDTO messageDTO = new MessageDTO();  //object : 거래번호
//			messageDTO.setMessageContent(responseAlarmDTO.getMember2Content()		
//			+"  <button id = \"complete1-Sell-"+responseDTO.getId()+" onclick=\"enrollTrade2(${alarm.id})\">\r\n"
//			+ "거래완료</button>"		
//					);
//			
//			BoardEntity boardEntity = boardService.findByBoardId((Long) responseDTO.getBoardEntityId());
//			messageDTO.setReceiverUserId(boardEntity.getMemberEntity().getUserid());
//			messageDTO.setAlarmType(true);
//			chatService.addMessage(roomId, principalDetails, messageDTO);
			
			
			
			
			
			
			
			
			
			
			
			//알람메시지를 채팅방으로도 전송 (상대방에게 responseDTO의 member2Content 전송)
			//// 먼저 채팅방 ID를 조회
			
			
			MemberEntity member1 = memberService.findById(responseDTO.getMember1Id());
			
			Long roomId = chatService.findRoomId(member1, responseDTO.getBoardEntityId());
			
			MessageDTO messageDTO = new MessageDTO();
			messageDTO.setMessageContent(responseAlarmDTO.getMember2Content() + 
				    "<button id=\"complete1-Sell-" + responseDTO.getId() + "\" " + 
				    "onclick=\"enrollTrade2(" + responseDTO.getId() + ")\">" +
				    "거래완료</button>");

			
			BoardEntity boardEntity = boardService.findByBoardId(responseDTO.getBoardEntityId());
			messageDTO.setReceiverUserId(member1.getUserid());
			messageDTO.setAlarmType(true);
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

}
