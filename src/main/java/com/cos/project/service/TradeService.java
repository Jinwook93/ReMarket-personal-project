package com.cos.project.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cos.project.dto.TradeDTO;
import com.cos.project.entity.AlarmEntity;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.entity.TradeEntity;
import com.cos.project.repository.AlarmRepository;
import com.cos.project.repository.BoardRepository;
import com.cos.project.repository.MemberRepository;
import com.cos.project.repository.TradeRepository;

import jakarta.transaction.Transactional;

@Service
public class TradeService {

	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private TradeRepository tradeRepository;
	
	@Autowired
	private AlarmRepository alarmRepository;
	
	@Transactional
	public TradeDTO createTrade(Long alarmId, TradeDTO tradeDTO) {
		AlarmEntity alarmEntity = alarmRepository.findById(alarmId).get();
		Long member1Id = alarmEntity.getMember1().getId();
		Long member2Id = alarmEntity.getMember2().getId();
		Long boardId = Long.valueOf(alarmEntity.getObject());
//		tradeDTO.setMember1Id(member1Id);
//		tradeDTO.setMember2Id(member2Id);
//		tradeDTO.setBoardEntityId(BoardId);
		MemberEntity member1 = memberRepository.findById(member1Id).orElseGet(null);
		MemberEntity member2 = memberRepository.findById(member2Id).orElseGet(null);
		BoardEntity board= boardRepository.findById(boardId).orElse(null);
		
		TradeEntity tradeEntity = tradeDTO.toEntity(member1, member2, board);		//거래 객체
		tradeRepository.save(tradeEntity);		//DB에 저장
		TradeDTO responseDTO = tradeDTO.fromEntity(tradeEntity);
		return responseDTO;
	}

	
	@Transactional
	public Map<String, Object> searchMember1Member2Board(Long alarmId) {
		Map<String,Object> tradeDTO_map = new HashMap<>();
		
		AlarmEntity alarmEntity = alarmRepository.findById(alarmId).get();
		Long member1Id = alarmEntity.getMember1().getId();
		Long member2Id = alarmEntity.getMember2().getId();
		Long boardId = Long.valueOf(alarmEntity.getObject());
		tradeDTO_map.put("member1Id", member1Id);
		tradeDTO_map.put("member2Id", member2Id);
		tradeDTO_map.put("boardId", boardId);
		return tradeDTO_map;
	}
	
	
}
