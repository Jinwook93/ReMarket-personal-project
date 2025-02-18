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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

	
	 @PersistenceContext
	    private EntityManager entityManager;
	
	private final AlarmService alarmService; 
	
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
	            Long messagesCount = (long) messageRepository.findByChattingRoomEntity(chattingRoomEntity.getId()).size();
	            System.out.println("멤버2 문자 일치 확인 1: "+member2.getId());
	            System.out.println("멤버2 문자 일치 확인 2: "+chattingRoomEntity.getExitedmemberId());
	            System.out.println("메시지테스트 갯수2 : "+ messagesCount);
	            chattingRoomEntity.setMessageIndex2(messagesCount); // 메시지 수에 맞게 messageIndex 업데이트
	            chattingRoomRepository.save(chattingRoomEntity);
	          
	        } else if (chattingRoomEntity.getExitedmemberId() != null && chattingRoomEntity.getExitedmemberId().equals(member1.getId())) {
	           
	        	chattingRoomEntity.setExitedmemberId(null);
	          
	        	resetExitedMessages(chattingRoomEntity);
	            
	        	Long messagesCount = (long) messageRepository.findByChattingRoomEntity(chattingRoomEntity.getId()).size();

	            System.out.println("멤버1 문자 일치 확인 1: "+member2.getId());
	            System.out.println("멤버1  문자 일치 확인 2: "+chattingRoomEntity.getExitedmemberId());
	            System.out.println("메시지테스트 갯수1 : "+ messagesCount);
	            chattingRoomEntity.setMessageIndex1(messagesCount); // 메시지 수에 맞게 messageIndex 업데이트
	            chattingRoomRepository.save(chattingRoomEntity);
	        }
	    }

	    // 채팅방이 없으면 새로 생성
	    if (chattingRoomEntity == null) {
	        chattingRoomEntity = ChattingRoomEntity.builder()
	            .title(boardEntity.getTitle())
	            .member1(member1)
	            .member2(member2)
	            .boardEntity(boardEntity)
	            .messageIndex1(0L)
	            .messageIndex2(0L)
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
	            .recentExitedmemberId(chattingRoomEntity.getRecentExitedmemberId())
	            .messageIndex1(chattingRoomEntity.getMessageIndex1())
	            .messageIndex2(chattingRoomEntity.getMessageIndex2())
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
	public Optional<ChattingRoomEntity> findChatRoom(Long roomid) {
		return chattingRoomRepository.findById(roomid);
	}
	
	
	
	
//	@Transactional
//	public boolean enterChatRoom(Long loggedId, Long userId, Long boardId) {
//		boolean flag = false;
//
//		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findEnableRoom(userId, loggedId, boardId);
//
//		if (chattingRoomEntity == null) {
//			chattingRoomEntity = chattingRoomRepository.findEnableRoom(loggedId, userId, boardId);
//
//		}
//
//		if (chattingRoomEntity != null) {
//			flag = true;
//		}
//		return flag;
//	}

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
						message.isDeleted(), message.getParentMessageId(), message.getSender().getProfileImage(), message.getReceiver().getProfileImage(),
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
		System.out.println("부모 메시지 테스트" + messageDTO.getParentMessageId());	
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
					.parentMessageId(messageDTO.getParentMessageId())
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
	public boolean deleteMessage(Long id) {
	    try {
	        System.out.println("삭제되는 ID: " + id);
	        if (messageRepository.existsById(id)) {
	        	MessageEntity message = messageRepository.findById(id).get();				//삭제 시 메시지 List index가 밀리므로, 삭제 대신 대체 내용으로 수정함
	        	message.setMessageContent("⚠️삭제된 메시지입니다");
	        	message.setDeleted(true);
	        	//           messageRepository.deleteById(id);											//삭제 메시지
	            return true;
	        } else {
	            System.out.println("삭제할 메시지가 없습니다.");
	            return false;
	        }
	    } catch (Exception e) {
	        System.out.println("메시지 삭제 실패: " + e.getMessage());
	        return false;
	    }
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

	    // 채팅방 및 메시지 조회
	    ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(roomId)
	        .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다: " + roomId));

	    List<MessageEntity> messages = messageRepository.findByChattingRoomEntity(roomId);
	    
	    // 채팅방을 나가면서 메시지 상태 업데이트
	    if (chattingRoomEntity.getExitedmemberId() == null) {
	        chattingRoomEntity.setExitedmemberId(senderId);
	        	chattingRoomEntity.setRecentExitedmemberId(senderId);
	        // 🟢 채팅방을 먼저 저장하여 영속 상태로 만듦
	        chattingRoomRepository.saveAndFlush(chattingRoomEntity);  

	        // 메시지 필터링 및 상태 업데이트
	        List<MessageEntity> filteredMessages = messages.stream()
	            .filter(message -> message.getSender().getId().equals(senderId) &&
	                               message.getReceiver().getId().equals(receiverId))
	            .peek(message -> {
	                message.setExited(true);
	                message.setExitedSenderId(chattingRoomEntity.getExitedmemberId());
	            })
	            .collect(Collectors.toList());

	        // 🟢 변경된 메시지 저장
	        messageRepository.saveAll(filteredMessages);
	        entityManager.flush();  // 메시지 삭제 즉시 반영
    	    entityManager.clear();  // 영속성 컨텍스트 초기화
        	alarmService.postAlarm(senderId, senderId, receiverId, "MESSAGE", "채팅방", String.valueOf(roomId), "나가기", null);
	    } else {
	    	System.out.println("else 삭제 되는건가요?????????");
	    	  forceDeleteRoom(roomId);
	    	  
	    		System.out.println("결국 else 삭제 되는건가요?????????");
	    		alarmService.postAlarm(senderId, senderId, receiverId, "MESSAGE", "채팅방", String.valueOf(roomId), "완전삭제", null);
//	        // 🟢 메시지 삭제
//	        messageRepository.deleteByRoomId(roomId);
//	        messageRepository.flush();
//	        // 🟢 채팅방 삭제
////	        chattingRoomRepository.deleteById(roomId);
////	        chattingRoomRepository.flush();
////	  
	    }
	    
	    return true;
	}

    @Transactional		//강제 삭제
    public void forceDeleteRoom(Long roomId) {
    	   messageRepository.deleteByRoomId(roomId);
    	    entityManager.flush();  // 메시지 삭제 즉시 반영
    	    entityManager.clear();  // 영속성 컨텍스트 초기화

    	    chattingRoomRepository.deleteById(roomId);
    	    entityManager.flush();  // 채팅방 삭제 즉시 반영
    	    entityManager.clear();
    }


	  @Transactional
	    public boolean markMessagesAsRead(List<Long> messageIds) {
	        List<MessageEntity> messages = messageRepository.findAllById(messageIds);
	        
	        if (messages.isEmpty()) return false; // 메시지가 없으면 false 반환
	        
	        messages.forEach(msg -> msg.setRead(true)); // 읽음 처리
	        return true; // 성공
	    }

	
	  @Transactional
	public Long findMessagesByRoomId(Long roomId, MemberEntity memberEntity) {
		  Optional<ChattingRoomEntity> room =chattingRoomRepository.findById(roomId);
		  List<MessageEntity> messages = (List<MessageEntity>) room.get().getMessages() ;
	//	  memberEntity.getId()
		  System.out.println("메시지 갯수" +messages.size());
	       return (long) (messages.isEmpty()?0 : messages.size());
	}

	  
	  @Transactional
	public String findMemberPosition(Long roomId, MemberEntity memberEntity) {
		  Optional<ChattingRoomEntity> room =chattingRoomRepository.findById(roomId);
		 if(memberEntity.getId().equals(room.get().getMember1())) {	
			 return "logged1";
		 }else  if(memberEntity.getId().equals(room.get().getMember2())) {
			 return "logged2";
		 }
		 
		  return null;
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
