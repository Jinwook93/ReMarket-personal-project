package com.cos.project.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.aspectj.weaver.ast.Instanceof;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.cos.project.dto.AlarmDTO;
import com.cos.project.dto.ChattingRoomDTO;
import com.cos.project.dto.PagedResponse;
import com.cos.project.dto.TradeDTO;
import com.cos.project.entity.AlarmEntity;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.ChattingRoomEntity;
import com.cos.project.entity.CommentLikeEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.entity.MessageEntity;
import com.cos.project.entity.TradeEntity;
import com.cos.project.repository.AlarmRepository;
import com.cos.project.repository.BoardLikeRepository;
import com.cos.project.repository.BoardRepository;
import com.cos.project.repository.ChattingRoomRepository;
import com.cos.project.repository.CommentLikeRepository;
import com.cos.project.repository.CommentRepository;
import com.cos.project.repository.MemberRepository;
import com.cos.project.repository.MessageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmService {

	private final AlarmRepository alarmRepository;
	private final ChattingRoomRepository chattingRoomRepository;
	private final MessageRepository messageRepository;
	private final MemberRepository memberRepository;
	private final BoardRepository boardRepository;
	private final CommentRepository commentRepository;
	private final CommentLikeRepository commentLikeRepository;
	private final BoardLikeRepository boardLikeRepository;
	
	
	// 내 기준

	@Transactional // 알림목록 추출
	public List<AlarmEntity> findAllAboutLoggedId(Long loggedId) {

		// member1,member2가 loggedId인 알림 목록을 출력
		List<AlarmEntity> alarms = alarmRepository.findByLoggedId(loggedId);

		return alarms.isEmpty() ? Collections.EMPTY_LIST : alarms;
	}

	@Transactional // 알림 갯수 비교 //새로운 알람 메시지
	public String NewAlarmMessage(Long loggedId, Long member1Id, Long member2Id, Long loggedIdAlarmCount) {

		String message = "";
		List<AlarmEntity> alarms = alarmRepository.findByLoggedId(loggedId); // DB에서의 사용자의 알람 갯수 조회
		Long alarmCount = (long) alarms.size(); // 사용자가 가진 알람
		List<AlarmEntity> unReadAlarms = Collections.EMPTY_LIST;
		if (loggedIdAlarmCount.equals(alarmCount)) { // 기존 알람 갯수와 DB의 알람 갯수 비교
			if (alarmCount > loggedIdAlarmCount) {
				if (loggedId.equals(member1Id)) {
					unReadAlarms = alarms.stream().filter(alarm -> alarm.getMember1Read().equals("UNREAD"))
							.collect(Collectors.toList());
				} else if (loggedId.equals(member2Id)) {
					unReadAlarms = alarms.stream().filter(alarm -> alarm.getMember2Read().equals("UNREAD"))
							.collect(Collectors.toList());
				}

				message = "새로운 알람이 " + (unReadAlarms.size() + alarmCount - loggedIdAlarmCount) + "개 도착했습니다";
			}

		}
		return message;
	}

	@Transactional // 사용자 별 알람 갯수 추출
	public Long findCount(Long loggedId) {

		// member1나 member2가 loggedId인 알림 목록을 출력
		List<AlarmEntity> alarms = alarmRepository.findByLoggedId(loggedId);

		return alarms.isEmpty() ? 0 : (long) alarms.size();
	}

	@Transactional // 알람 등록
	public AlarmEntity postAlarm(Long loggedId, Long member1Id, Long member2Id, String type, String childType,
			String object, String action, String priority) {
		AlarmDTO alarmDTO = null;
		AlarmEntity alarmEntity = null;
		// Case 1: When both member1Id and member2Id are null, it's a login alarm
		if (member1Id == null && member2Id == null) {
			member1Id = loggedId;
			alarmDTO = alarmConstructor(loggedId, member1Id, member2Id, type, childType, object, action, priority);
			MemberEntity member1 = memberRepository.findById(member1Id)
					.orElseThrow(() -> new IllegalAccessError("사용자를 조회할 수 없습니다"));
			MemberEntity member2 = null; // No member2 in this case (login alarm)
			alarmEntity = alarmDTO.toEntity(member1, member2);

		} else {
			// Case 2: Both member1Id and member2Id are provided
			MemberEntity member1 = memberRepository.findById(member1Id)
					.orElseThrow(() -> new IllegalAccessError("사용자를 조회할 수 없습니다"));
			MemberEntity member2 = memberRepository.findById(member2Id)
					.orElseThrow(() -> new IllegalAccessError("사용자를 조회할 수 없습니다"));

//	        alarmDTO = alarmConstructor(loggedId, member1Id, member2Id, type, childType, object, action, priority);
//            alarmEntity = alarmDTO.toEntity(member1, member2);

			if (loggedId.equals(member1Id)) {
				alarmDTO = alarmConstructor(loggedId, member1Id, member2Id, type, childType, object, action, priority);
				alarmEntity = alarmDTO.toEntity(member1, member2);
			} else if (loggedId.equals(member2Id)) {
				alarmDTO = alarmConstructor(loggedId, member2Id, member1Id, type, childType, object, action, priority);
				alarmEntity = alarmDTO.toEntity(member2, member1);
			} else {
				// You can handle any additional cases where neither member1 nor member2 matches
				// the logged-in user
				throw new IllegalArgumentException("Logged user doesn't match any member IDs.");
			}
		}
		alarmEntity.setMember1Read("UNREAD");
		alarmEntity.setMember2Read("UNREAD");
		// Save the alarm entity to the database
		alarmRepository.saveAndFlush(alarmEntity);
		return alarmEntity;
	}

	@Transactional // 알림 숨김 (삭제)
	public boolean hideAlarm(Long alarmId, Long loggedId, Long member1Id, Long member2Id) {
		MemberEntity member1 = memberRepository.findById(member1Id).get();
		if(member1Id != null && member2Id == null) {
			MemberEntity member2 = null;
			
			}
		else if(member1Id != null && member2Id != null) {
		MemberEntity member2 = memberRepository.findById(member2Id).get();
		}
		AlarmEntity alarmEntity = alarmRepository.findById(alarmId).get(); // 알람조회
		if (loggedId.equals(member1Id)) {
			alarmEntity.setMember1Visible(false);
			alarmEntity.setMember1Read("READ");
		} else if (loggedId.equals(member2Id)) {
			alarmEntity.setMember2Visible(false);
			alarmEntity.setMember2Read("READ");
		}

		alarmRepository.saveAndFlush(alarmEntity);

		return true;
	}

	
	
	//모든 알람 삭제 (숨김)
	@Transactional
	public void deleteAllAlarms(Long loggedId) {
		
		List<AlarmEntity> alarms = alarmRepository.findByLoggedId(loggedId);
		
		alarms.forEach(alarm ->{
			if(alarm.getMember1().getId().equals(loggedId)){
				alarm.setMember1Visible(false);
				alarm.setMember1Read("READ");
			}else if(alarm.getMember2().getId().equals(loggedId)){
				alarm.setMember2Visible(false);
				alarm.setMember2Read("READ");
			}
				
		});
		
		
	}
	
	
	
	
	
	
	
	
//	@Transactional // 알림 읽음
//	public void readAlarm(Long alarmId, Long loggedId, Long member1Id, Long member2Id) {
//		MemberEntity member1 = memberRepository.findById(member1Id).get();
//		MemberEntity member2 = memberRepository.findById(member2Id).get();
//		AlarmEntity alarmEntity = alarmRepository.findById(alarmId).get(); // 알람조회
//		if (loggedId.equals(member1Id)) {
//			alarmEntity.setMember1Read("READ");
//		} else if (loggedId.equals(member2Id)) {
//			alarmEntity.setMember2Read("READ");
//		}
//		alarmRepository.saveAndFlush(alarmEntity);
//
//	}

//	@Transactional // 알림 읽음
//	public void readAlarm(Long alarmId, Long loggedId, Long member1Id, Long member2Id) {
//		MemberEntity member1 = memberRepository.findById(member1Id).get();
//		MemberEntity member2 = memberRepository.findById(member2Id).get();
//		AlarmEntity alarmEntity = alarmRepository.findById(alarmId).get(); // 알람조회
//		if (loggedId.equals(member1Id)) {
//			alarmEntity.setMember1Read("READ");
//		} else if (loggedId.equals(member2Id)) {
//			alarmEntity.setMember2Read("READ");
//		}
//		alarmRepository.saveAndFlush(alarmEntity);
//
//	}

	@Transactional // 알림 읽음
	public void readAlarm(Long alarmId, Long loggedId) {
		MemberEntity member = memberRepository.findById(loggedId).get();
		AlarmEntity alarmEntity = alarmRepository.findById(alarmId).get(); // 알람조회

		if (loggedId.equals(alarmEntity.getMember1().getId())) {
			alarmEntity.setMember1Read("READ");
		} else if (loggedId.equals(alarmEntity.getMember2().getId()) && alarmEntity.getMember2().getId() != null) {
			alarmEntity.setMember2Read("READ");
		}
		alarmRepository.saveAndFlush(alarmEntity);
		alarmRepository.flush();
	}

	@Transactional // 모든 알림 읽음 설정
	public void markAllAsRead(Long loggedId) {
		List<AlarmEntity> alarms = alarmRepository.findByLoggedId(loggedId);

		for (AlarmEntity alarmEntity : alarms) {
			if (loggedId.equals(alarmEntity.getMember1().getId())) {
				alarmEntity.setMember1Read("READ");
			} else if (loggedId.equals(alarmEntity.getMember2().getId()) && alarmEntity.getMember2().getId() != null) {
				alarmEntity.setMember2Read("READ");
			}
		}

		// 한 번에 저장
		alarmRepository.saveAll(alarms);
		alarmRepository.flush(); // 강제 플러시

	}

	@Transactional
	public AlarmDTO alarmConstructor(Long loggedId, Long member1Id, Long member2Id, String type, String childType,
			String object, String action, String priority) {
		Optional<MemberEntity> member1 = memberRepository.findById(member1Id);
		Optional<MemberEntity> member2 = null;
//		Long Object_id = null;
		if (member2Id != null) {
			member2 = memberRepository.findById(member2Id);
		}
		String member1Content = "";
		String member2Content = "";

		Boolean member1Visible = true;
		Boolean member2Visible = true;
		
		String member1Read ="UNREAD";
		String member2Read ="UNREAD";

		if (type.equals("LOGIN")) {
			member1Content = "로그인에 성공하였습니다";
			member2Visible = false;
		}

		else if (type.equals("BOARD")) { // 게시판
			if (childType.equals("게시판") && action.equals("등록")) {
				List<BoardEntity> boards = boardRepository.findByMemberID(member1Id);
				BoardEntity filteredBoard = boards.stream()
						.filter(board -> board.getMemberEntity().getId().equals(member1Id))
						.max(Comparator.comparing(BoardEntity::getCreateTime)).get();

				if (!filteredBoard.getId().equals(null)) {
					member1Content = member1.get().getNickname() + "님이 " + filteredBoard.getId() + "번 게시물을 등록하였습니다";
				}
			} else if (childType.equals("게시판") && action.equals("삭제")) {
				if (member2Id == null) {
//					member1Content = member1.get().getNickname() + "님의 " + object + "번 게시글이 삭제되었습니다";
					member1Content = member1.get().getNickname() + "님의 " + object + "번 게시글이 삭제되었습니다";
				} else if (member2Id != null) { // 거래가 완료되었을 경우
					ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(Long.valueOf(object)).get();
					Long boardId = chattingRoomEntity.getBoardEntity().getId();
					member1Content = member1.get().getNickname() + "님의 " + String.valueOf(boardId) + "번 게시글이 삭제되었습니다";
				member2Content = "(숨김)"+member1.get().getNickname() + "님의 "+ String.valueOf(boardId) +"번 게시글이 삭제되었습니다";
//					member2Content = "숨김";
				}

			} else if (childType.equals("게시판") && action.equals("수정")) {
				member1Content = member1.get().getNickname() + "님의 " + object + "번 게시글이 수정되었습니다";
			} else if (childType.equals("게시판") && object.equals("좋아요") && action.equals("활성화")) {
				member2Content = member1.get().getNickname() + "님이 " + member2.get().getNickname() + "님의 게시글을 좋아합니다";
				member1Content = member2.get().getNickname() + "님의 게시글에 좋아요를 눌렀습니다";
			} else if (childType.equals("게시판") && object.equals("좋아요") && action.equals("취소")) {
				member2Content = member1.get().getNickname() + "님이 " + member2.get().getNickname()
						+ " 님의 게시글의 좋아요를 취소하였습니다";
				member1Content = member2.get().getNickname() + "님의 게시글에 좋아요를 취소하였습니다";
			} else if (childType.equals("게시판") && object.equals("싫어요") && action.equals("활성화")) {
				member2Content = member1.get().getNickname() + "님이 " + member2.get().getNickname() + " 님의 게시글을 싫어합니다";
				member1Content = member2.get().getNickname() + "님의 게시글에 싫어요를 눌렀습니다";
			} else if (childType.equals("게시판") && object.equals("싫어요") && action.equals("취소")) {
				member2Content = member1.get().getNickname() + "님이 " + member2.get().getNickname()
						+ " 님의 게시글의 싫어요를 취소하였습니다";
				member1Content = member2.get().getNickname() + "님의 게시글에 싫어요를 취소하였습니다";
			}

			else if (childType.equals("댓글") && action.equals("등록")) {// member1, member2 의 정보를 다 받아와야 함
				member1Content = member1.get().getNickname() + " 님의 댓글이 등록되었습니다";
				member2Content = member1.get().getNickname() + " 님의 댓글이 " + member2.get().getNickname() + "님의 " + object
						+ "번 게시글에 등록되었습니다";
				if (member1Id.equals(member2Id)) {
					member1Content = member1.get().getNickname() + " 님의 댓글이 등록되었습니다";
					member2Content = member1.get().getNickname() + " 님의 댓글이 등록되었습니다";
				}

			} else if (childType.equals("댓글") && action.equals("삭제")) {
				member1Content = member1.get().getNickname() + " 님의 댓글이 삭제되었습니다";
				member2Content = member1.get().getNickname() + " 님의 댓글이 " + member2.get().getNickname() + "님의 " + object
						+ "번 게시글에서 삭제되었습니다";
				if (member1Id.equals(member2Id)) {
					member1Content = member1.get().getNickname() + " 님의 댓글이 삭제되었습니다";
					member2Content = member1.get().getNickname() + " 님의 댓글이 삭제되었습니다";
				}

			} else if (childType.equals("댓글") && action.equals("수정")) {
				member1Content = member1.get().getNickname() + " 님의 댓글이 수정되었습니다";
				member2Content = member1.get().getNickname() + " 님의 댓글이 수정되었습니다";
			} else if (childType.equals("댓글") && object.equals("좋아요") && action.equals("활성화")) { // 3자
				member1Content = member2.get().getNickname() + "님의 댓글에 좋아요를 눌렀습니다";
				member2Content = member1.get().getNickname() + " 님이 " + member2.get().getNickname()
						+ "님의 댓글의 좋아요를 눌렀습니다";
			} else if (childType.equals("댓글") && object.equals("좋아요") && action.equals("취소")) {
				member1Content = member2.get().getNickname() + "님의 댓글에 좋아요를 취소하였습니다";
				member2Content = member1.get().getNickname() + " 님이 " + member2.get().getNickname()
						+ "님의 댓글의 좋아요를 취소하였습니다";

			} else if (childType.equals("댓글") && object.equals("싫어요") && action.equals("활성화")) {
				member1Content = member2.get().getNickname() + "님의 댓글에 싫어요를 눌렀습니다";
				member2Content = member1.get().getNickname() + " 님이 " + member2.get().getNickname() + "님의 댓글을 싫어합니다";

			} else if (childType.equals("댓글") && object.equals("싫어요") && action.equals("취소")) {
				member1Content = member1.get().getNickname() + " 님의 댓글에 싫어요를 취소하였습니다";
				member2Content = member1.get().getNickname() + " 님이 " + member2.get().getNickname()
						+ "님의 댓글의 싫어요를 취소하였습니다";
			}

			else if (childType.equals("댓글") && action.equals("블라인드")) {
				member1Content = member2.get().getNickname() + "  님의 댓글을 블라인드 하였습니다";
				member2Content = member1.get().getNickname() + " 님이 " + member2.get().getNickname() + "  님의 " + object
						+ "번 댓글을 블라인드하였습니다";
			} else if (childType.equals("댓글") && action.equals("블라인드 취소")) {
				member1Content = member2.get().getNickname() + "  님의 댓글을 블라인드 해제하였습니다";
				member2Content = member1.get().getNickname() + " 님이 " + member2.get().getNickname() + "  님의 " + object
						+ "번 댓글을 블라인드 해제하였습니다";
			}

			else if (childType.equals("대댓글") && action.equals("등록")) {// member1, member2 의 정보를 다 받아와야 함
				member1Content = member1.get().getNickname() + "님의 댓글이 등록되었습니다";
				member2Content = member1.get().getNickname() + "님이 " + member2.get().getNickname() + "님의 댓글에 답하였습니다";
			} else if (childType.equals("대댓글") && action.equals("삭제")) {
				member1Content = member1.get().getNickname() + "님의 댓글이 삭제되었습니다";
				member2Content = member1.get().getNickname() + "님의 댓글이 " + member2.get().getNickname()
						+ "님의 댓글에서 삭제되었습니다";
				member2Visible = false;
			} else if (childType.equals("대댓글") && action.equals("수정")) {
				member1Content = member1.get().getNickname() + "님의 댓글이 수정되었습니다";
				member2Content = member1.get().getNickname() + "님의 댓글이 수정되었습니다";
			} else if (childType.equals("대댓글") && object.equals("좋아요") && action.equals("활성화")) { // 3자
				member1Content = member2.get().getNickname() + "님의 댓글에 좋아요를 눌렀습니다";
				member2Content = member1.get().getNickname() + "님이 " + member2.get().getNickname()
						+ "님의 댓글의 좋아요를 눌렀습니다";

			} else if (childType.equals("대댓글") && object.equals("좋아요") && action.equals("취소")) {
				member1Content = member2.get().getNickname() + "님의 댓글에 좋아요를 취소하였습니다";
				member2Content = member1.get().getNickname() + "님이 " + member2.get().getNickname()
						+ "님의 댓글의 좋아요를 취소하였습니다";
			} else if (childType.equals("대댓글") && object.equals("싫어요") && action.equals("활성화")) {
				member1Content = member2.get().getNickname() + "님의 댓글에 싫어요를 눌렀습니다";
				member2Content = member1.get().getNickname() + "님이 " + member2.get().getNickname() + "님의 댓글을 싫어합니다";

			} else if (childType.equals("대댓글") && object.equals("싫어요") && action.equals("취소")) {
				member1Content = member2.get().getNickname() + "님의 댓글에 싫어요를 취소하였습니다";
				member2Content = member1.get().getNickname() + "님이 " + member2.get().getNickname()
						+ "님의 댓글의 싫어요를 취소하였습니다";

			}

		}

		else if (type.equals("MESSAGE")) { // 메시지
			if (childType.equals("채팅방") && action.equals("채팅방 만듬")) {
				member1Content = member1.get().getNickname() + "님과 " + member2.get().getNickname() + "님의 채팅방이 등록되었습니다";
				member2Content = member2.get().getNickname() + " 님과 " + member1.get().getNickname() + "님의 채팅방이 등록되었습니다";
				member2Visible = false;
			} else if (childType.equals("채팅방") && action.equals("나가기")) {
				member1Content = member1.get().getNickname() + "님과 " + member2.get().getNickname() + "님의  채팅방을 나갔습니다";
				member2Content = member1.get().getNickname() + "님이 " + object + "번 채팅창을 나갔습니다";
			} else if (childType.equals("채팅방") && action.equals("완전삭제")) {
				member1Content = member1Content = member1.get().getNickname() + "님과 " + member2.get().getNickname()
						+ "님의 채팅창이 삭제되었습니다";
				member2Content = member1Content = member2.get().getNickname() + "님과 " + member1.get().getNickname()
						+ "님의 채팅창이 삭제되었습니다";
				member2Visible = false;
			
			} else if (childType.equals("채팅방") && action.equals("재입장")) {
				member1Content = member2.get().getNickname() + "님의 채팅방에 재입장하였습니다";
				member2Content = member1.get().getNickname() + "님이 재입장하였습니다";
			}

			else if (childType.equals("메시지") && action.equals("송수신")) {
				ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(Long.valueOf(object))
						.orElse(null);

				member1Content = member2.get().getNickname() + "님에게 메시지를 보냈습니다";
				member2Content = "새로운 메시지 : " + member1.get().getNickname();

				if (chattingRoomEntity.getExitedmemberId() == member2Id) {
					member1Content = "⚠️" + member2.get().getNickname() + "님은 이미 나간 유저이므로 메시지를 보낼 수 없습니다";
					member2Visible = false;
				}
			} else if (childType.equals("메시지") && action.equals("삭제")) {
			member1Content = "(숨김)"+member1.get().getNickname()+"님의 메시지가 삭제되었습니다";
			member2Content = "(숨김)"+ member1.get().getNickname() + "님의 메시지가 삭제되었습니다";
//				member1Content = "숨김";
//				member2Content = "숨김";

			}

			else if (childType.equals("좋아요") && action.equals("활성화")) {
				member1Content = 	"(숨김)"+member2.get().getNickname() + "님의 메시지에 좋아요를 눌렀습니다";
				member2Content ="(숨김)"+ member1.get().getNickname()+"님이 " + member2.get().getNickname() + "님의 메시지를 좋아합니다";
//				member1Content = "숨김";
//				member2Content = "숨김";
			} else if (childType.equals("좋아요") && action.equals("취소")) {
				member1Content = "(숨김)"+	member2.get().getNickname() + "님의 메시지 좋아요를 취소했습니다";
				member2Content = "(숨김)"+member1.get().getNickname()+"님이 " + member2.get().getNickname() + "님의 메시지 좋아요를 취소하였습니다";
//				member1Content = "숨김";
//				member2Content = "숨김";
			}

		}

		else if (type.equals("TRADE")) { // 거래
			BoardEntity board = null;
				if(object != null) {
					board = boardRepository.findById(Long.valueOf(object)).get() ;	
			}
					
			if (childType.equals("거래") && action.equals("상대방 동의 확인")) {
				member1Content = member2.get().getNickname() + "님에게 거래신청을 보냈습니다";
				member2Content = member1.get().getNickname() + " 님이 거래를 희망합니다. 거래하시겠습니까?";
			} else if (childType.equals("거래") && action.equals("거래수락")) {
				member1Content = member1.get().getNickname() + "님이 거래를 수락했습니다. 거래상태가 거래 중으로 전환됩니다";
				member2Content = member1.get().getNickname() + "님과의 거래를 수락했습니다. 거래상태가 거래 중으로 전환됩니다";

			} else if (childType.equals("거래") && action.equals("거래거절")) {
				member1Content = member1.get().getNickname() + "님이 거래를 거절했습니다.";
				member2Content = member2.get().getNickname() + "님과의 거래를 거절했습니다.";

			} else if (childType.equals("거래") && action.equals("예약")) {
				member1Content = member2.get().getNickname() + "님에게 예약신청을 보냈습니다";
				member2Content = member1.get().getNickname() + " 님이 예약를 희망합니다. 수락하시겠습니까?";
			} else if (childType.equals("거래") && action.equals("예약수락")) {
				member1Content = member2.get().getNickname() + "님과의 예약을 수락했습니다.";
				member2Content = member1.get().getNickname() + "님이 예약을 수락했습니다.";

			} else if (childType.equals("거래") && action.equals("예약거절")) {
				member1Content = member1.get().getNickname() + "님이 예약을 거절했습니다.";
				member2Content = member2.get().getNickname() + "님과의 예약을 거절했습니다.";

			} else if (childType.equals("거래") && action.equals("예약상태변경")) {
				member1Content = member2.get().getNickname() + "님과의 거래예약을 거래 중으로 전환하였습니다 .";
				member2Content = member1.get().getNickname() + "님이 거래예약를 거래 중으로 전환하였습니다";
			}

			else if (childType.equals("거래") && action.equals("거래 완료 확인")) {
				BoardEntity boardEntity = boardRepository.findById(Long.valueOf(object)).orElse(null);
				List<TradeEntity> trades = boardEntity.getTrades();

				TradeDTO tradeDTO = trades.stream()
						.filter(trade -> Boolean.FALSE.equals(trade.getCompleted1())
								&& Boolean.TRUE.equals(trade.getCompleted2()))
						.findFirst().map(trade -> new TradeDTO().fromEntity(trade)).orElse(null);
				member1Content = member2.get().getNickname() + "님에게 거래완료 신청을 보냈습니다";
//						member2Content = member1.get().getNickname()  + " 님이 거래완료를 희망합니다. 거래를 마치시겠습니까? <div class='messageButtonSelect'><button id='complete1-Sell-" + tradeDTO.getId() + "'>거래완료</button></div>";

								member2Content = member1.get().getNickname()
						+ " 님이 거래완료를 희망합니다. 거래를 마치시겠습니까? <button id='complete1-Sell-" + tradeDTO.getId()
						+ "'>거래완료</button>";
				
				
//				member2Content = member1.get().getNickname()
//					    + " 님이 거래완료를 희망합니다. 거래를 마치시겠습니까? "
//					    + "<button id='complete1-Sell-" + tradeDTO.getId() + "' "
//					    + "style=\"display: ${alarm.expired === true ? 'none' : 'block'}\">"
//					    + "거래완료</button>";

				
			} else if (childType.equals("거래") && action.equals("거래완료")) {
//				member1Content = member2.get().getNickname() + "님과 " + object + " 번 게시판 거래를 완료하였습니다";
				member1Content = member2.get().getNickname() + "님과 " + board.getTitle() +" 거래를 완료하였습니다";
				member2Content = member1.get().getNickname() + "님이 거래완료를 수락하였습니다." + member1.get().getNickname()
						+ " 님과의 " + object + " 번 게시판 거래를 완료하였습니다";
			}

			else if (childType.equals("거래") && action.equals("거래취소")) {
//				member1Content = member2.get().getNickname() + "님과 " + object + " 번 게시판 거래를 취소하였습니다";
				member1Content = member2.get().getNickname() + "님과 '"  + board.getTitle() +"'  거래를 취소하였습니다";
				member2Content = member1.get().getNickname() + "님이 거래를 취소하였습니다";
			}

			else if (childType.equals("거래") && action.equals("거래불가")) {
//				member1Content = member2.get().getNickname() + "님과 " + object + " 번 게시판 거래를 취소하였습니다";
				member1Content = "(숨김)"+member2.get().getNickname() + "님과 ' " + board.getTitle() + "'  거래를 거절하였습니다";
//				member2Content ="신청하신 "+board.getTitle()+" 거래는 다른 사용자와 진행되어 거래가 불가능합니다."+ "<br><hr><button class='small-btn searchOtherProduct-"+ board.getCategory().name()  +">다른 상품도 확인해보세요!</button>";
				member2Content = "신청하신 " + board.getTitle() + 
					    " 거래는 다른 사용자와 진행되어 거래가 불가능합니다." + 
					    "<br><hr>" + 
					    "<button class='small-btn searchOtherProduct-" + board.getCategory().name() + 
					    "' onclick=\"location.href='/board/list?condition=2&category=" + board.getCategory().name() + "'\">" +
					    "다른 상품도 확인해보세요!</button>";

				
				//				member1Visible = false;
//				member1Read = "READ";
			}
			
			else if (childType.equals("거래") && action.equals("다시 거래 가능")) {
//				member1Content = member2.get().getNickname() + "님과 " + object + " 번 게시판 거래를 취소하였습니다";
				member1Content = "(숨김)"+member2.get().getNickname() + "님과 ' " + board.getTitle() + "'  거래가 다시 가능합니다";
				member2Content = member2.get().getNickname() + "님과 ' " + board.getTitle() + "'  거래가 다시 가능합니다";
			//	member1Visible = false;
//				member1Read = "READ";
			}
			
			
		}

		if (member1Id.equals(member2Id)) { // 메시지가 양쪽 다 뜨는 것을 방지
			member2Visible = false;
//			member2Read = "READ";
		}

		AlarmDTO alarmDTO = new AlarmDTO().builder().action(action).type(childType).object(object).type(type)
				.childType(childType).member1Id(member1Id).member2Id(member2Id).member1Content(member1Content)
				.member2Content(member2Content).member1Visible(member1Visible).member2Visible(member2Visible)
				.priority("MEDIUM").expired(Boolean.FALSE).member1Read(member1Read).member1Read(member2Read).build();

		return alarmDTO;
	}

	@Transactional
	public PagedResponse<AlarmDTO> getUserAlarms(Long loggedId, Pageable pageable) {
	    // 사용자의 알림을 페이지네이션하여 조회
	    Page<AlarmEntity> alarmEntities = alarmRepository.findByLoggedId(loggedId, pageable);

	    // 조회된 AlarmEntity 목록을 AlarmDTO로 변환
	    List<AlarmDTO> alarms = alarmEntities.getContent().stream()
	            .map(alarm -> new AlarmDTO(
	                    alarm.getId(),                          // 알림 ID
	                    alarm.getMember1Content(),             // 회원1이 보낸 알림 내용
	                    alarm.getMember2Content(),             // 회원2가 보낸 알림 내용
	                    alarm.getMember1() != null ? alarm.getMember1().getId() : null, // member1Id (회원1 ID, 없으면 null)
	                    alarm.getMember2() != null ? alarm.getMember2().getId() : null, // member2Id (회원2 ID, 없으면 null)
	                    alarm.getMember1Visible(),             // 회원1에게 보이는 여부
	                    alarm.getMember2Visible(),             // 회원2에게 보이는 여부
	                    alarm.getCreateTime(),                 // 알림 생성 시간
	                    alarm.getType(),                       // 알림 타입
	                    alarm.getChildType(),                  // 세부 알림 타입
	                    alarm.getObject(),                     // 관련 객체 정보
	                    alarm.getAction(),                     // 수행된 액션 정보
	                    alarm.getMember1Read(),                // 회원1이 읽었는지 여부
	                    alarm.getMember2Read(),                // 회원2가 읽었는지 여부
	                    alarm.getPriority(),                   // 알림 우선순위
	                    null,                                  // targetId가 필요하면 추가, 현재는 null 처리
	                    alarm.getExpired()                     // 만료 여부
	            ))
	            .collect(Collectors.toList());

	    // 페이지네이션 정보를 포함하여 PagedResponse 객체 생성 후 반환
	    return new PagedResponse<>(
	            alarms,
	            alarmEntities.getNumber(),         // 현재 페이지 번호
	            alarmEntities.getSize(),           // 페이지 크기
	            alarmEntities.getTotalPages(),     // 총 페이지 수
	            alarmEntities.getTotalElements(),  // 총 알림 개수
	            alarmEntities.isLast()             // 마지막 페이지 여부
	    );
	}


	@Transactional
	public List<AlarmEntity> unReadAlarmList(Long loggedId) {
		// loggedId가 member1 또는 member2인 알람들을 조회
		List<AlarmEntity> alarms = this.findAllAboutLoggedId(loggedId);

		// 필터링: 읽지 않은 알람 && 해당 사용자가 볼 수 있는 알람만
		List<AlarmEntity> filteredAlarms = alarms.stream()
				.filter(alarm -> (alarm.getMember1() != null && alarm.getMember1().getId().equals(loggedId)
						&& "UNREAD".equals(alarm.getMember1Read()) && Boolean.TRUE.equals(alarm.getMember1Visible()))
						|| (alarm.getMember2() != null && alarm.getMember2().getId().equals(loggedId)
								&& "UNREAD".equals(alarm.getMember2Read())
								&& Boolean.TRUE.equals(alarm.getMember2Visible())))
				.collect(Collectors.toList());
		alarmRepository.flush(); // DB에 즉시 반영

		return filteredAlarms;
	}

	@Transactional
	public AlarmDTO findTradeAlarmService(Long roomId, Long loggedId) {
		ChattingRoomEntity chattingRoomEntity = chattingRoomRepository.findById(roomId).orElse(null);
		if (chattingRoomEntity == null) {
			return null;
		}

		BoardEntity boardEntity = chattingRoomEntity.getBoardEntity();

		// member1, member2 초기화
		final MemberEntity member1;
		final MemberEntity member2;

		if (chattingRoomEntity.getMember1().getId().equals(loggedId)) {
			member1 = chattingRoomEntity.getMember1();
			member2 = chattingRoomEntity.getMember2();
		} else if (chattingRoomEntity.getMember2().getId().equals(loggedId)) {
			member1 = chattingRoomEntity.getMember2();
			member2 = chattingRoomEntity.getMember1();
		} else {
			return null; // 예외 처리: 사용자가 채팅방의 멤버가 아닐 경우
		}

		List<AlarmEntity> alarmEntities = this.findAllAboutLoggedId(loggedId);

		// 최신 알람 찾기
		return alarmEntities.stream().filter(alarm -> alarm.getType().equals("TRADE"))
				.filter(alarm -> (alarm.getMember1().equals(member1) && alarm.getMember2().equals(member2))
						|| (alarm.getMember2().equals(member1) && alarm.getMember1().equals(member2)))
				.filter(alarm -> alarm.getObject().equals(String.valueOf(boardEntity.getId())))
				.filter(alarm -> alarm.getAction().equals("상대방 동의 확인") || alarm.getAction().equals("예약") )
				.max(Comparator.comparing(AlarmEntity::getCreateTime)) // 최신 알람 찾기
				.map(AlarmEntity::toDTO).orElse(null);

	}

	@Transactional
	public Boolean readHiddenAlarm(Long alarmId, Long loggedId) {
		Boolean result = false;
		AlarmEntity alarmEntity = alarmRepository.findById(alarmId).get();
		String alarmContent = null;
		String readStatus = null;
		
		
		if (loggedId.equals(alarmEntity.getMember1().getId())) {
			alarmContent = alarmEntity.getMember1Content();
			if (alarmContent.contains("숨김") && alarmEntity.getMember1Read().equals("UNREAD")) {
				alarmEntity.setMember1Read("READ");
				result = true;
			}

		} else {
			alarmContent = alarmEntity.getMember2Content();
			if (alarmContent.contains("숨김") && alarmEntity.getMember2Read().equals("UNREAD")) {
				alarmEntity.setMember2Read("READ");
				result = true;
			}

		}

		return result;
	}
	
	
	
	
	
	
	
	// action: 예약,상대방 동의 확인, 예약거절 , 거래거절, 거래취소, 
		// 상대방 동의 확인, 예약 action을 가질 경우 이 알람과 동일한 알람들을 expired 할것
		
//		거래,예약을 취소, 거절할 경우  지난 "알람" 목록들 중 거래요청을 수락을 하면 수락이 되는 버그가 있음
	
	
	
	
	//예약 신청, 거래 신청을 받는 알람이 있을 떄, 최근 알람 제외하고는 만료하기
	//setIncludeAlarmId : 만료대상을 알람 본인도 포함을 시킬 건지?
	@Transactional
	public Boolean setExpiredAlarm(Long alarmId, boolean setIncludeAlarmId) {
	    Optional<AlarmEntity> alarmEntityOpt = alarmRepository.findById(alarmId);

	    // 존재하지 않는 경우 false 반환
	    if (!alarmEntityOpt.isPresent()) {
	        return false;
	    }
	    	
	    AlarmEntity alarmEntity = alarmEntityOpt.get();
	    
	    if(!alarmEntity.getType().equals("TRADE")) {
	    	return false;
	    }
	    
	    MemberEntity member1 = alarmEntity.getMember1();
	    MemberEntity member2 = alarmEntity.getMember2();
	    String alarmObject = alarmEntity.getObject();
	    Long boardId = Long.valueOf(alarmObject);

	    BoardEntity boardEntity = boardRepository.findById(boardId).orElse(null);

//	    // 거래가 존재하면 false 반환
//	    if (boardEntity == null || !boardEntity.getTrades().isEmpty()) {
//	        return false;
//	    }
	    
	    // 거래가 존재하면 false 반환
	    
	    
	    if (boardEntity == null || !boardEntity.getTrades().isEmpty()) {
	        
	    	
	    	return false;
	    }

	    // 특정 조건을 만족하는 알람만 조회
	    List<AlarmEntity> alarms = alarmRepository.findByMembersAndObject(member1, member2, alarmObject);

	    // 필터링
	    List<AlarmEntity> filteredAlarms = alarms.stream()
	        .filter(alarm -> 
	            (alarm.getAction().equals("상대방 동의 확인") || alarm.getAction().equals("예약") || alarm.getAction().equals("거래 완료 확인"))
	            && Boolean.FALSE.equals(alarm.getExpired()) // 이미 만료된 알람 제외
	            && !alarm.getId().equals(alarmId)									//해당 alarmId (최근) 제외
	        )
	        .collect(Collectors.toList());

	    if(filteredAlarms.size()  == 0) {
	    	return false;
	    }

	    for (AlarmEntity alarm : filteredAlarms) { 
	            alarm.setExpired(Boolean.TRUE);
	    }

	    // 변경된 알람 저장
	    alarmRepository.saveAll(filteredAlarms);

	  return true;
	}
	
	
	
	//거래 완료 신청 알람이 여러 개일 경우 최신 알람을 제외하고 만료된 정보 출력 
	
	@Transactional
	public Boolean setExpiredCompleteAlarm(Long alarmId, boolean setIncludeAlarmId) {
	    Optional<AlarmEntity> alarmEntityOpt = alarmRepository.findById(alarmId);

	    // 존재하지 않는 경우 false 반환
	    if (!alarmEntityOpt.isPresent()) {
	        return false;
	    }
	    	
	    AlarmEntity alarmEntity = alarmEntityOpt.get();
	    
	    if(!alarmEntity.getType().equals("TRADE")) {
	    	return false;
	    }
	    
	    MemberEntity member1 = alarmEntity.getMember1();
	    MemberEntity member2 = alarmEntity.getMember2();
	    String alarmObject = alarmEntity.getObject();
	    Long boardId = Long.valueOf(alarmObject);

	    BoardEntity boardEntity = boardRepository.findById(boardId).orElse(null);

//	    // 거래가 존재하면 false 반환
//	    if (boardEntity == null || !boardEntity.getTrades().isEmpty()) {
//	        return false;
//	    }
	    

	    // 특정 조건을 만족하는 알람만 조회
	    List<AlarmEntity> alarms = alarmRepository.findByMembersAndObject(member1, member2, alarmObject);

	    // 필터링
	    List<AlarmEntity> filteredAlarms = alarms.stream()
	        .filter(alarm -> 
	            ( alarm.getAction().equals("거래 완료 확인"))
	            && Boolean.FALSE.equals(alarm.getExpired()) // 이미 만료된 알람 제외
	            && !alarm.getId().equals(alarmId)									//해당 alarmId (최근) 제외
	        )
	        .collect(Collectors.toList());

	    if(filteredAlarms.size()  == 0) {
	    	return false;
	    }

	    for (AlarmEntity alarm : filteredAlarms) { 
	            alarm.setExpired(Boolean.TRUE);
	    }

	    // 변경된 알람 저장
	    alarmRepository.saveAll(filteredAlarms);

	  return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	

	@Transactional		//현재 특정 alarm을 expired
	public void setExpiredCurrentAlarm(Long alarmId) {
		AlarmEntity alarmEntity =alarmRepository.findById(alarmId).orElse(null);
		
		if(alarmEntity.getExpired().equals(Boolean.FALSE)) {
		alarmEntity.setExpired(Boolean.TRUE);	
		alarmRepository.save(alarmEntity);
		alarmRepository.flush();
		}
		
	}
	
	
	
	
	//예약,등록한 경우 다른 방의 신청 알람,메시지 만료시키기
	
	@Transactional
	public List<AlarmEntity> setOtherAlarmExpired(BoardEntity boardEntity, ChattingRoomEntity tradedRoom) {

		if(boardEntity == null) {
			return null;
		}
		
		List<TradeEntity> trades =  boardEntity.getTrades();
		if(trades == null) {
			return null;
		}
		
		
		
		

		
				MemberEntity member1 = tradedRoom.getMember1(); 
				MemberEntity member2 = tradedRoom.getMember2(); 
				
				
			
				
				
		
		List<AlarmEntity> alarms = alarmRepository.findByObject(String.valueOf(boardEntity.getId()));		//보드에 관한 알람
		
		if(alarms.size() == 0) {
			return null;
		}
		
		List<AlarmEntity> notTradedAlarms = alarms.stream()
			    .filter(alarm ->
			        alarm.getType().equals("TRADE") &&
			        (alarm.getAction().equals("상대방 동의 확인") || alarm.getAction().equals("예약")) &&
			        !(Objects.equals(alarm.getMember1(), member1) && Objects.equals(alarm.getMember2(), member2)) &&		//Objects 비교 : 비교대상이 null일 경우 nullPointException 을 발생시키지 않고 false로 리턴
			        !(Objects.equals(alarm.getMember1(), member2) && Objects.equals(alarm.getMember2(), member1)) &&
			        Boolean.FALSE.equals(alarm.getExpired()) // Boolean 비교는 equals 사용
			    )
			    .collect(Collectors.toList()); // 필터링된 알람 리스트 반환

			// 거래되지 못한 알람들 expired 설정 (Dirty Checking 사용)
			notTradedAlarms.forEach(alarm ->
			alarm.setExpired(Boolean.TRUE)
			
					);
			alarmRepository.saveAllAndFlush(notTradedAlarms);
		
//			for(AlarmEntity notTradedAlarm : notTradedAlarms ) {
//				notTradedAlarm.setExpired(Boolean.TRUE);
//				ChattingRoomEntity  notTradedRoom = chattingRoomRepository.findEnableRoom(notTradedAlarm.getMember1().getId(), notTradedAlarm.getMember2().getId(), Long.valueOf(notTradedAlarm.getObject()));
//				chatService.setMessageExpired(notTradedRoom);
//			}
		
		return notTradedAlarms;
		
	}
	
	
	
	
	
	//거래 완료가 되었을 경우 TRADE 연관 알람 모두 expired (원치않는 알람까지 expired 되므로 쓰지 않음)
//@Transactional
//	public void setExpiredAllAlarm(MemberEntity member1, MemberEntity member2, Long boardId) {
//			List<AlarmEntity> alarms = alarmRepository.findByMembersAndObject(member1, member2, String.valueOf(boardId));
//				
//			alarms.stream().filter(alarm -> alarm.getExpired().equals(Boolean.FALSE) && alarm.getType().equals("TRADE"))
//					.forEach(alarm -> alarm.setExpired(Boolean.TRUE));
//	
//			alarmRepository.saveAll(alarms);
//	}


	
	
	//최근 거래 완료 확인 하나만 expired 설정
@Transactional
public void setExpiredAllAlarm(MemberEntity member1, MemberEntity member2, Long boardId) {
    List<AlarmEntity> alarms = alarmRepository.findByMembersAndObject(member1, member2, String.valueOf(boardId));

    alarms.stream()
        .filter(alarm -> Boolean.FALSE.equals(alarm.getExpired()) && "거래 완료 확인".equals(alarm.getAction()))
        .findFirst()
        .ifPresent(alarm -> { // ✅ Optional 값이 존재할 경우에만 실행
            alarm.setExpired(Boolean.TRUE); // ✅ 상태 변경 (Dirty Checking 작동)
        });
}


//거래가 취소될 경우 기존 신청했던 알람 목록 조회
@Transactional
public List<AlarmEntity> setOtherRoomsEnableTrade(BoardEntity boardEntity, ChattingRoomEntity chattingRoomEntity2) {
	
		List<AlarmEntity> alarmsAboutBoard = alarmRepository.findByObject(String.valueOf(boardEntity.getId()));
	
		MemberEntity member1 = chattingRoomEntity2.getMember1();
		MemberEntity member2 = chattingRoomEntity2.getMember2();		// 보드 게시자
		
		MemberEntity boardWriter = boardEntity.getMemberEntity();
		
			List<AlarmEntity> noTradedchattingRooms = alarmsAboutBoard.stream().filter(
					alarm -> (
						    alarm.getType().equals("TRADE") &&
					        (alarm.getAction().equals("상대방 동의 확인") || alarm.getAction().equals("예약")) 
					        && Boolean.TRUE.equals(alarm.getExpired()) 
					        &&
					        !(Objects.equals(alarm.getMember1(), member1) && Objects.equals(alarm.getMember2(), member2)) &&		//Objects 비교 : 비교대상이 null일 경우 nullPointException 을 발생시키지 않고 false로 리턴
					        !(Objects.equals(alarm.getMember1(), member2) && Objects.equals(alarm.getMember2(), member1))
					      
					)
					).collect(Collectors.toList());
					System.out.println("여기까지 가나1");
	return noTradedchattingRooms;
}




	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
