package com.cos.project.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sound.midi.Receiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.dto.ChattingRoomDTO;
import com.cos.project.dto.MessageDTO;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.ChattingRoomEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.entity.MessageEntity;
import com.cos.project.repository.BoardRepository;
import com.cos.project.repository.ChattingRoomRepository;
import com.cos.project.repository.MemberRepository;
import com.cos.project.repository.MessageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

	private final BoardRepository boardRepository;

	private final MemberRepository memberRepository;

	private final ChattingRoomRepository chattingRoomRepository;
//	private finalBoardRepository boardRepository;

	private final MessageRepository messageRepository;
	
	
	
	
	
	@Transactional
	public ChattingRoomEntity findOrCreateRoom(String title, String loggedId, String userId, Long boardId, int price) {
	    BoardEntity boardEntity = boardRepository.findById(boardId)
	        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
	    
	    MemberEntity member1 = memberRepository.findByUserid(loggedId)
	        .orElseThrow(() -> new IllegalArgumentException("송신자를 찾을 수 없습니다."));
	    
	    MemberEntity member2 = memberRepository.findByUserid(userId)
	        .orElseThrow(() -> new IllegalArgumentException("수신자를 찾을 수 없습니다."));

	    // 기존 채팅방이 있는지 확인
	    ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findEnableRoom(member2.getId(), member1.getId(), boardId);
	    if (chattingRoomEntity == null) {
	        chattingRoomEntity = chattingRoomRepository.findEnableRoom(member1.getId(), member2.getId(), boardId);
	    }

	    // 채팅방이 없으면 새로 생성
	    if (chattingRoomEntity == null) {
	        chattingRoomEntity = ChattingRoomEntity.builder()
	            .title(title)
	            .member1(member1)
	            .member2(member2)
	            .boardEntity(boardEntity)
	            .price(price)
	            .build();
	        chattingRoomRepository.save(chattingRoomEntity);
	    }

	    return chattingRoomEntity;
	}

	
	
	
	
	
	
	
//	@Transactional			//채팅방 생성;   member1: 채팅방 '최초 생성' 사용자  member2: 채팅방 '수신' 사용자
//	public ChattingRoomEntity createRoom(String title, String loggedId, String userId, Long boardId, int price) {
//		BoardEntity boardEntity = boardRepository.findById(boardId).get();
//		MemberEntity member1 = memberRepository.findByUserid(loggedId).get();
//		MemberEntity member2 = memberRepository.findByUserid(userId)
//				.orElseThrow(() -> new IllegalArgumentException("수신자를 조회할 수 없습니다"));
//
//		ChattingRoomEntity chattingRoomEntity = null;
//		
//		//채팅방 정보 조회 (만약 수신자가 채팅방 생성 사용자일 경우)
//		chattingRoomEntity = chattingRoomRepository.findEnableRoom( member2.getId(), member1.getId(),boardId);
//		
//		if(chattingRoomEntity == null) {
//			chattingRoomEntity = chattingRoomRepository.findEnableRoom( member1.getId(), member2.getId(),boardId);
//			
//		}
//		
//		
//		if(chattingRoomEntity == null) {
//		//채팅방이 없는 경우
//		chattingRoomEntity = chattingRoomEntity.builder().title(title).member1(member1).member2(member2)
//				.boardEntity(boardEntity).price(price).build();
//
//		chattingRoomRepository.save(chattingRoomEntity);
//		}
//		return chattingRoomEntity;
//	}
//
	@Transactional
	public boolean enterChatRoom(Long loggedId, Long userId, Long boardId) {
		boolean flag = false;
		
		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findEnableRoom(userId, loggedId, boardId);
		
		if(chattingRoomEntity == null) {
			chattingRoomEntity = chattingRoomRepository.findEnableRoom( loggedId, userId,boardId);
			
		}

		if (chattingRoomEntity != null) {
			flag = true;
		}
		return flag;
	}

	@Transactional
	public boolean updateChatRoom(Long chattingRoomid, Long loggedId, Long boardId, String title, int price) {
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
	public boolean deleteChatRoom(Long chattingRoomid, Long loggedId, Long userId, Long boardId) {

		BoardEntity boardEntity = boardRepository.findById(boardId).get();
		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(chattingRoomid).get();
		if (boardEntity.getMemberEntity().getId() == chattingRoomid) {
			chattingRoomRepository.deleteById(chattingRoomid);
			return true;
		}

		return false;
	}
	
	@Transactional 
	public boolean disableChatRoom(Long chattingRoomid, Long loggedId, Long userId, Long boardId) {

		BoardEntity boardEntity = boardRepository.findById(boardId).get();
		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(chattingRoomid).get();
		if (boardEntity.getMemberEntity().getId() == chattingRoomid) {
		//	채팅방 비활성화 기능 추가
			return true;
		}

		return false;
	}

	
	@Transactional
	public List<MessageDTO> showRoomMessage(Long id){
	    ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(id).orElseThrow(() -> new RuntimeException("채팅방이 존재하지 않습니다."));
	    Long chattingRoomEntityid = chattingRoomEntity.getId();
	    List<MessageEntity> messages = messageRepository.findByChattingRoomEntity(chattingRoomEntityid);
	    
	    return messages.stream()
	            .map(message -> new MessageDTO(
	                message.getId(),
	                message.getChattingRoomEntity().getId(),
	                message.getSender().getUserid(),
	                message.getReceiver().getUserid(),
	                message.getMessageContent(),
	                message.getSendTime()
	            ))
	            .collect(Collectors.toList());
	}


	
	
	@Transactional
	public Long findMember2Id(Long id, Long loggedId){
	    Long member2Id = chattingRoomRepository.findMember2Id(id, loggedId);
	    return member2Id;		//상대방아이디
	}
	
	
	
	

	@Transactional
	public boolean addMessage(Long roomId, PrincipalDetails principalDetails, MessageDTO messageDTO) {

	    // Retrieve the chatting room and sender information
	    ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(roomId)
	        .orElseThrow(() -> new RuntimeException("Chat room not found"));
	    MemberEntity sender = principalDetails.getMemberEntity();
	    
	    // Use the existing receiver from the chatting room
	    MemberEntity receiver = chattingRoomEntity.getBoardEntity().getMemberEntity();

	    // Check if the message content is valid
	    if (messageDTO.getMessageContent() == null) {
	        return false;
	    } else {
	        // Create a new MessageEntity and save it to the database
	        MessageEntity messageEntity = MessageEntity.builder()
	            .chattingRoomEntity(chattingRoomEntity)
	            .sender(sender)
	            .receiver(receiver)  // Directly use the managed receiver
	            .messageContent(messageDTO.getMessageContent())
	            .build();

	        messageRepository.save(messageEntity);
	        messageRepository.flush();
	        return true;
	    }
	}


		
//		MessageEntity messageResult = messageRepository.findById(null)
//		
//		return messageResult;
	
		
	
	
	
	@Transactional
	public List<ChattingRoomDTO> myChattingRoomList(Long id) {
	    List<ChattingRoomEntity> chattingRoomEntities = chattingRoomRepository.findAllByLoggedMember(id);

	    return chattingRoomEntities.stream()
	            .map((ChattingRoomEntity entity) -> {  // Explicitly specify the type here
	                System.out.println("Mapping ChattingRoomEntity: " + entity.getTitle());
	                
	                // Log member1 and member2 user IDs
	                String member1UserId = entity.getMember1() != null ? entity.getMember1().getUserid() : null;
	                String member2UserId = entity.getMember2() != null ? entity.getMember2().getUserid() : null;
	                System.out.println("member1UserId: " + member1UserId + ", member2UserId: " + member2UserId);
	                
	                // Log boardId
	                String boardId = entity.getBoardEntity() != null ? String.valueOf(entity.getBoardEntity().getId()) : null;
	                System.out.println("boardId: " + boardId);

	                // Log messages inside the ChattingRoomDTO
	                List<MessageEntity> messageEntities = messageRepository.findByChattingRoomEntity(id);
	                
	                Set<MessageDTO> messageDTOs = messageEntities.stream()
	                        .map((MessageEntity message) -> {
	                            System.out.println("Mapping MessageEntity: " + message.getMessageContent());
	                            return MessageDTO.builder()
	                                    .id(message.getId())
	                                    .roomId(message.getChattingRoomEntity().getId())
	                                    .messageContent(message.getMessageContent())
	                                    .senderUserId(message.getSender().getUserid()) // Corrected this line
	                                    .sendTime(message.getSendTime())
	                                    .build();
	                        })
	                        .collect(Collectors.toSet());

	                System.out.println("Messages mapped: " + messageDTOs);

	                return ChattingRoomDTO.builder()
	                        .id(entity.getId())
	                        .title(entity.getTitle())
	                        .price(entity.getPrice())
	                        .createTime(entity.getCreateTime())
	                        .liked(entity.isLiked())
	                        .member1UserId(member1UserId)
	                        .member2UserId(member2UserId)
	                        .boardId(boardId)
	                        .messages(messageDTOs)
	                        .build();
	            })
	            .collect(Collectors.toList());
	}





	
	
}
