package com.cos.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cos.project.dto.TradeDTO;
import com.cos.project.entity.AlarmEntity;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.ChattingRoomEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.entity.TradeEntity;
import com.cos.project.entity.TradeStatus;
import com.cos.project.repository.AlarmRepository;
import com.cos.project.repository.BoardRepository;
import com.cos.project.repository.ChattingRoomRepository;
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
	
	@Autowired
	private ChattingRoomRepository chattingRoomRepository;
	
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

	@Transactional
	public TradeDTO findTradeByRoomId(Long roomId, Long loggedId) {
	    // 채팅방 조회
	    ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(roomId).orElse(null);
	    if (chattingRoomEntity == null) {
	        return null;
	    }

	    // 게시글 조회
	    BoardEntity boardEntity = chattingRoomEntity.getBoardEntity();
	    if (boardEntity == null) {
	        return null;
	    }

	    // 거래 목록 조회 및 필터링 (완료된 거래 찾기)
	    return boardEntity.getTrades().stream()
	        .filter(trade -> (trade.getCompleted1().equals(Boolean.TRUE) && trade.getCompleted2().equals(Boolean.TRUE)  // 완료된 거래만 필터링
	        		|| (trade.getBooking1() || trade.getBooking2())
	        		|| (trade.getAccept1() || trade.getAccept2())
	        		)) 
	        .map(trade -> new TradeDTO().fromEntity(trade)) // TradeDTO 변환
	        .findFirst() // 가장 첫 번째 거래 선택
	        .orElse(null); // 없으면 null 반환
	}

	
	//게시글이 미예약 상태인지 확인
	
	@Transactional
	public TradeDTO findNotBookingByRoomId(Long roomId, Long loggedId) {
	    // 채팅방 조회
	    ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(roomId).orElse(null);
	    if (chattingRoomEntity == null) {
	        return null;
	    }

	    // 게시글 조회
	    BoardEntity boardEntity = chattingRoomEntity.getBoardEntity();
	    if (boardEntity == null) {
	        return null;
	    }

	    // 거래 목록 조회 및 필터링 (완료된 거래 찾기)
	    return boardEntity.getTrades().stream()
	        .filter(trade -> (!trade.getBooking1() || !trade.getBooking2())
	        		&&  (!trade.getAccept1() && !trade.getAccept2())) // 미거래만 필터링
	        .map(trade -> new TradeDTO().fromEntity(trade)) // TradeDTO 변환
	        .findFirst() // 가장 첫 번째 거래 선택
	        .orElse(null); // 없으면 null 반환
	}
	
	//게시글이 예약 상태인지 확인
	
	@Transactional
	public TradeDTO findBookingByRoomId(Long roomId, Long loggedId) {
	    // 채팅방 조회
	    ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(roomId).orElse(null);
	    if (chattingRoomEntity == null) {
	        return null;
	    }

	    // 게시글 조회
	    BoardEntity boardEntity = chattingRoomEntity.getBoardEntity();
	    if (boardEntity == null) {
	        return null;
	    }

	    // 거래 목록 조회 및 필터링 (완료된 거래 찾기)
	    return boardEntity.getTrades().stream()
	        .filter(trade -> trade.getBooking1() && trade.getBooking2()) // 예약된 거래만 필터링
	        .map(trade -> new TradeDTO().fromEntity(trade)) // TradeDTO 변환
	        .findFirst() // 가장 첫 번째 거래 선택
	        .orElse(null); // 없으면 null 반환
	}
	
	@Transactional
	public TradeDTO bookTrade(Long alarmId, TradeDTO tradeDTO) {
		AlarmEntity alarmEntity = alarmRepository.findById(alarmId).get();
		Long member1Id = alarmEntity.getMember1().getId();
		Long member2Id = alarmEntity.getMember2().getId();
		Long boardId = Long.valueOf(alarmEntity.getObject());
		MemberEntity member1 = memberRepository.findById(member1Id).orElseGet(null);
		MemberEntity member2 = memberRepository.findById(member2Id).orElseGet(null);
		BoardEntity board= boardRepository.findById(boardId).orElse(null);
		
		TradeEntity tradeEntity = tradeDTO.toEntity(member1, member2, board);		//거래 객체
		
		tradeRepository.save(tradeEntity);		//DB에 저장
		TradeDTO responseDTO = tradeDTO.fromEntity(tradeEntity);
		return responseDTO;
	}

	@Transactional
	public TradeDTO changeBookEnrollTrade(Long roomId, Long loggedId, TradeDTO tradeDTO) {
	    // 채팅방 조회
	    ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(roomId).orElse(null);
	    if (chattingRoomEntity == null) {
	        return null;
	    }

	    // 게시글 조회
	    BoardEntity boardEntity = chattingRoomEntity.getBoardEntity();
	    if (boardEntity == null) {
	        return null;
	    }
	    
	    TradeDTO filteredDTO = this.findBookingByRoomId(roomId,  loggedId); 	//booking1, booking2가 활성화된 tradeDTO 검색
	
	    TradeEntity tradeEntity = tradeRepository.findById(filteredDTO.getId()).orElse(null);
	
	    if(tradeEntity != null) {
	    tradeEntity.setBooking1(tradeDTO.getBooking1());
	    tradeEntity.setBooking2(tradeDTO.getBooking2());
	    tradeEntity.setAccept1(tradeDTO.getAccept1());
	    tradeEntity.setAccept2(tradeDTO.getAccept2());
	    }
	    filteredDTO =  filteredDTO.fromEntity(tradeEntity);
	    
	    return filteredDTO;
	}
	
}
