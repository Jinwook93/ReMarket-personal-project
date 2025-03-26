package com.cos.project.service;

import java.sql.Timestamp;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cos.project.details.PrincipalDetails;
import com.cos.project.dto.ChattingRoomDTO;
import com.cos.project.dto.MessageDTO;
import com.cos.project.dto.PagedResponse;
import com.cos.project.dto.TradeDTO;
import com.cos.project.entity.AlarmEntity;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.ChattingRoomEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.entity.MessageEntity;
import com.cos.project.entity.TradeEntity;
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

	private final TradeService tradeService;

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

			if (chattingRoomEntity.getExitedmemberId() != null
					&& chattingRoomEntity.getExitedmemberId().equals(member1.getId())) { // 나간 유저가 로그인 한 유저와 같을 떄

				chattingRoomEntity.setExitedmemberId(null);

//				resetExitedMessages(chattingRoomEntity);

//				//입장 시 메시지 상태창 추가 
				this.setMessageEntityStatusBar(chattingRoomEntity.getId(), member1.getId(), "enterRoom");
				
				Long messagesCount = (long) messageRepository.findByChattingRoomEntity(chattingRoomEntity.getId())
						.size();

				AlarmEntity alarmEntity = alarmService.postAlarm(member1.getId(), member1.getId(),
						member2.getId(), "MESSAGE", "채팅방", String.valueOf(chattingRoomEntity.getId()), "재입장", null);
				
				if (!member1.getId().equals(boardMember.getId())) {
					chattingRoomEntity.setMessageIndex1(messagesCount); // 메시지 수에 맞게 messageIndex 업데이트
				} else if (member1.getId().equals(boardMember.getId())) {
					chattingRoomEntity.setMessageIndex2(messagesCount); // 메시지 수에 맞게 messageIndex 업데이트
				}

				chattingRoomRepository.save(chattingRoomEntity);
				chattingRoomRepository.flush(); // 강제 반영

				// 최신 값을 다시 조회
				ChattingRoomEntity updatedRoom = chattingRoomRepository.findById(chattingRoomEntity.getId())
						.orElseThrow();

				if (!member1.getId().equals(boardMember.getId())) {
					updatedRoom.setReCreateTime1(updatedRoom.getUpdateTime()); // ✅ 최신 updateTime 사용
				} else {
					updatedRoom.setReCreateTime2(updatedRoom.getUpdateTime()); // ✅ 최신 updateTime 사용
				}

				chattingRoomRepository.save(updatedRoom); // 변경된 값 저장
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

//	@Transactional
//	private void resetExitedMessages(ChattingRoomEntity chattingRoomEntity) {
//		List<MessageEntity> messages = messageRepository.findByChattingRoomEntity(chattingRoomEntity.getId());
//		for (MessageEntity message : messages) {
//			message.setExited(false);
//		}
//		messageRepository.saveAll(messages);
//	}

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
						message.getReceiver().getProfileImage(), message.getAlarmType(), message.getStatusBar(),
						message.getSendTime(),message.getExpired()))
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
		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(roomId)
				.orElseThrow(() -> new RuntimeException("Chat room not found"));
		MemberEntity sender = principalDetails.getMemberEntity();

		MemberEntity receiver = memberRepository.findByUserid(receiverUserId).get();

		if (messageDTO.getMessageContent() == null) {
			return false;
		} else {
			List<MessageDTO> messages = this.showRoomMessage(roomId);
			// 재입장 후 채팅 입력시 메시지 상태창 추가
			if (sender.getId().equals(chattingRoomEntity.getMember1().getId())
					&& chattingRoomEntity.getMessageIndex1() > 0
					&& messages.size() - chattingRoomEntity.getMessageIndex1() == 0) {
//				this.setMessageEntityStatusBar(chattingRoomEntity.getId(), principalDetails.getMemberEntity().getId(),
//						"enterRoom");
//				chattingRoomEntity.setMessageIndex1(chattingRoomEntity.getMessageIndex1() + 1);
			
				
//				this.setMessageEntityStatusBar(chattingRoomEntity.getId(), principalDetails.getMemberEntity().getId(),
//						"enterRoom");
				chattingRoomEntity.setMessageIndex1(chattingRoomEntity.getMessageIndex1());
				
				
				
			} else if (sender.getId().equals(chattingRoomEntity.getMember2().getId())
					&& chattingRoomEntity.getMessageIndex2() > 0
					&& messages.size() - chattingRoomEntity.getMessageIndex2() == 0) {
//				this.setMessageEntityStatusBar(chattingRoomEntity.getId(), principalDetails.getMemberEntity().getId(),
//						"enterRoom");
//				chattingRoomEntity.setMessageIndex2(chattingRoomEntity.getMessageIndex2() + 1);
				
				
				
//				this.setMessageEntityStatusBar(chattingRoomEntity.getId(), principalDetails.getMemberEntity().getId(),
//				"enterRoom");
		chattingRoomEntity.setMessageIndex2(chattingRoomEntity.getMessageIndex2());
			}

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

	// chattingRoomEntities를 getSendTime() 역순으로 map을 한다(최근 메시지를 가진 방이 가장 위에 위치)
	// 만약 entity의 recentMessage.getSendTime() 이 없을 경우 entity.getCreateTime()를 대신
	// 비교대상으로 한다

	@Transactional
	public List<ChattingRoomDTO> myChattingRoomList(Long loggedId) {
		List<ChattingRoomEntity> chattingRoomEntities = chattingRoomRepository.findAllByLoggedMember(loggedId);

		if (chattingRoomEntities.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		MemberEntity loggedMember = memberRepository.findById(loggedId).get();
		String loggedUserId = loggedMember.getUserid();

		List<ChattingRoomDTO> chattingRoomDTOs = chattingRoomEntities.stream()
				.filter(entity -> entity.getExitedmemberId() == null || !entity.getExitedmemberId().equals(loggedId))
				.map((ChattingRoomEntity entity) -> {
					// Log member1 and member2 user IDs
					String member1UserId = entity.getMember1() != null ? entity.getMember1().getUserid() : null;
					String member2UserId = entity.getMember2() != null ? entity.getMember2().getUserid() : null;

					// Log boardId
					String boardId = entity.getBoardEntity() != null ? String.valueOf(entity.getBoardEntity().getId())
							: null;

					// Log messages inside the ChattingRoomDTO
					List<MessageEntity> messageEntities = messageRepository.findByChattingRoomEntity(entity.getId());
					MessageDTO recentMessage = this.recentRoomMessage(entity.getId(), loggedId);

					return ChattingRoomDTO.builder().id(entity.getId()).title(entity.getTitle())
							.price(entity.getPrice()).createTime(recentMessage.getSendTime()) // Sorting 기준 필드
							.liked(entity.isLiked()).member1UserId(member1UserId).member2UserId(member2UserId)
							.boardId(boardId).member1Visible(entity.getMember1Visible())
							.member2Visible(entity.getMember2Visible()).messageIndex1(entity.getMessageIndex1())
							.messageIndex2(entity.getMessageIndex2()).exitedmemberId(entity.getExitedmemberId())
							.recentExitedmemberId(entity.getRecentExitedmemberId()).build();
				})
				.sorted(Comparator.comparing(ChattingRoomDTO::getCreateTime,
						Comparator.nullsLast(Comparator.reverseOrder()))) // 최근 순 정렬
				.collect(Collectors.toList());
		return chattingRoomDTOs;
	}

	@Transactional
	public BoardEntity findBoard(Long id) {
		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(id).get();
		return chattingRoomEntity.getBoardEntity();

	}

	@Transactional
	public boolean deleteMessage(Long id) {
		try {
//			System.out.println("삭제되는 ID: " + id);
			if (messageRepository.existsById(id)) {
				MessageEntity message = messageRepository.findById(id).get(); // 삭제 시 메시지 List index가 밀리므로, 삭제 대신 대체
																				// 내용으로 수정함
				message.setMessageContent("⚠️삭제된 메시지입니다");
				message.setDeleted(true);
				// messageRepository.deleteById(id); //삭제 메시지
				return true;
			} else {
//				System.out.println("삭제할 메시지가 없습니다.");
				return false;
			}
		} catch (Exception e) {
//			System.out.println("메시지 삭제 실패: " + e.getMessage());
			return false;
		}
	}

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

				// 나갈 시 메신저 상태창 추가
				this.setMessageEntityStatusBar(roomId, senderId, "exitRoom");

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
			TradeDTO tradeDTO = tradeService.findNotTradeByRoomId(roomId, senderId);
			if (tradeDTO != null) {
				tradeService.deleteByTradeEntityId(tradeDTO.getId());
			}
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
//		System.out.println("메시지 갯수" + messages.size());
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
	public MessageDTO recentRoomMessage(Long roomId, Long loggedId) {

//	        Set<MessageEntity> messages = room.getMessages();			<-- 왜 안되는지 원인을 못찾음 (null로 인식함)
		List<MessageEntity> messages = messageRepository.findByChattingRoomEntity(roomId);
		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(roomId).get();

		List<MessageEntity> filteredMessages = null;

		MessageDTO filteredMessageDTO = null;

		String profileImageUrl = "/boardimage/nullimage.jpg";

		// 재입장의 경우일 때
		Timestamp reCreateTime = null;
		Long messageIndex = null;
		if (chattingRoomEntity.getMember1().getId().equals(loggedId)) {
			reCreateTime = chattingRoomEntity.getReCreateTime1();
			messageIndex = chattingRoomEntity.getMessageIndex1();
//            	if(chattingRoomEntity.getMember1().getProfileImage() != null) {
//            		profileImageUrl = chattingRoomEntity.getMember1().getProfileImage();
//            	}

		} else if (chattingRoomEntity.getMember2().getId().equals(loggedId)) {
			reCreateTime = chattingRoomEntity.getReCreateTime2();
			messageIndex = chattingRoomEntity.getMessageIndex2();

//            if(chattingRoomEntity.getMember2().getProfileImage() != null) {
//        		profileImageUrl = chattingRoomEntity.getMember2().getProfileImage();
//        	}
		}

		// messageIndex 부터 시작하는 신규 메시지 리스트 추출
		if (messageIndex > 0 && messageIndex != null) {
			filteredMessages = getMessagesFromIndex(messages, messageIndex);

			// filteredMessages가 비었는지 확인
//        System.out.println("filteredMessages 크기: " + filteredMessages.size());
//        System.out.println("filteredMessages 내용: " + filteredMessages);

			// 상태바 메시지 제외
			filteredMessageDTO = filteredMessages.stream()
					.filter(message -> message.getStatusBar() == null || !message.getStatusBar())
					// sendTime이 null이 아닌 메시지만 필터링
					.max(Comparator.comparing(MessageEntity::getSendTime,
							Comparator.nullsLast(Comparator.naturalOrder())))// 최신 메시지 찾기 //null도 허용
					.map(message -> {
						if (loggedId.equals(chattingRoomEntity.getMember1().getId())
								&& chattingRoomEntity.getMessageIndex1() > 0
								&& (messages.size() - chattingRoomEntity.getMessageIndex1() == 0)) { // 재방문한 loggedId에
																										// 대한 조건
							return new MessageDTO(null, null, null, "최근 메시지가 없습니다1", chattingRoomEntity.getId(), null,
									chattingRoomEntity.getReCreateTime1() != null
											? chattingRoomEntity.getReCreateTime1()
											: chattingRoomEntity.getCreateTime(),null);
						} else if (loggedId.equals(chattingRoomEntity.getMember2().getId())
								&& chattingRoomEntity.getMessageIndex2() > 0
								&& (messages.size() - chattingRoomEntity.getMessageIndex2() == 0)) { // 재방문한 loggedId에
																										// 대한 조건
							return new MessageDTO(null, null, null, "최근 메시지가 없습니다2", chattingRoomEntity.getId(), null,
									chattingRoomEntity.getReCreateTime2() != null
											? chattingRoomEntity.getReCreateTime2()
											: chattingRoomEntity.getCreateTime(),null);
						}
						return message.convertToDTO(message); // message 객체에서 convertToDTO 호출
					}).orElse(new MessageDTO(null, null, null, "최근 메시지가 없습니다3", chattingRoomEntity.getId(), null,
							reCreateTime != null ? reCreateTime : chattingRoomEntity.getCreateTime(),null)); // 최신 메시지가 없으면
																										// null 반환
		} else {

			filteredMessageDTO = messages.stream()
					.filter(message -> message.getStatusBar() == null || !message.getStatusBar())
					// sendTime이 null이 아닌 메시지만 필터링
					.max(Comparator.comparing(MessageEntity::getSendTime,
							Comparator.nullsLast(Comparator.naturalOrder())))// 최신 메시지 찾기 //null도 허용
					.map(message -> {
						if (loggedId.equals(chattingRoomEntity.getMember1().getId())
								&& chattingRoomEntity.getMessageIndex1() > 0
								&& (messages.size() - chattingRoomEntity.getMessageIndex1() == 0)) { // 재방문한 loggedId에
																										// 대한 조건
							return new MessageDTO(null, null, null, "최근 메시지가 없습니다4", chattingRoomEntity.getId(), null,
									chattingRoomEntity.getReCreateTime1() != null
											? chattingRoomEntity.getReCreateTime1()
											: chattingRoomEntity.getCreateTime(),null);
						} else if (loggedId.equals(chattingRoomEntity.getMember2().getId())
								&& chattingRoomEntity.getMessageIndex2() > 0
								&& (messages.size() - chattingRoomEntity.getMessageIndex2() == 0)) { // 재방문한 loggedId에
																										// 대한 조건
							return new MessageDTO(null, null, null, "최근 메시지가 없습니다5", chattingRoomEntity.getId(), null,
									chattingRoomEntity.getReCreateTime2() != null
											? chattingRoomEntity.getReCreateTime2()
											: chattingRoomEntity.getCreateTime(),null);
						}
						return message.convertToDTO(message); // message 객체에서 convertToDTO 호출
					}).orElse(new MessageDTO(null, null, null, "최근 메시지가 없습니다6", chattingRoomEntity.getId(), null,
							reCreateTime != null ? reCreateTime : chattingRoomEntity.getCreateTime(),null)); // 최신 메시지가 없으면
																										// null 반환

		}

//		   // Determine the time to use for sorting
//        Timestamp messageTime = (filteredMessageDTO != null && filteredMessageDTO.getSendTime() != null)
//                ? filteredMessageDTO.getSendTime()
//                : (reCreateTime != null) 
//                    ? reCreateTime
//                    : chattingRoomEntity.getCreateTime();  // Use createTime as fallback
//		

		// null이 아닐 때만 설정
//        if (messageTime != null) {
//            filteredMessageDTO.setSendTime(messageTime);
//        }

//        System.out.println(filteredMessageDTO.toString());
//        
		return filteredMessageDTO;
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

	@Transactional // 메신저의 상태창 만들기
	public void setMessageEntityStatusBar(Long roomId, Long loggedId, String flag) {

		MemberEntity member1 = null;
		MemberEntity member2 = null;
		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(roomId).orElse(null);

		if (loggedId.equals(chattingRoomEntity.getMember1().getId())) {
			member1 = chattingRoomEntity.getMember1();
			member2 = chattingRoomEntity.getMember2();
		} else if (loggedId.equals(chattingRoomEntity.getMember2().getId())) {
			member1 = chattingRoomEntity.getMember2();
			member2 = chattingRoomEntity.getMember1();
		}

		MessageEntity messageEntity = MessageEntity.builder().sender(member1).receiver(member2)
				.chattingRoomEntity(chattingRoomEntity).isRead(true).build();

		if (flag.equals("enterRoom")) { // 채팅방을 재입장하였을 경우(나간 뒤)
			messageEntity.setMessageContent(member1.getUserid() + "님이 입장하였습니다");
			messageEntity.setStatusBar(true);
		} else if (flag.equals("exitRoom")) { // 채팅방을 나갔을 경우
			messageEntity.setMessageContent(member1.getUserid() + "님이 나갔습니다");
			messageEntity.setStatusBar(true);
		}

//		MessageDTO messageDTO = messageEntity.convertToDTO(messageEntity);
		messageRepository.save(messageEntity);
	}

	// messageIndex부터 추출하는 메시지 리스트 나열
	public List<MessageEntity> getMessagesFromIndex(List<MessageEntity> messages, Long messageIndex) {
		if (messages == null || messages.isEmpty()) {
			return List.of(); // 빈 리스트 반환
		}

		int index;
		try {
			index = Math.toIntExact(messageIndex); // 안전한 Long -> int 변환
		} catch (ArithmeticException e) {
			return List.of(); // 변환 실패 시 빈 리스트 반환
		}

		if (index < 0 || index >= messages.size()) {
			return List.of(); // 범위를 벗어나면 빈 리스트 반환
		}

		return new ArrayList<>(messages.subList(index, messages.size())); // 새로운 리스트 반환
	}

	@Transactional
	public List<ChattingRoomDTO> searchRooms(Long loggedId, String searchContent) {
		List<ChattingRoomEntity> chattingRoomEntities = chattingRoomRepository.findAllByLoggedMember(loggedId);

		if (chattingRoomEntities.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		MemberEntity loggedMember = memberRepository.findById(loggedId).get();
		String loggedUserId = loggedMember.getUserid();

		List<ChattingRoomDTO> chattingRoomDTOs = chattingRoomEntities
				.stream().filter(
						entity -> (entity.getExitedmemberId() == null || !entity.getExitedmemberId().equals(loggedId))
								&& searchContent.contains(entity.getBoardEntity().getTitle())
								&& (entity.getMember1().getId().equals(loggedId)
										|| entity.getMember2().getId().equals(loggedId)))
				.map((ChattingRoomEntity entity) -> {
					// Log member1 and member2 user IDs
					String member1UserId = entity.getMember1() != null ? entity.getMember1().getUserid() : null;
					String member2UserId = entity.getMember2() != null ? entity.getMember2().getUserid() : null;

					// Log boardId
					String boardId = entity.getBoardEntity() != null ? String.valueOf(entity.getBoardEntity().getId())
							: null;

					// Log messages inside the ChattingRoomDTO
					List<MessageEntity> messageEntities = messageRepository.findByChattingRoomEntity(entity.getId());
					MessageDTO recentMessage = this.recentRoomMessage(entity.getId(), loggedId);

					return ChattingRoomDTO.builder().id(entity.getId()).title(entity.getTitle())
							.price(entity.getPrice()).createTime(recentMessage.getSendTime()) // Sorting 기준 필드
							.liked(entity.isLiked()).member1UserId(member1UserId).member2UserId(member2UserId)
							.boardId(boardId).member1Visible(entity.getMember1Visible())
							.member2Visible(entity.getMember2Visible()).messageIndex1(entity.getMessageIndex1())
							.messageIndex2(entity.getMessageIndex2()).exitedmemberId(entity.getExitedmemberId())
							.recentExitedmemberId(entity.getRecentExitedmemberId()).build();
				})
				.sorted(Comparator.comparing(ChattingRoomDTO::getCreateTime,
						Comparator.nullsLast(Comparator.reverseOrder()))) // 최근 순 정렬
				.collect(Collectors.toList());
		return chattingRoomDTOs;
	}

	@Transactional
	public List<MessageDTO> searchMessage(Long loggedId, String searchContent) {

		List<ChattingRoomEntity> chattingRoomEntities = chattingRoomRepository.findAllByLoggedMember(loggedId);

		// messageEntities를 빈 리스트로 초기화
		List<MessageEntity> messageEntities = new ArrayList<>();

		// 채팅방이 없으면 빈 리스트 반환
		if (chattingRoomEntities.isEmpty()) {
			return Collections.emptyList();
		}

		return chattingRoomEntities.stream().flatMap(room -> {
			List<MessageEntity> messageEntitiesByRoom = messageRepository.findByChattingRoomEntity(room.getId());

			Long messageIndex = null;
			if (room.getMember1().getId().equals(loggedId)) {
				messageIndex = room.getMessageIndex1();
			} else if (room.getMember2().getId().equals(loggedId)) {
				messageIndex = room.getMessageIndex2();
			}

			// 유효한 메시지만 필터링
			return getMessagesFromIndex(messageEntitiesByRoom, messageIndex).stream()
					.filter(message -> (message.getMessageContent().contains(searchContent)
							|| message.getSender().getUserid().contains(searchContent))
							&& !Boolean.TRUE.equals(message.getStatusBar()))
					.map(message -> new MessageDTO(message.getId(),
							message.getSender().getProfileImage() != null ? message.getSender().getProfileImage()
									: "/boardimage/nullimage.jpg",
							message.getSender().getUserid(), message.getMessageContent(), room.getId(), // room을 여기서 참조
							message.getStatusBar(), message.getSendTime(),message.getExpired()));
		}).collect(Collectors.toList());
	}

	@Transactional
	public ChattingRoomDTO findBookingRoom(Long boardId, Long loggedId) {
		ChattingRoomEntity chattingRoomEntity = null;
		BoardEntity boardEntity = boardRepository.findById(boardId).orElse(null);
		if (boardEntity == null) {
			return null;
		}

		TradeDTO filteredTrade = boardEntity.getTrades().stream()
				.filter(trade -> trade.getBooking1() && trade.getBooking2())
				.map(trade -> new TradeDTO().fromEntity(trade)).findFirst().orElse(null);

//			if(loggedId.equals(filteredTrade.getMember2Id())) {				
//				 chattingRoomEntity = chattingRoomRepository.findEnableRoom(filteredTrade.getMember1Id(), filteredTrade.getMember2Id(), boardId);
//			}else if(loggedId.equals(filteredTrade.getMember1Id())) {
//				 chattingRoomEntity = chattingRoomRepository.findEnableRoom(filteredTrade.getMember2Id(), filteredTrade.getMember1Id(), boardId);
//			}

		chattingRoomEntity = chattingRoomRepository.findEnableRoom(filteredTrade.getMember1Id(),
				filteredTrade.getMember2Id(), boardId);
		if (chattingRoomEntity == null) {
			chattingRoomEntity = chattingRoomRepository.findEnableRoom(filteredTrade.getMember2Id(),
					filteredTrade.getMember1Id(), boardId);
		}
		if (chattingRoomEntity == null) {
			return null;
		}
		return ChattingRoomDTO.builder().id(chattingRoomEntity.getId())
				.member1UserId(chattingRoomEntity.getMember1().getUserid())
				.member2UserId(chattingRoomEntity.getMember2().getUserid())
				.member1Visible(chattingRoomEntity.getMember1Visible())
				.member2Visible(chattingRoomEntity.getMember2Visible())
				.exitedmemberId(chattingRoomEntity.getExitedmemberId())
				.recentExitedmemberId(chattingRoomEntity.getRecentExitedmemberId())
				.messageIndex1(chattingRoomEntity.getMessageIndex1())
				.messageIndex2(chattingRoomEntity.getMessageIndex2())
				.boardId(String.valueOf(chattingRoomEntity.getBoardEntity().getId()))
				.createTime(chattingRoomEntity.getCreateTime()).build();

	}

	@Transactional
	public ChattingRoomDTO findRoomByBoardIdAndMemberId(Long boardId, Long member1Id, Long member2Id, Long loggedId) {

		boolean memberFlag = true;
		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findEnableRoom(member1Id, member2Id, boardId);

		if (chattingRoomEntity == null) {
			chattingRoomEntity = chattingRoomRepository.findEnableRoom(member2Id, member1Id, boardId);
			memberFlag = false;
		}

		BoardEntity boardEntity = chattingRoomEntity.getBoardEntity();

		ChattingRoomDTO chattingRoomDTO = new ChattingRoomDTO();

		return chattingRoomDTO.builder().id(chattingRoomEntity.getId())
				.member1UserId(chattingRoomEntity.getMember1().getUserid())
				.member2UserId(chattingRoomEntity.getMember2().getUserid()).boardId(String.valueOf(boardEntity.getId()))
				.member1Visible(chattingRoomEntity.getMember1Visible())
				.member2Visible(chattingRoomEntity.getMember2Visible())
				.exitedmemberId(chattingRoomEntity.getExitedmemberId())
				.recentExitedmemberId(chattingRoomEntity.getRecentExitedmemberId()).title(chattingRoomEntity.getTitle())
				.price(chattingRoomEntity.getPrice()).messageIndex1(chattingRoomEntity.getMessageIndex1())
				.messageIndex2(chattingRoomEntity.getMessageIndex2()).createTime(chattingRoomEntity.getCreateTime())
				.build();

	}

	@Transactional			//메시지 내부 버튼 기능 만료
	public void setMessageExpired(ChattingRoomEntity chattingRoomEntity) {
			List<MessageEntity> messages = messageRepository.findByChattingRoomEntity(chattingRoomEntity.getId());
				for(MessageEntity message : messages) {
					message.setExpired(true);
				}
		
	}

}
