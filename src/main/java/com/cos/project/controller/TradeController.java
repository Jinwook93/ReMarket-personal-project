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
import com.cos.project.dto.TradeDTO;
import com.cos.project.entity.AlarmEntity;
import com.cos.project.entity.TradeEntity;
import com.cos.project.service.AlarmService;
import com.cos.project.service.MemberService;
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
	
	@ResponseBody
	@PostMapping("/checkCreateTrade1/{boardId}")
	public ResponseEntity<?> checkCreateTrade(@PathVariable(name = "boardId")Long boardId ,@RequestBody TradeDTO tradeDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		Long loggedId = principalDetails.getMemberEntity().getId();
		AlarmEntity alarmEntity = alarmService.postAlarm(loggedId, tradeDTO.getMember1Id(),tradeDTO.getMember2Id() , "TRADE", "거래",String.valueOf(boardId), "상대방 동의 확인", null);
		AlarmDTO responseDTO = alarmEntity.toDTO();
	System.out.println(responseDTO.toString());
		boolean result= true;
		return ResponseEntity.ok(responseDTO);
	}
	
	
	
	@ResponseBody				
	@PostMapping("/checkCreateTrade2/{alarmId}")							//상대방이 거래를 승낙할 경우
	public ResponseEntity<?> createTrade2(@PathVariable(name = "alarmId")Long alarmId,@RequestBody TradeDTO tradeDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		Long loggedId = principalDetails.getMemberEntity().getId();
		if(tradeDTO.getAccept1() && tradeDTO.getAccept2()) {
		
		boolean result =true; 
		TradeDTO responseDTO = tradeService.createTrade(alarmId,tradeDTO);
//		System.out.println(responseDTO.toString());
//		return ResponseEntity.ok(responseDTO);
		AlarmEntity alarmEntity = alarmService.postAlarm(loggedId, responseDTO.getMember1Id(),responseDTO.getMember2Id() , "TRADE", "거래",String.valueOf(responseDTO.getId()), "거래수락", null);
		AlarmDTO responseAlarmDTO = alarmEntity.toDTO();
		
		return ResponseEntity.ok(responseAlarmDTO);
		}
		else {
			System.out.println("왓냐");
			Map<String, Object> tradeDTO_map = tradeService.searchMember1Member2Board(alarmId);
	   AlarmEntity alarmEntity = alarmService.postAlarm(loggedId, (Long)tradeDTO_map.get("member1Id"),(Long)tradeDTO_map.get("member2Id") , "TRADE", "거래",String.valueOf(tradeDTO_map.get("boardId")), "거래거절", null);
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
