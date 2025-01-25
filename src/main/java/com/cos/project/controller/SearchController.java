package com.cos.project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.service.BoardService;
import com.cos.project.service.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

			private final BoardService boardService;
			private final MemberService memberService;
				
		@GetMapping("/board/result")
		public String searchBoard(@RequestParam(name="category1") String category1, @RequestParam(name="category2") String category2,
				@RequestParam(name="search") String search, Model model){
						
				
				
			System.out.print("검색컨트롤러도착"+category1+"  " +category2+ "   "+ search);
				List<BoardEntity> boardEntity = boardService.searchBoard(category1,category2,search);
			
					model.addAttribute("boards", boardEntity);
			
			
//					return ResponseEntity.ok(boardEntity);
				return "searchboardresult";
				
			
		}
	
	
}
