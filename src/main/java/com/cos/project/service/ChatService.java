package com.cos.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.ChattingRoomEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.repository.BoardRepository;
import com.cos.project.repository.ChattingRoomRepository;
import com.cos.project.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

	private final BoardRepository boardRepository;

	private final MemberRepository memberRepository;

	private final ChattingRoomRepository chattingRoomRepository;
//	private finalBoardRepository boardRepository;

	@Transactional			//채팅방 생성;   member1: 채팅방 '최초 생성' 사용자  member2: 채팅방 '수신' 사용자
	public ChattingRoomEntity createRoom(String title, String member1Id, String member2Id, Long boardId, int price) {
		BoardEntity boardEntity = boardRepository.findById(boardId).get();
		MemberEntity member1 = memberRepository.findByUserid(member1Id).get();
		MemberEntity member2 = memberRepository.findByUserid(member2Id)
				.orElseThrow(() -> new IllegalArgumentException("수신자를 조회할 수 없습니다"));

		ChattingRoomEntity chattingRoomEntity = null;
		
		//채팅방 정보 조회 (만약 수신자가 채팅방 생성 사용자일 경우)
		chattingRoomEntity = chattingRoomRepository.findEnableRoom( member2.getId(), member1.getId(),boardId);
		
		if(chattingRoomEntity == null) {
			chattingRoomEntity = chattingRoomRepository.findEnableRoom( member1.getId(), member2.getId(),boardId);
			
		}
		
		
		if(chattingRoomEntity == null) {
		//채팅방이 없는 경우
		chattingRoomEntity = chattingRoomEntity.builder().title(title).member1(member1).member2(member2)
				.boardEntity(boardEntity).price(price).build();

		chattingRoomRepository.save(chattingRoomEntity);
		}
		return chattingRoomEntity;
	}

	@Transactional
	public boolean enterChatRoom(Long member1Id, Long member2Id, Long boardId) {
		boolean flag = false;
		
		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findEnableRoom(member2Id, member1Id, boardId);
		
		if(chattingRoomEntity == null) {
			chattingRoomEntity = chattingRoomRepository.findEnableRoom( member1Id, member2Id,boardId);
			
		}

		if (chattingRoomEntity != null) {
			flag = true;
		}
		return flag;
	}

	@Transactional
	public boolean updateChatRoom(Long chattingRoomid, Long member1Id, Long boardId, String title, int price) {
		BoardEntity boardEntity = boardRepository.findById(boardId).get();
		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(chattingRoomid).get();
		if (boardEntity.getMemberEntity().getId() == chattingRoomid) {
			chattingRoomEntity.setTitle(title);
			chattingRoomEntity.setPrice(price);

			return true;
		}

		return false;
	}

	@Transactional 
	public boolean deleteChatRoom(Long chattingRoomid, Long member1Id, Long member2Id, Long boardId) {

		BoardEntity boardEntity = boardRepository.findById(boardId).get();
		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(chattingRoomid).get();
		if (boardEntity.getMemberEntity().getId() == chattingRoomid) {
			chattingRoomRepository.deleteById(chattingRoomid);
			return true;
		}

		return false;
	}
	
	@Transactional 
	public boolean disableChatRoom(Long chattingRoomid, Long member1Id, Long member2Id, Long boardId) {

		BoardEntity boardEntity = boardRepository.findById(boardId).get();
		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(chattingRoomid).get();
		if (boardEntity.getMemberEntity().getId() == chattingRoomid) {
		//	채팅방 비활성화 기능 추가
			return true;
		}

		return false;
	}


}
