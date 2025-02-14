package com.cos.project.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.aspectj.weaver.ast.Instanceof;
import org.springframework.stereotype.Service;

import com.cos.project.dto.AlarmDTO;
import com.cos.project.entity.AlarmEntity;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.ChattingRoomEntity;
import com.cos.project.entity.CommentLikeEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.entity.MessageEntity;
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
	public Long findCount (Long loggedId) {

		// member1나 member2가 loggedId인 알림 목록을 출력
		List<AlarmEntity> alarms = alarmRepository.findByLoggedId(loggedId);

		return alarms.isEmpty() ? 0 : (long)alarms.size();
	}
	
	
	
	

	
	@Transactional  // 알람 등록
	public AlarmEntity postAlarm(Long loggedId, Long member1Id, Long member2Id, String type, String childType, String object, String action, String priority) {
	    AlarmDTO alarmDTO = null;
	    AlarmEntity alarmEntity = null;
	    
	    // Case 1: When both member1Id and member2Id are null, it's a login alarm
	    if (member1Id == null && member2Id == null) {
	        member1Id = loggedId;
	        alarmDTO = alarmConstructor(loggedId, member1Id, member2Id, type, childType, object, action, priority);
	        MemberEntity member1 = memberRepository.findById(member1Id).orElseThrow(() -> new IllegalAccessError("사용자를 조회할 수 없습니다"));
	        MemberEntity member2 = null;  // No member2 in this case (login alarm)
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
	            // You can handle any additional cases where neither member1 nor member2 matches the logged-in user
	            throw new IllegalArgumentException("Logged user doesn't match any member IDs.");
	        }
	    }
	    
	    // Save the alarm entity to the database
	    alarmRepository.saveAndFlush(alarmEntity);
	    return alarmEntity;
	}


	@Transactional // 알림 숨김 (삭제)
	public boolean hideAlarm(Long alarmId, Long loggedId, Long member1Id, Long member2Id) {
		MemberEntity member1 = memberRepository.findById(member1Id).get();
		MemberEntity member2 = memberRepository.findById(member2Id).get();
		AlarmEntity alarmEntity = alarmRepository.findById(alarmId).get(); // 알람조회
		if (loggedId.equals(member1Id)) {
			alarmEntity.setMember1Visible(false);
		} else if (loggedId.equals(member2Id)) {
			alarmEntity.setMember2Visible(false);
		}

		alarmRepository.saveAndFlush(alarmEntity);

		return true;
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


	@Transactional // 알림 읽음
	public void readAlarm(Long alarmId, Long loggedId, Long member1Id, Long member2Id) {
		MemberEntity member1 = memberRepository.findById(member1Id).get();
		MemberEntity member2 = memberRepository.findById(member2Id).get();
		AlarmEntity alarmEntity = alarmRepository.findById(alarmId).get(); // 알람조회
		if (loggedId.equals(member1Id)) {
			alarmEntity.setMember1Read("READ");
		} else if (loggedId.equals(member2Id)) {
			alarmEntity.setMember2Read("READ");
		}
		alarmRepository.saveAndFlush(alarmEntity);

	}
	
	
	
	@Transactional
	public AlarmDTO alarmConstructor(Long loggedId,Long member1Id, Long member2Id, String type, String childType, String object, String action, String priority) {
		Optional<MemberEntity> member1 = memberRepository.findById(member1Id);
		Optional<MemberEntity> member2 = null;
//		Long Object_id = null;
		if(member2Id != null) {
			member2 = memberRepository.findById(member2Id);
		}
		String member1Content= "";
		String member2Content = "";  
		
		Boolean member1Visible =true;
		Boolean member2Visible=true;
		
		if( type.equals("LOGIN")) {
			member1Content="로그인에 성공하였습니다";
			member2Visible=false;
		}
	
		
		else if (type.equals("BOARD")) { // 게시판
			if (childType.equals("게시판")  && action.equals("등록")) {
				List <BoardEntity> boards = boardRepository.findByMemberID(member1Id);
				BoardEntity filteredBoard = boards.stream().filter(board -> board.getMemberEntity().getId().equals(member1Id))
						.max(Comparator.comparing(BoardEntity :: getCreateTime))
						.get();
				
				if(!filteredBoard.getId().equals(null) ) {
				member1Content = member1.get().getUserid()+"님이 "+filteredBoard.getId() +"번 게시물을 등록하였습니다";
				}
			} else if (childType.equals("게시판") && action.equals("삭제")) {
				member1Content = member1.get().getUserid() + "님의 "+ object +"번 게시글이 삭제되었습니다";
			} else if (childType.equals("게시판") &&  action.equals("수정")) {
				member1Content = member1.get().getUserid() + "님의 "+object +"번 게시글이 수정되었습니다";
			}else if (childType.equals("게시판") && object.equals("좋아요")   && action.equals("활성화")) {
				member1Content = member1.get().getUserid()+"님이" + member2.get().getUserid() + "님의 게시글을 좋아합니다";
				member2Content = member2.get().getUserid() + "님의 게시글에 좋아요를 눌렀습니다";
			}else if (childType.equals("게시판") && object.equals("좋아요")   && action.equals("취소")) {
				member1Content = member1.get().getUserid()+"님이" + member2.get().getUserid() + "님의 게시글의 좋아요를 취소하였습니다";
				member2Content = member2.get().getUserid() + "님의 게시글에 좋아요를 취소하였습니다";
			}else if (childType.equals("게시판") && object.equals("싫어요")   && action.equals("활성화")) {
				member1Content = member1.get().getUserid()+"님이" + member2.get().getUserid() + "님의 게시글을 싫어합니다";
				member2Content = member2.get().getUserid() + "님의 게시글에 싫어요를 눌렀습니다";
			}else if (childType.equals("게시판") && object.equals("싫어요")   && action.equals("취소")) {
				member1Content = member1.get().getUserid()+"님이" + member2.get().getUserid() + "님의 게시글의 싫어요를 취소하였습니다";
				member2Content = member2.get().getUserid() + "님의 게시글에 싫어요를 취소하였습니다";
			}
			
			else if (childType.equals("댓글")  && action.equals("등록")) {//member1, member2 의 정보를 다 받아와야 함
				member1Content = member1.get().getUserid() + " 님의 댓글이 등록되었습니다";
				member2Content = member1.get().getUserid() + " 님의 댓글이 "+member2.get().getUserid()+"님의 "+object+"번 게시글에 등록되었습니다";
				if(member1Id.equals(member2Id)) {
					member1Content = member1.get().getUserid() + " 님의 댓글이 등록되었습니다";
					member2Content =   member1.get().getUserid() + " 님의 댓글이 등록되었습니다";
				}
			
			} else if (childType.equals("댓글")  && action.equals("삭제")) {
				member1Content = member1.get().getUserid() + " 님의 댓글이 삭제되었습니다";
				member2Content = member1.get().getUserid() + " 님의 댓글이 "+member2.get().getUserid()+"님의 "+ object+"번 게시글에서 삭제되었습니다";
				if(member1Id.equals(member2Id)) {
					member1Content = member1.get().getUserid() + " 님의 댓글이 삭제되었습니다";
					member2Content =   member1.get().getUserid() + " 님의 댓글이 삭제되었습니다";
				}
			
			} else if (childType.equals("댓글")  && action.equals("수정")) {
				member1Content = member1.get().getUserid() + " 님의 댓글이 수정되었습니다";
				member2Content = member1.get().getUserid() + " 님의 댓글이 수정되었습니다";
			} else if (childType.equals("댓글")  && object.equals("좋아요")&& action.equals("활성화")) {		//3자
				member1Content = member2.get().getUserid() + "님의 댓글에 좋아요를 눌렀습니다";
				member2Content = member1.get().getUserid()+" 님이" + member2.get().getUserid() + "님의 댓글의 좋아요를 눌렀습니다";
			}else if (childType.equals("댓글") && object.equals("좋아요")   && action.equals("취소")) {
				member1Content = member2.get().getUserid() + "님의 댓글에 좋아요를 취소하였습니다";
				member2Content = member1.get().getUserid()+" 님이" + member2.get().getUserid() + "님의 댓글의 좋아요를 취소하였습니다";

			}else if (childType.equals("댓글") && object.equals("싫어요")   && action.equals("활성화")) {
				member1Content = member2.get().getUserid() + "님의 댓글에 싫어요를 눌렀습니다";
				member2Content = member1.get().getUserid()+" 님이" + member2.get().getUserid() + "님의 댓글을 싫어합니다";

			}else if (childType.equals("댓글") && object.equals("싫어요")   && action.equals("취소")) {
				member1Content = member1.get().getUserid() + " 님의 댓글에 싫어요를 취소하였습니다";
				member2Content = member1.get().getUserid()+" 님이" + member2.get().getUserid() + "님의 댓글의 싫어요를 취소하였습니다";
			}
			
			else if (childType.equals("댓글")  && action.equals("블라인드")) {
				member1Content = member2.get().getUserid()+"  님의 댓글을 블라인드 하였습니다";
				member2Content =  member1.get().getUserid() + " 님이 " +member2.get().getUserid()+"  님의 "+object+"번 댓글을 블라인드하였습니다";
			}else if (childType.equals("댓글")   && action.equals("블라인드 취소")) {
				member1Content = member2.get().getUserid()+"  님의 댓글을 블라인드 해제하였습니다";
				member2Content = member1.get().getUserid()+" 님이 " + member2.get().getUserid() +"  님의 "+object+"번 댓글을 블라인드 해제하였습니다";
			}
			
			
			else if (childType.equals("대댓글")  && action.equals("등록")) {//member1, member2 의 정보를 다 받아와야 함
				member1Content = member1.get().getUserid() + "님의 댓글이 등록되었습니다";
				member2Content = member1.get().getUserid() + "님이 "+member2.get().getUserid()+"님의 댓글에 답하였습니다";
			} else if (childType.equals("대댓글")  && action.equals("삭제")) {
				member1Content = member1.get().getUserid()  + "님의 댓글이 삭제되었습니다";
				member2Content = member1.get().getUserid() + "님의 댓글이 "+member2.get().getUserid()+"님의 댓글에서 삭제되었습니다";
				member2Visible = false;
			} else if (childType.equals("대댓글")  && action.equals("수정")) {
				member1Content = member1.get().getUserid() + "님의 댓글이 수정되었습니다";
				member2Content = member1.get().getUserid() + "님의 댓글이 수정되었습니다";
			} else if (childType.equals("대댓글")  && object.equals("좋아요")&& action.equals("활성화")) {		//3자
				member1Content = member2.get().getUserid() + "님의 댓글에 좋아요를 눌렀습니다";
				member2Content = member1.get().getUserid()+"님이 " + member2.get().getUserid() + "님의 댓글의 좋아요를 눌렀습니다";
				
			}else if (childType.equals("대댓글") && object.equals("좋아요")   && action.equals("취소")) {
				member1Content = member2.get().getUserid() + "님의 댓글에 좋아요를 취소하였습니다";
				member2Content = member1.get().getUserid()+"님이 " + member2.get().getUserid() + "님의 댓글의 좋아요를 취소하였습니다";
			}else if (childType.equals("대댓글") && object.equals("싫어요")   && action.equals("활성화")) {
				member1Content = member2.get().getUserid() + "님의 댓글에 싫어요를 눌렀습니다";
				member2Content = member1.get().getUserid()+"님이 " + member2.get().getUserid() + "님의 댓글을 싫어합니다";

			}else if (childType.equals("대댓글") && object.equals("싫어요")   && action.equals("취소")) {
				member1Content = member2.get().getUserid() + "님의 댓글에 싫어요를 취소하였습니다";
				member2Content = member1.get().getUserid()+"님이 " + member2.get().getUserid() + "님의 댓글의 싫어요를 취소하였습니다";
				
			} 
			

		}
		
		
		
		
		else if (type.equals("MESSAGE")) { // 메시지
			if (childType.equals("채팅방") && action.equals("채팅방 만듬")) {
				member1Content = member1.get().getUserid() +"님과 " +member2.get().getUserid()+"님의 채팅방이 등록되었습니다";
				member2Content =  member2.get().getUserid() +" 님과 " +member1.get().getUserid()+"님의 채팅방이 등록되었습니다";
			} else if (childType.equals("채팅방") && action.equals("나가기")) {
				member1Content =  member1.get().getUserid() +"님과 " +member2.get().getUserid()+"님의  채팅방을 나갔습니다";
				member2Content =  member1.get().getUserid() +"님이 "+object+"번 채팅창을 나갔습니다";
			}else if (childType.equals("채팅방") && action.equals("완전삭제")) {
					member1Content = member1Content = member1.get().getUserid() +"님과 " +member2.get().getUserid()+"님의 채팅창이 삭제되었습니다";
					member2Content = member1Content = member2.get().getUserid() +"님과 " +member1.get().getUserid()+"님의 채팅창이 삭제되었습니다";
					member2Visible = false;
				}  else if (childType.equals("메시지") && action.equals("송수신")) {
				ChattingRoomEntity chattingRoomEntity =chattingRoomRepository.findById(Long.valueOf(object)).orElse(null);
					
					member1Content =  member2.get().getUserid() +"님에게 메시지를 보냈습니다"; 
				member2Content = "새로운 메시지 : "+member1.get().getUserid();
				
				if(chattingRoomEntity.getExitedmemberId() == member2Id) {
					member1Content =  "⚠️"+member2.get().getUserid() +"님은 이미 나간 유저이므로 메시지를 보낼 수 없습니다";
				member2Visible= false;
				}
			}
//		else if (childType.equals("메시지") && action.equals("삭제")) {
//			member1Content = member1.get().getUserid()+"님의 메시지가 삭제되었습니다";
//			member2Content = member2.get().getUserid() + "님의 게시글이 삭제되었습니다";
//		}
			
//			else if (childType.equals("메시지") && object.equals("좋아요")   && action.equals("활성화")) {
//				member1Content = member2.get().getUserid()+"님이" + member1.get().getUserid() + "님의 메시지를 좋아합니다";
//				member2Content = member1.get().getUserid() + "님의 메시지에 좋아요를 눌렀습니다";
//			}else if (childType.equals("메시지") && object.equals("좋아요")   && action.equals("취소")) {
//				member1Content = member2.get().getUserid()+"님이" + member1.get().getUserid() + "님의 게시글의 메시지를 취소하였습니다";
//				member2Content = member1.get().getUserid() + "님의 메시지에 좋아요를 취소하였습니다";
//			}
			


		}
		
		
		if(member1Id.equals(member2Id)) {	//메시지가 양쪽 다 뜨는 것을 방지
			member2Visible = false;
		}
		
		
		AlarmDTO alarmDTO = new AlarmDTO().builder()
				.action(action)
				.type(childType)
				.object(object)
				.type(type)
				.childType(childType)
				.member1Id(member1Id)
				.member2Id(member2Id)
				.member1Content(member1Content)
				.member2Content(member2Content)
				.member1Visible(member1Visible)
				.member2Visible(member2Visible)
				.priority("MEDIUM")
				.build();
		
		return alarmDTO;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
