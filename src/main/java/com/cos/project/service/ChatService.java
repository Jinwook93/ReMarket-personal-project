package com.cos.project.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
	public ChattingRoomDTO findOrCreateRoom(String title, String loggedId, String userId, Long boardId, int price) {
	    BoardEntity boardEntity = boardRepository.findById(boardId)
	        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

	    MemberEntity member1 = memberRepository.findByUserid(loggedId)
	        .orElseThrow(() -> new IllegalArgumentException("송신자를 찾을 수 없습니다."));

	    MemberEntity member2 = memberRepository.findByUserid(userId)
	        .orElseThrow(() -> new IllegalArgumentException("수신자를 찾을 수 없습니다."));

	    // 기존 채팅방이 있는지 확인
	    ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findEnableRoom(member1.getId(), member2.getId(), boardId);
	    
	    if (chattingRoomEntity == null) {
	        chattingRoomEntity = chattingRoomRepository.findEnableRoom(member2.getId(), member1.getId(), boardId);
	    }

	    // ⚠️ chattingRoomEntity가 null인지 먼저 체크 후 getExitedmemberId() 호출해야 함
	    if (chattingRoomEntity != null) {
	        if (chattingRoomEntity.getExitedmemberId() != null && chattingRoomEntity.getExitedmemberId().equals(member2.getId())) {
	            chattingRoomEntity.setExitedmemberId(null);
	            resetExitedMessages(chattingRoomEntity);
	        } else if (chattingRoomEntity.getExitedmemberId() != null && chattingRoomEntity.getExitedmemberId().equals(member1.getId())) {
	            chattingRoomEntity.setExitedmemberId(null);
	            resetExitedMessages(chattingRoomEntity);
	        }
	    }

	    // 채팅방이 없으면 새로 생성
	    if (chattingRoomEntity == null) {
	        chattingRoomEntity = ChattingRoomEntity.builder()
	            .title(boardEntity.getTitle())
	            .member1(member1)
	            .member2(member2)
	            .boardEntity(boardEntity)
	            .price(price)
	            .build();
	        chattingRoomRepository.save(chattingRoomEntity);
	    }

	   ChattingRoomDTO responseDTO = ChattingRoomDTO.builder()
	            .id(chattingRoomEntity.getId())
	            .title(chattingRoomEntity.getTitle())
	            .price(chattingRoomEntity.getPrice())
	            .member1UserId(chattingRoomEntity.getMember1().getUserid())
	            .member2UserId(chattingRoomEntity.getMember2().getUserid())
	            .boardId(String.valueOf(chattingRoomEntity.getBoardEntity().getId()))
	            .build();
	   
	   return responseDTO;
	}
	
	@Transactional
	private void resetExitedMessages(ChattingRoomEntity chattingRoomEntity) {
	    List<MessageEntity> messages = messageRepository.findByChattingRoomEntity(chattingRoomEntity.getId());
	    for (MessageEntity message : messages) {
	        message.setExited(false);
	    }
	    messageRepository.saveAll(messages);
	}


	@Transactional
	public boolean enterChatRoom(Long loggedId, Long userId, Long boardId) {
		boolean flag = false;

		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findEnableRoom(userId, loggedId, boardId);

		if (chattingRoomEntity == null) {
			chattingRoomEntity = chattingRoomRepository.findEnableRoom(loggedId, userId, boardId);

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
			// 채팅방 비활성화 기능 추가
			return true;
		}

		return false;
	}

	@Transactional
	public List<MessageDTO> showRoomMessage(Long id) {
		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("채팅방이 존재하지 않습니다."));
		Long chattingRoomEntityid = chattingRoomEntity.getId();
		List<MessageEntity> messages = messageRepository.findByChattingRoomEntity(chattingRoomEntityid);

		return messages.stream()
				.map(message -> new MessageDTO(message.getId(), message.getChattingRoomEntity().getId(),
						message.getSender().getUserid(), message.getReceiver().getUserid(), message.getMessageContent(),
						message.isLiked(), message.isRead(), message.isExited(), message.getExitedSenderId(),
						message.getSendTime()))
				.collect(Collectors.toList());
	}

	
	@Transactional
	public Long findMember2Id(Long id, Long loggedId) {
		Long member2Id = chattingRoomRepository.findMember2Id(id, loggedId);
		return member2Id; // 상대방아이디
	}

	@Transactional
	public boolean addMessage(Long roomId, PrincipalDetails principalDetails, MessageDTO messageDTO) {

		String receiverUserId = messageDTO.getReceiverUserId();
		System.out.println("리시버아이디 테스트" + receiverUserId);

		// Retrieve the chatting room and sender information
		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(roomId)
				.orElseThrow(() -> new RuntimeException("Chat room not found"));
		MemberEntity sender = principalDetails.getMemberEntity();

		// Use the existing receiver from the chatting room
		MemberEntity receiver = memberRepository.findByUserid(receiverUserId).get();
		System.out.println("리시버 아이디 :" + receiver.getUserid());

		// Check if the message content is valid
		if (messageDTO.getMessageContent() == null) {
			return false;
		} else {
			// Create a new MessageEntity and save it to the database
			MessageEntity messageEntity = MessageEntity.builder().chattingRoomEntity(chattingRoomEntity).sender(sender)
					.receiver(receiver) // Directly use the managed receiver
					.messageContent(messageDTO.getMessageContent()).build();

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
		List<ChattingRoomEntity> chattingRoomEntities = chattingRoomRepository.findAllByLoggedMember2(id);

		if (chattingRoomEntities.isEmpty()) {
			chattingRoomEntities = chattingRoomRepository.findAllByLoggedMember(id);
		} else {
			for (ChattingRoomEntity room : chattingRoomEntities) {

				MemberEntity memberTmp = null;
				memberTmp = room.getMember1();

				room.setMember1(room.getMember2());

				room.setMember2(memberTmp);
			}
		}
		if (chattingRoomEntities.isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		return chattingRoomEntities.stream().map((ChattingRoomEntity entity) -> { // Explicitly specify the type here
//	                System.out.println("Mapping ChattingRoomEntity: " + entity.getTitle());

			// Log member1 and member2 user IDs
			String member1UserId = entity.getMember1() != null ? entity.getMember1().getUserid() : null;
			String member2UserId = entity.getMember2() != null ? entity.getMember2().getUserid() : null;
//	                System.out.println("member1UserId: " + member1UserId + ", member2UserId: " + member2UserId);

			// Log boardId
			String boardId = entity.getBoardEntity() != null ? String.valueOf(entity.getBoardEntity().getId()) : null;
//	                System.out.println("boardId: " + boardId);

			// Log messages inside the ChattingRoomDTO
			List<MessageEntity> messageEntities = messageRepository.findByChattingRoomEntity(id);

			Set<MessageDTO> messageDTOs = messageEntities.stream().map((MessageEntity message) -> {
//	                            System.out.println("Mapping MessageEntity: " + message.getMessageContent());
				return MessageDTO.builder().id(message.getId()).roomId(message.getChattingRoomEntity().getId())
						.messageContent(message.getMessageContent()).senderUserId(message.getSender().getUserid()) // Corrected
																													// this
																													// line
						.sendTime(message.getSendTime()).build();
			}).collect(Collectors.toSet());

//	                System.out.println("Messages mapped: " + messageDTOs);

			return ChattingRoomDTO.builder().id(entity.getId()).title(entity.getTitle()).price(entity.getPrice())
					.createTime(entity.getCreateTime()).liked(entity.isLiked()).member1UserId(member1UserId)
					.member2UserId(member2UserId).boardId(boardId).messages(messageDTOs).build();
		}).collect(Collectors.toList());
	}

	@Transactional
	public BoardEntity findBoard(Long id) {
		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(id).get();
		return chattingRoomEntity.getBoardEntity();

	}

	@Transactional
	public boolean deleteMessage(Long id) { // 선택한 메시지를 지움
		messageRepository.deleteById(id);
		return true;

	}

//	@Transactional
//	public boolean deleteRoom(Long id, Long senderId, String receiverUserId) {
//		Long receiverId = memberRepository.findByUserid(receiverUserId).get().getId();
//		// Long senderId = memberRepository.findByUserid(senderUserId).get().getId();
//
//		messageRepository.deleteAllByRoomAndSenderAndReceiver(id, senderId, receiverId);
//
//		// 송신자,수신자가 바뀔 경우도 고려
//		messageRepository.deleteAllByRoomAndSenderAndReceiver(id, receiverId, senderId);
//
////		 this.deleteMessage(id);
//		chattingRoomRepository.deleteById(id);
//
//		return true;
//	}
	
	
	@Transactional
	public boolean deleteRoom(Long roomId, Long senderId, String receiverUserId) {
		Long receiverId = memberRepository.findByUserid(receiverUserId).get().getId();
		// Long senderId = memberRepository.findByUserid(senderUserId).get().getId();

		

		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(roomId).get();
		List<MessageEntity> messages = messageRepository.findByChattingRoomEntity(roomId);
		
		if(senderId != null) {
		chattingRoomEntity.setExitedmemberId(senderId);
	//	chattingRoomEntity.setMember1(null);
		
		chattingRoomRepository.save(chattingRoomEntity);
		

		
		List<MessageEntity> filteredMessages = messages.stream()
			    .filter(message -> message.getReceiver().getId() == receiverId) // ID 비교 수정
			    .peek(message -> {
			//    message.setSender(null);
			    message.setExited(true);
			    message.setExitedSenderId(chattingRoomEntity.getExitedmemberId());
			    }) // setter 사용 시 peek() 활용
			    .collect(Collectors.toList()); // Collectors로 수정
				
			messageRepository.saveAll(filteredMessages);
		}
			else if(chattingRoomEntity.getExitedmemberId() != null) {	
		messageRepository.deleteAllByRoomAndSenderAndReceiver(roomId, senderId, receiverId);

		// 송신자,수신자가 바뀔 경우도 고려
		messageRepository.deleteAllByRoomAndSenderAndReceiver(roomId, receiverId, senderId);

//		 this.deleteMessage(id);
		chattingRoomRepository.deleteById(roomId);
		}
		return true;
	}



	  @Transactional
	    public boolean markMessagesAsRead(List<Long> messageIds) {
	        List<MessageEntity> messages = messageRepository.findAllById(messageIds);
	        
	        if (messages.isEmpty()) return false; // 메시지가 없으면 false 반환
	        
	        messages.forEach(msg -> msg.setRead(true)); // 읽음 처리
	        return true; // 성공
	    }
	  
	  
	  
//	  @Transactional			//채팅창 재방문시
//	    public boolean ReEnterChatRoom(List<Long> messageIds) {
//	        List<MessageEntity> messages = messageRepository.findAllById(messageIds);
//	        
//	        if (messages.isEmpty()) return false; // 메시지가 없으면 false 반환
//	        
//	        messages.forEach(msg -> msg.setRead(true)); // 읽음 처리
//	        return true; // 성공
//	    }

}
