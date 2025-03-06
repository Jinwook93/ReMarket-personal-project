package com.cos.project.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.cos.project.dto.PagedResponse;
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

		MemberEntity boardMember = boardEntity.getMemberEntity();

		MemberEntity member1 = memberRepository.findByUserid(loggedId)
				.orElseThrow(() -> new IllegalArgumentException("송신자를 찾을 수 없습니다."));

		MemberEntity member2 = memberRepository.findByUserid(userId)
				.orElseThrow(() -> new IllegalArgumentException("수신자를 찾을 수 없습니다."));

		// 기존 채팅방이 있는지 확인
		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findEnableRoom(member1.getId(), member2.getId(),
				boardId);

		if (chattingRoomEntity == null) {
			chattingRoomEntity = chattingRoomRepository.findEnableRoom(member2.getId(), member1.getId(), boardId);
		}

		// ⚠️ chattingRoomEntity가 null인지 먼저 체크 후 getExitedmemberId() 호출해야 함
		if (chattingRoomEntity != null) {
//			if (chattingRoomEntity.getExitedmemberId() != null
//					&& chattingRoomEntity.getExitedmemberId().equals(member2.getId())) {
//				chattingRoomEntity.setExitedmemberId(null);
//				resetExitedMessages(chattingRoomEntity);
//				Long messagesCount = (long) messageRepository.findByChattingRoomEntity(chattingRoomEntity.getId())
//						.size();
//				System.out.println("멤버2 문자 일치 확인 1: " + member2.getId());
//				System.out.println("멤버2 문자 일치 확인 2: " + chattingRoomEntity.getExitedmemberId());
//				System.out.println("메시지테스트 갯수2 : " + messagesCount);
//				
////				MemberEntity memberTmp = null;
//				
////				chattingRoomEntity.setMember2(member2);
////				chattingRoomEntity.setMember1(member1);
//				
//				System.out.println("멤버 1은?" +member1.getId());
//				System.out.println("멤버 2은?" +member2.getId());
//				chattingRoomEntity.setMessageIndex2(messagesCount); // 메시지 수에 맞게 messageIndex 업데이트
//				chattingRoomRepository.save(chattingRoomEntity);
//				chattingRoomRepository.flush();  
//	} else 

			if (chattingRoomEntity.getExitedmemberId() != null
					&& chattingRoomEntity.getExitedmemberId().equals(member1.getId())) { // 나간 유저가 로그인 한 유저와 같을 떄

				chattingRoomEntity.setExitedmemberId(null);

				resetExitedMessages(chattingRoomEntity);

				Long messagesCount = (long) messageRepository.findByChattingRoomEntity(chattingRoomEntity.getId())
						.size();

				if (!member1.getId().equals(boardMember.getId())) {

//					if (chattingRoomEntity.getMember1().getId().equals(boardMember.getId())) { // member1, member2가 메시지
//																																								// 송신 시 바뀌기 떄문에 서로 바꿔주는 작업 진행
//						chattingRoomEntity.setMember1(chattingRoomEntity.getMember2());
//						chattingRoomEntity.setMember2(boardMember);
//					}
					;
//					System.out.println("멤버1 문자 일치 확인 1: " + member2.getId());
//					System.out.println("멤버1  문자 일치 확인 2: " + chattingRoomEntity.getExitedmemberId());
//					System.out.println("메시지테스트 갯수1 : " + messagesCount);
					chattingRoomEntity.setMessageIndex1(messagesCount); // 메시지 수에 맞게 messageIndex 업데이트
				} else if (member1.getId().equals(boardMember.getId())) {
//					System.out.println("멤버2문자 일치 확인 1: " + member2.getId());
//					System.out.println("멤버2 문자 일치 확인 2: " + chattingRoomEntity.getExitedmemberId());
//					System.out.println("메시지테스트 갯수2 : " + messagesCount);
					chattingRoomEntity.setMessageIndex2(messagesCount); // 메시지 수에 맞게 messageIndex 업데이트
				}

				chattingRoomRepository.save(chattingRoomEntity);
				chattingRoomRepository.flush();
			}
		}

		// 채팅방이 없으면 새로 생성
		if (chattingRoomEntity == null) {
			chattingRoomEntity = ChattingRoomEntity.builder().title(boardEntity.getTitle()).member1(member1)
					.member2(member2).boardEntity(boardEntity).messageIndex1(0L).messageIndex2(0L).price(price)
					.member1Visible(true).member2Visible(false).build();
			chattingRoomRepository.save(chattingRoomEntity);
			chattingRoomRepository.flush();
		} else {
			List<MessageEntity> messages = messageRepository.findByChattingRoomEntity(chattingRoomEntity.getId());
			if (messages.isEmpty()) {
				chattingRoomEntity.setMember1Visible(true);
				chattingRoomEntity.setMember2Visible(false);
			} else {
				chattingRoomEntity.setMember1Visible(true);
				chattingRoomEntity.setMember2Visible(true);
			}
			chattingRoomRepository.flush();
		}

//		System.out.println("member1Visible:" + chattingRoomEntity.getMember1Visible());
//		System.out.println("member2Visible:" + chattingRoomEntity.getMember2Visible());
		ChattingRoomDTO responseDTO = ChattingRoomDTO.builder().id(chattingRoomEntity.getId())
				.title(chattingRoomEntity.getTitle()).price(chattingRoomEntity.getPrice())
				.member1UserId(chattingRoomEntity.getMember1().getUserid())
				.member2UserId(chattingRoomEntity.getMember2().getUserid())
				.boardId(String.valueOf(chattingRoomEntity.getBoardEntity().getId()))
				.recentExitedmemberId(chattingRoomEntity.getRecentExitedmemberId())
				.messageIndex1(chattingRoomEntity.getMessageIndex1())
				.messageIndex2(chattingRoomEntity.getMessageIndex2())
				.member1Visible(chattingRoomEntity.getMember1Visible())
				.member2Visible(chattingRoomEntity.getMember2Visible()).build();

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
						message.isDeleted(), message.getParentMessageId(), message.getSender().getProfileImage(),
						message.getReceiver().getProfileImage(), message.getAlarmType(), message.getSendTime()))
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
//		System.out.println("리시버아이디 테스트" + receiverUserId);
//		System.out.println("부모 메시지 테스트" + messageDTO.getParentMessageId());
		// Retrieve the chatting room and sender information
		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(roomId)
				.orElseThrow(() -> new RuntimeException("Chat room not found"));
		MemberEntity sender = principalDetails.getMemberEntity();

		// Use the existing receiver from the chatting room
		MemberEntity receiver = memberRepository.findByUserid(receiverUserId).get();
//		System.out.println("리시버 아이디 :" + receiver.getUserid());

		// Check if the message content is valid
		if (messageDTO.getMessageContent() == null) {
			return false;
		} else {
			// Create a new MessageEntity and save it to the database
			MessageEntity messageEntity = MessageEntity.builder().chattingRoomEntity(chattingRoomEntity).sender(sender)
					.receiver(receiver) // Directly use the managed receiver
					.parentMessageId(messageDTO.getParentMessageId()).alarmType(messageDTO.getAlarmType())
					.messageContent(messageDTO.getMessageContent()).build();

			messageRepository.save(messageEntity);
			messageRepository.flush();

//			if(chattingRoomEntity.getMember2Visible().equals(false)) {		//room의 메시지가 최초로 등록될 시 member2에게도 보이게 함 
//				chattingRoomEntity.setMember2Visible(true);
//			}

			if (chattingRoomEntity.getMember1Visible().equals(false)
					|| chattingRoomEntity.getMember2Visible().equals(false)) { // room의 메시지가 최초로 등록될 시 member2에게도 보이게 함
				chattingRoomEntity.setMember1Visible(true);
				chattingRoomEntity.setMember2Visible(true);
			}

			return true;
		}
	}

//		MessageEntity messageResult = messageRepository.findById(null)
//		
//		return messageResult;

	@Transactional
	public List<ChattingRoomDTO> myChattingRoomList(Long id, Long loggedId) {
//		List<ChattingRoomEntity> chattingRoomEntities = chattingRoomRepository.findAllByLoggedMember2(id);
		List<ChattingRoomEntity> chattingRoomEntities = chattingRoomRepository.findAllByLoggedMember(id);
//		if (chattingRoomEntities.isEmpty()) {
//			chattingRoomEntities = chattingRoomRepository.findAllByLoggedMember(id);
//		} else {

//		if (!chattingRoomEntities.isEmpty()) {
//			for (ChattingRoomEntity room : chattingRoomEntities) {
//
//				MemberEntity memberTmp = null;
//				memberTmp = room.getMember1();
//
//				Boolean visibleTmp = null;
//				visibleTmp = room.getMember1Visible();
//
//				Long messageIndexTmp = null;
//				messageIndexTmp = room.getMessageIndex1();
//
//				if (loggedId.equals(room.getMember2().getId())) {
//					room.setMember1(room.getMember2());
//					room.setMember1Visible(room.getMember2Visible());
////					room.setMessageIndex1(room.getMessageIndex2());
//
//					room.setMember2(memberTmp);
//					room.setMember2Visible(visibleTmp);
////					room.setMessageIndex2(messageIndexTmp);
//				}
//			}
//		} else
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
					.member2UserId(member2UserId).boardId(boardId).messages(messageDTOs)
					.member1Visible(entity.getMember1Visible()).member2Visible(entity.getMember2Visible()).build();
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
				MessageEntity message = messageRepository.findById(id).get(); // 삭제 시 메시지 List index가 밀리므로, 삭제 대신 대체
																				// 내용으로 수정함
				message.setMessageContent("⚠️삭제된 메시지입니다");
				message.setDeleted(true);
				// messageRepository.deleteById(id); //삭제 메시지
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

			if (chattingRoomEntity.getMember1Visible().equals(false)
					|| chattingRoomEntity.getMember2Visible().equals(false)) {
				forceDeleteRoom(roomId);
				alarmService.postAlarm(senderId, senderId, receiverId, "MESSAGE", "채팅방", String.valueOf(roomId), "완전삭제",
						null);
			} else {
				chattingRoomEntity.setExitedmemberId(senderId);
				chattingRoomEntity.setRecentExitedmemberId(senderId);
				// 🟢 채팅방을 먼저 저장하여 영속 상태로 만듦
				chattingRoomRepository.saveAndFlush(chattingRoomEntity);

				// 메시지 필터링 및 상태 업데이트
				List<MessageEntity> filteredMessages = messages.stream()
						.filter(message -> message.getSender().getId().equals(senderId)
								&& message.getReceiver().getId().equals(receiverId))
						.peek(message -> {
							message.setExited(true);
							message.setExitedSenderId(chattingRoomEntity.getExitedmemberId());
						}).collect(Collectors.toList());

				// 🟢 변경된 메시지 저장
				messageRepository.saveAll(filteredMessages);
				entityManager.flush(); // 메시지 삭제 즉시 반영
				entityManager.clear(); // 영속성 컨텍스트 초기화

				alarmService.postAlarm(senderId, senderId, receiverId, "MESSAGE", "채팅방", String.valueOf(roomId), "나가기",
						null);
			}
		} else {
			forceDeleteRoom(roomId);

			alarmService.postAlarm(senderId, senderId, receiverId, "MESSAGE", "채팅방", String.valueOf(roomId), "완전삭제",
					null);
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

	@Transactional // 강제 삭제
	public void forceDeleteRoom(Long roomId) {
		messageRepository.deleteByRoomId(roomId);
		entityManager.flush(); // 메시지 삭제 즉시 반영
		entityManager.clear(); // 영속성 컨텍스트 초기화

		chattingRoomRepository.deleteById(roomId);
		entityManager.flush(); // 채팅방 삭제 즉시 반영
		entityManager.clear();
	}

//	@Transactional
//	public boolean markMessagesAsRead(List<Long> messageIds) {
//		List<MessageEntity> messages = messageRepository.findAllById(messageIds);
//
//		if (messages.isEmpty())
//			return false; // 메시지가 없으면 false 반환
//
//		messages.forEach(msg -> msg.setRead(true)); // 읽음 처리
//		return true; // 성공
//	}

	@Transactional
	public boolean markMessagesAsRead(List<Long> messageIds) {
		List<MessageEntity> messages = messageRepository.findAllById(messageIds).stream().filter(msg -> !msg.isRead()) // ✅
																														// 이미
																														// 읽은
																														// 메시지는
																														// 제외
				.collect(Collectors.toList());

		if (messages.isEmpty())
			return false; // 변경할 메시지가 없으면 false

		messages.forEach(msg -> msg.setRead(true)); // 읽음 처리
		return true; // 성공
	}

	@Transactional
	public Long findMessagesByRoomId(Long roomId, MemberEntity memberEntity) {
		Optional<ChattingRoomEntity> room = chattingRoomRepository.findById(roomId);
		List<MessageEntity> messages = (List<MessageEntity>) room.get().getMessages();
		// memberEntity.getId()
		System.out.println("메시지 갯수" + messages.size());
		return (long) (messages.isEmpty() ? 0 : messages.size());
	}

	@Transactional
	public String findMemberPosition(Long roomId, MemberEntity memberEntity) {
		Optional<ChattingRoomEntity> room = chattingRoomRepository.findById(roomId);
		if (memberEntity.getId().equals(room.get().getMember1())) {
			return "logged1";
		} else if (memberEntity.getId().equals(room.get().getMember2())) {
			return "logged2";
		}

		return null;
	}

	@Transactional // 채팅방 번호 조회
	public Long findRoomId(MemberEntity member, Long boardId) {
		Long loggedId = member.getId(); // 로그인 유저의 Id
		BoardEntity boardEntity = boardRepository.findById(boardId).orElse(null);

		if (boardEntity == null) {
			throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다: " + boardId);
		}

		Long targetId = boardEntity.getMemberEntity().getId(); // 상대방 Id
		Long roomId = null;

		// findEnableRoom()이 null을 반환할 경우 대비
		ChattingRoomEntity room = chattingRoomRepository.findEnableRoom(loggedId, targetId, boardId);
		if (room != null) {
			roomId = room.getId();
		}

		if (roomId == null) {
			room = chattingRoomRepository.findEnableRoom(targetId, loggedId, boardId);
			if (room != null) {
				roomId = room.getId();
			}
		}

		if (roomId == null) {
			ChattingRoomDTO chattingRoomDTO = this.findOrCreateRoom(boardEntity.getTitle(), member.getUserid(),
					boardEntity.getMemberEntity().getUserid(), boardId, boardEntity.getPrice());
			roomId = chattingRoomDTO.getId();
		}

		System.out.println("roomId + " + roomId);
		return roomId;
	}

//	  public PagedResponse<ChattingRoomDTO> myChattingRoomList(Long loggedId, Pageable pageable) {
//		    Page<ChattingRoomEntity> roomEntities = chattingRoomRepository.findAllByLoggedMember1(loggedId, pageable);
//
//		    List<ChattingRoomDTO> rooms = roomEntities.stream().map(room ->   
//		        new ChattingRoomDTO(
//		            room.getId(),
//		            room.getTitle(),
//		            room.getPrice(), 
//		            room.getCreateTime(),
//		            room.isLiked(),
//		            room.getMember1().getUserid(), 
//		            room.getMember2().getUserid(),
//		            room.getBoardEntity().getId(), 
//		            room.getExitedmemberId(),
//		            room.getMessageIndex1(),
//		            room.getMessageIndex2(), 
//		            room.getRecentExitedmemberId(), 
//		            room.getMessages().stream()
//		            .map(msg -> new MessageDTO(
//		                msg.getId(),
//		                msg.getChattingRoomEntity() != null ? msg.getChattingRoomEntity().getId() : null,
//		                msg.getSender() != null ? msg.getSender().getUserid() : null,
//		                msg.getReceiver() != null ? msg.getReceiver().getUserid() : null,
//		                msg.getMessageContent(),
//		                msg.isLiked(),
//		                msg.isRead(),
//		                msg.isExited(),
//		                msg.getExitedSenderId(),
//		                msg.isDeleted(),
//		                msg.getParentMessageId(),
//		                msg.getSender() != null ? msg.getSender().getProfileImage() : null,
//		                msg.getReceiver() != null ? msg.getReceiver().getProfileImage() : null,
//		                msg.getAlarmType(),
//		                msg.getSendTime()
//		            )).collect(Collectors.toSet()) // Set<MessageDTO> 변환
//
//		        )
//		    ).collect(Collectors.toList());
//
//		    return new PagedResponse<>(
//		        rooms,
//		        roomEntities.getNumber(),
//		        roomEntities.getSize(),
//		        roomEntities.getTotalPages(),
//		        roomEntities.getTotalElements(),
//		        roomEntities.isLast()
//		    );
//		}

//	  @Transactional			//채팅창 재방문시
//	    public boolean ReEnterChatRoom(List<Long> messageIds) {
//	        List<MessageEntity> messages = messageRepository.findAllById(messageIds);
//	        
//	        if (messages.isEmpty()) return false; // 메시지가 없으면 false 반환
//	        
//	        messages.forEach(msg -> msg.setRead(true)); // 읽음 처리
//	        return true; // 성공
//	    }

	@Transactional
	public MessageDTO recentRoomMessage(Long roomId) {
		// 방을 조회하고, 없다면 null 반환
//	    ChattingRoomEntity room = chattingRoomRepository.findById(roomId).get();

//	    System.out.println("방 정보 조회 완료: " + room);

//	        Set<MessageEntity> messages = room.getMessages();			<-- 왜 안되는지 원인을 못찾음 (null로 인식함)
		List<MessageEntity> messages = messageRepository.findByChattingRoomEntity(roomId);

		// 메시지 중 최신 메시지 추출
		return messages.stream().filter(message -> message.getSendTime() != null) // sendTime이 null이 아닌 메시지만 필터링
				.max(Comparator.comparing(MessageEntity::getSendTime)) // 최신 메시지 찾기
				.map(message -> {
//	                    System.out.println("최근 메시지: " + message.getMessageContent());  // 로그 추가
					return message.convertToDTO(message); // message 객체에서 convertToDTO 호출
				}).orElse(new MessageDTO(null, null, "최근 메시지가 없습니다", null)); // 최신 메시지가 없으면 null 반환
	}

	@Transactional
	public List<MessageDTO> findUnReadMessages(Long loggedId) {

		String loggedUserId = memberRepository.findById(loggedId)
				.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다.")).getUserid();

		List<ChattingRoomEntity> rooms = chattingRoomRepository.findAllByLoggedMember(loggedId);
		List<MessageEntity> filteredMessages = new ArrayList<>();

		for (ChattingRoomEntity room : rooms) {
			List<MessageEntity> messages = messageRepository.findByChattingRoomEntity(room.getId());

			Long messageIndex = (room.getMember1().equals(loggedId)) ? room.getMessageIndex1()
					: room.getMessageIndex2();

			if (messageIndex == null) {
				continue; // messageIndex가 없으면 건너뜀
			}

			int index = Math.toIntExact(messageIndex);
			for (; index < messages.size(); index++) {
				MessageEntity message = messages.get(index);
				if (message.isRead() || message.getSender().getId().equals(loggedId)) { // 읽지않음 상태이거나 (로그인유저가 송신자일 경우에
																						// 대한 메시지는 제외)
					continue;
				}
				filteredMessages.add(message);
			}
		}

		return filteredMessages.stream().map(message -> message.convertToDTO(message)).collect(Collectors.toList());
	}

	@Transactional // 한 채팅방 당 읽지않음 메시지 수
	public List<MessageDTO> findUnReadMessage(Long roomId, Long loggedId) {

		Optional<ChattingRoomEntity> room = chattingRoomRepository.findById(roomId);
		List<MessageEntity> messages = messageRepository.findByChattingRoomEntity(roomId);

		Long messageIndex = (room.get().getMember1().getId().equals(loggedId)) ? room.get().getMessageIndex1()
				: room.get().getMessageIndex2();

		List<MessageEntity> filteredMessages = new ArrayList<>();

		if (messageIndex == null) {
			messageIndex = 0L;
//	            continue; // messageIndex가 없으면 건너뜀
		}

		int index = Math.toIntExact(messageIndex);
		for (; index < messages.size(); index++) {
			MessageEntity message = messages.get(index);
			if (message.isRead() || message.getSender().getId().equals(loggedId)) { // 읽지않음 상태이거나 (로그인유저가 송신자일 경우에 대한
																					// 메시지는 제외)
				continue;
			}

			filteredMessages.add(message);
		}

		return filteredMessages.stream().map(message -> message.convertToDTO(message)).collect(Collectors.toList());

	}

}
