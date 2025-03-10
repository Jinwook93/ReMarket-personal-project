package com.cos.project.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.dto.ChattingRoomDTO;
import com.cos.project.dto.MessageDTO;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.ChattingRoomEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.service.BoardService;
import com.cos.project.service.ChatService;
import com.cos.project.service.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

			private final BoardService boardService;
			private final MemberService memberService;
			private final ChatService chatService;
				
		@GetMapping("/board/result")
		public String searchBoard(@RequestParam(name="buy_Sell")String buy_Sell,@RequestParam(name="category1") String category1, @RequestParam(name="category2") String category2,
				@RequestParam(name="search") String search, Model model){
						
				
				
			System.out.print("검색컨트롤러도착"+category1+"  " +category2+ "   "+ search);
				List<BoardEntity> boardEntity = boardService.searchBoard(buy_Sell,category1,category2,search);
			
				
					model.addAttribute("boards", boardEntity);
			
			
//					return ResponseEntity.ok(boardEntity);
				return "searchboardresult";
				
			
		}
	
	
//		@GetMapping("/room/result")
//		@ResponseBody
//		public ResponseEntity<?> searchRooms(@RequestParam(name="search") String searchContent, @AuthenticationPrincipal PrincipalDetails principalDetails){
//						
//				Map<String, Object> roomAndMessage = new HashMap<>();
//			Long loggedId = principalDetails.getMemberEntity().getId();
//				List<ChattingRoomDTO> rooms = chatService.searchRooms(loggedId, searchContent);
//				List<MessageDTO> messages = chatService.searchMessage(loggedId, searchContent);
//				List<String> boardTitles = rooms.stream().map(room -> {
//					return boardService.findByBoardId(Long.valueOf(room.getBoardId())).getTitle();       
//				}).collect(Collectors.toList());
//				
//				roomAndMessage.put("rooms", rooms);
//				roomAndMessage.put("messages", messages);
//				roomAndMessage.put("boards", boardTitles);
//				return ResponseEntity.ok(roomAndMessage);
//				
//			
//		}
		
		@GetMapping("/message/result")
		@ResponseBody
		public ResponseEntity<?> searchMessages(@RequestParam(name="search") String searchContent, @AuthenticationPrincipal PrincipalDetails principalDetails){
						
			Long loggedId = principalDetails.getMemberEntity().getId();
				List<MessageDTO> messages = chatService.searchMessage(loggedId, searchContent);

				System.out.println("🔍 검색어: " + searchContent);
				System.out.println("💬 메시지 목록:");
				for (MessageDTO message : messages) {
				    System.out.println("메시지 소속 방 번호: " + message.getRoomId());
				    System.out.println("메시지 내용: " + message.getMessageContent());
				}

				return ResponseEntity.ok(messages);
				
			
		}
}
