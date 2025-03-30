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
		
		TradeEntity tradeEntity = 
		tradeRepository.findByMember1IdAndMember2IdAndBoardEntityId(member1Id, member2Id, boardId).orElse(null);
		
		
		if(tradeEntity == null) {
			tradeEntity =	tradeRepository.findByMember1IdAndMember2IdAndBoardEntityId(member2Id, member1Id, boardId).orElse(null);
		}
		
		
		
		return tradeEntity;
	}

	
	
	@Transactional
	public TradeEntity findByTradeEntityId(Long id) {
		return tradeRepository.findById(id).orElse(null);
		
	}
	
	
	
	@Transactional
	public void deleteByTradeEntityId(Long id) {
		tradeRepository.deleteById(id);
		
	}
	
	
	@Transactional
	public TradeDTO setCompleted(Long tradeId,Boolean isCompleted1, Boolean isCompleted2) {
		TradeEntity tradeEntity = tradeRepository.findById(tradeId).orElse(null);
	
		
		tradeEntity.setCompleted1(isCompleted1 ==null? false : isCompleted1);
		tradeEntity.setCompleted2(isCompleted2);
		if((isCompleted1 != null &&isCompleted1 == true )&& (isCompleted2 != null &&isCompleted2 == true)) {
			tradeEntity.setTradeStatus(TradeStatus.완료);
		}
		TradeDTO tradeDTO = new TradeDTO();
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
	        .filter(trade -> (trade.getCompleted1().equals(Boolean.TRUE) || trade.getCompleted2().equals(Boolean.TRUE)  // 완료된 거래만 필터링
	        		|| (trade.getBooking1() != null ||  trade.getBooking2() != null)&& (trade.getBooking1() || trade.getBooking2())
	        		|| (trade.getAccept1() != null ||  trade.getAccept2() != null)&& (trade.getAccept1() || trade.getAccept2())
	        		)) 
	        .map(trade -> new TradeDTO().fromEntity(trade)) // TradeDTO 변환
	        .findFirst() // 가장 첫 번째 거래 선택
	        .orElse(null); // 없으면 null 반환
	}

	
	
	//계약 완료가 아닌 거래 검색
	@Transactional
	public TradeDTO findNotTradeByRoomId(Long roomId, Long loggedId) {
	    // 채팅방 조회
	    ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(roomId).orElse(null);
	    if (chattingRoomEntity == null) {
	        return null;
	    }

	    	MemberEntity member1 = chattingRoomEntity.getMember1();
	    	MemberEntity member2 = chattingRoomEntity.getMember2();
	    	
//	    	if(member2.getId().equals(loggedId)) {
//	    		member1 = chattingRoomEntity.getMember2();
//	    		member2 = chattingRoomEntity.getMember1();
//	    	}
	    	
	    
	    // 게시글 조회
	    BoardEntity boardEntity = chattingRoomEntity.getBoardEntity();
	    if (boardEntity == null) {
	        return null;
	    }

	    // 거래 목록 조회 및 필터링 (완료된 거래 찾기)
	    return boardEntity.getTrades().stream()
	    	    .filter(trade -> 
	    	        // 거래가 완료되지 않았고
	    	        (!Boolean.TRUE.equals(trade.getCompleted1()) && !Boolean.TRUE.equals(trade.getCompleted2())) 
	    	        &&
	    	        // member1과 member2가 거래 참여자인 경우 (양방향 체크)
	    	        (
	    	            (trade.getMember1().getId().equals(member1.getId()) && trade.getMember2().getId().equals(member2.getId())) 
	    	            || 
	    	            (trade.getMember1().getId().equals(member2.getId()) && trade.getMember2().getId().equals(member1.getId()))
	    	        )
	    	    )
	    	    .map(trade -> new TradeDTO().fromEntity(trade)) // TradeDTO로 변환
	    	    .findFirst() // 첫 번째 거래 반환
	    	    .orElse(null); // 없으면 null

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

	//알람아이디로 Trade 목록 조회
	@Transactional
	public TradeEntity findByTradeAlarmId(Long alarmId) {
	    // Find the AlarmEntity by its ID
	    AlarmEntity alarmEntity = alarmRepository.findById(alarmId).orElse(null);

	    if (alarmEntity == null) {
	        return null; // If AlarmEntity doesn't exist, return null
	    }

	    Long boardId = Long.valueOf(alarmEntity.getObject());
	    
	    if (boardId == null) {
	        return null; // If boardId is null, return null
	    }
	    
	    // Find the BoardEntity by its ID
	    BoardEntity boardEntity = boardRepository.findById(boardId).orElse(null);

	    if (boardEntity == null) {
	        return null; // If BoardEntity doesn't exist, return null
	    }

	    // Get the trades from the boardEntity
	    List<TradeEntity> trades = boardEntity.getTrades();

	    // Find the trade based on the provided conditions
	    return trades.stream()
	            .filter(tr -> tr != null && (
	                    (tr.getAccept1().equals(Boolean.TRUE) && tr.getAccept2().equals(Boolean.TRUE)) ||
	                    (tr.getBooking1().equals(Boolean.TRUE) && tr.getBooking2().equals(Boolean.TRUE))
	            ))
	            .findFirst()
	            .orElse(null); // Return the first trade that matches, or null if none match
	}

	
	
	
	
	
@Transactional
	public boolean comparingTradeIdAndRoomId(Long trade1Id, Long trade2Id, String room1UserId, String room2UserId) {
		Long room1Id = memberRepository.findByUserid(room1UserId).get().getId();
		Long room2Id = memberRepository.findByUserid(room2UserId).get().getId();
		
			
	
		return ( (room1Id.equals(trade1Id) &&room2Id.equals(trade2Id) ) 
				|| (room1Id.equals(trade2Id) &&room2Id.equals(trade1Id) ) 
				);
	}

	
	
	
}
