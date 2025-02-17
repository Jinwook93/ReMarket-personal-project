package com.cos.project.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cos.project.dto.TradeDTO;
import com.cos.project.entity.AlarmEntity;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.entity.TradeEntity;
import com.cos.project.entity.TradeStatus;
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

	
	@Transactional			//alarm에서 member1Id, member2Id, boardId 정보 조회
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
	
	@Transactional
	public TradeEntity findTradeEntityByMember1Member2Board(Long member1Id, Long member2Id, Long boardId) {
		return tradeRepository.findByMember1IdAndMember2IdAndBoardEntityId(member1Id, member2Id, boardId).orElse(null);
	}

	@Transactional
	public void deleteByTradeEntityId(Long id) {
		tradeRepository.deleteById(id);
		
	}
	
	
	@Transactional
	public TradeDTO setCompleted(Long tradeId,Boolean isCompleted1, Boolean isCompleted2) {
		TradeEntity tradeEntity = tradeRepository.findById(tradeId).orElse(null);
	
		tradeEntity.setCompleted1(isCompleted1);
		tradeEntity.setCompleted2(isCompleted2);
		if((isCompleted1 != null &&isCompleted1 == true )&& (isCompleted2 != null &&isCompleted2 == true)) {
			tradeEntity.setTradeStatus(TradeStatus.완료);
		}
//		if (Boolean.TRUE.equals(isCompleted1) && Boolean.TRUE.equals(isCompleted2)) {
//		    tradeEntity.setTradeStatus(TradeStatus.완료);
//		}
		TradeDTO tradeDTO = new TradeDTO();
		System.out.println("다 되었나"+ isCompleted1+ isCompleted2);
	TradeDTO responseDTO = 	tradeDTO.fromEntity(tradeEntity);
			return responseDTO;
	}
	
}
